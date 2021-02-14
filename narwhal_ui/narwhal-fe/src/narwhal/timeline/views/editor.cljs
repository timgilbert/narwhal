(ns narwhal.timeline.views.editor
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.events :as events]
            [narwhal.timeline.subs :as subs]
            [narwhal.timeline.views.effects :as effects]
            [narwhal.components.persist :as persist]
            [narwhal.components.name-edit :as name-edit]
            [narwhal.timeline.db :as db]
            [fork.re-frame :as fork]))

(defn timeline-persist-controls [timeline-id]
  [persist/persist-controls
   #:persist{:item-id      timeline-id
             :name-type    ::timeline
             :scratch-sub  ::subs/scratch?
             :clean-sub    ::subs/clean?
             :update-event ::events/update-timeline
             :create-event ::events/create-timeline
             :delete-event ::events/delete-timeline
             :revert-event ::events/revert-timeline}])

(defn timeline-name-controls [timeline-id]
  [name-edit/editable-name
   #:name-edit{:item-id     timeline-id
               :type        ::name-edit/timeline
               :name-sub    ::subs/timeline-name
               :dirty-sub   ::subs/dirty?
               :scratch-sub ::subs/scratch?
               :on-submit   [::events/update-title timeline-id]}])

(defn no-effects-message [timeline-id step-index]
  [:div
   [:p "No effects yet!"]])

(defn step-pause-control
  [{:keys [props] :as fork-props}]
  [:div
   [component/number-control fork-props
    #:component{:field-name :pauseMs
                :blur-event ::events/set-step-pause-ms
                :event-args (::addr props)
                :label      "Step Pause (ms)"
                :tooltip    (str "title: Pause for this many milliseconds "
                                 "in between each repetition of this step"
                                 "; pos: top")}]])

(defn step-repetitions-control
  [{:keys [props] :as fork-props}]
  [:div
   [component/number-control fork-props
    #:component{:field-name :repetitions
                :blur-event ::events/set-step-repetitions
                :event-args (::addr props)
                :label      "Repetitions"
                :minimum    1
                :step       1
                :tooltip    (str "title: Number of times to repeat this "
                                 "step; pos: top")}]])

(defn all-step-controls [{:keys [] :as fork-props}]
  [:div.uk-grid.uk-child-width-1-5 {:data-uk-grid ""}
   [step-pause-control fork-props]
   [step-repetitions-control fork-props]])

(defn step-control-form
  [timeline-id step-index step]
  [:form.uk-form-stacked
   [fork/form
    {:props             {::addr [timeline-id step-index]}
     :initial-values    {:pauseMs     (:pauseMs step)
                         :repetitions (:repetitions step)}
     :prevent-default?  true
     :clean-on-unmount? true
     :keywordize-keys   true
     :path              [::edit-step-form timeline-id step-index]}
    all-step-controls]])

(defn step-controls [timeline-id step-index step]
  [:div
   [:hr]
   [:form]
   [:div
    [:p.uk-text-muted "Step controls"]
    [step-control-form timeline-id step-index step]]
   [:button.uk-button.uk-button-default
    {:on-click #(>evt [::events/insert-step timeline-id (inc step-index)])}
    "Add Step"]])

(defn step-display [timeline-id step-index step]
  (let [{:keys [effects]} step]
    [:div.uk-width-5-6
     (if (empty? effects)
       [no-effects-message timeline-id step-index]
       [:div
        [util/for-children effects
         [:div]
         [effects/effect-display timeline-id step-index]]
        [step-controls timeline-id step-index step]])]))

(defn no-steps-message [timeline-id]
  [:div
   [:p "No steps yet!"]
   [step-controls timeline-id 0]])

(defn step-repeat-display
  [timeline-id step-index {:keys [repetitions]}]
  [:div.uk-flex.uk-flex-column
   (if (= repetitions 1)
     [:div
      [component/icon "arrow-down"
       {:data-uk-tooltip "title: Repeats one time; pos: right"}]]
     [:div
      [component/icon "refresh"
       {:data-uk-tooltip (str "title: Repeats " repetitions
                              " times; pos: right")}]
      [:div repetitions]])])

(defn step-number [timeline-id step-index step]
  [:div.uk-width-1-6.uk-text-center
   [:div.uk-card.uk-card-default.uk-height-1-1
    [:h3.uk-text-center
     (inc step-index)]
    [step-repeat-display timeline-id step-index step]]])

(defn step-divider [total step-index step]
  (when (> total (inc step-index))
    [:hr.uk-width-1-1]))

(defn step-list [timeline-id]
  (let [steps (<sub [::subs/timeline-steps timeline-id])]
    (if (empty? steps)
      [no-steps-message timeline-id]
      [util/for-children steps
       [:div.uk-grid.uk-child-width-expand.uk-padding-small
        {:data-uk-grid ""}]
       [step-number timeline-id]
       [step-display timeline-id]
       [step-divider (count steps)]])))

(defn debug-view [timeline-id]
  (let [timeline-meta (<sub [::subs/timeline-meta-by-id timeline-id])
        hydrate-type  (if (<sub [::subs/scratch? timeline-id])
                        ::db/create
                        ::db/update)]
    [util/json-dump {:i (db/dehydrate timeline-meta hydrate-type)}]))

(defn toggle-button [timeline-id {:components/keys [active? icon]}]
  [component/icon icon])

(defn playback-toggle [timeline-id]
  (let [repeat? (<sub [::subs/timeline-repeat? timeline-id])
        icon    (if repeat? "refresh" "arrow-right")
        tooltip (if repeat?
                  "Repeats forever. Click to toggle to run once."
                  "Runs once. Click to toggle to repeat.")]
    [:div
     [component/icon icon
      {:on-click        #(>evt [::events/toggle-repeat timeline-id])
       :data-uk-tooltip (str "title:" tooltip ";pos: top")}]]))

(defn top-controls
  [timeline-id]
  [:div
   [:div.uk-flex.uk-flex-left.uk-flex-middle
    [playback-toggle timeline-id]
    [:div.uk-padding-small
     [timeline-name-controls timeline-id]]]])

(defn timeline-editor [timeline-id]
  [:div
   [top-controls timeline-id]
   [step-list timeline-id]
   [timeline-persist-controls timeline-id]
   [debug-view timeline-id]])

(defn timeline-edit-page
  [route]
  (let [timeline-id (-> route :path-params :timeline-id)
        exists?     (<sub [::subs/timeline-exists? timeline-id])
        loaded?     (<sub [:nav/loaded? timeline-id])]
    (cond
      exists?
      [timeline-editor timeline-id]
      loaded?
      [component/error-page "Can't find timeline-id " timeline-id]
      :else
      [component/spinner-page])))
