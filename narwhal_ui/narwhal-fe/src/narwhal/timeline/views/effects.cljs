(ns narwhal.timeline.views.effects
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.subs :as subs]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.timeline.events :as events]
            [narwhal.frame.views.list :as frame-list]
            [re-frame.core :as rf]
            [narwhal.util.color :as color]
            [narwhal.util.edit-state :as edit]
            [narwhal.grid.views :as grid]
            [fork.re-frame :as fork]))

(defn color-picker
  [{:keys [props values handle-change handle-blur]}]
  [:div
   [:label.uk-form-label {:for :color} "Choose Color"]
   [:input.uk-input
    {:type      "color"
     :name      :color
     :on-change (fn [js-evt]
                  (handle-change js-evt)
                  (>evt [::events/set-solid-frame-color (::addr props)
                         (fork/retrieve-event-value js-evt)]))
     :on-blur  (fn [js-evt]
                 (handle-blur js-evt)
                 (>evt [::edit/clear-editing ::edit/timeline]))
     :value    (get values :color)}]])

(defn solid-frame-target-editor
  [timeline-id step-index effect-index]
  (let [color (<sub [::subs/solid-frame-color
                     timeline-id step-index effect-index])]
    [:form.uk-form-stacked
     [fork/form
      {:props             {::addr [timeline-id step-index effect-index]}
       :initial-values    {:color color}
       :prevent-default?  true
       :clean-on-unmount? true
       :keywordize-keys   true
       :path              [::edit-color timeline-id step-index effect-index]}
      color-picker]]))

(defn random-frame-target-editor
  [_timeline-id _step-index _effect-index]
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [frame-list/random-frame-display true]])

(defn no-saved-frames-message []
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [:div.uk-card.uk-card-body.uk-card-small
    [:p "No saved frames available! Create one first"]]])

(defn saved-frame-target-editor
  [timeline-id step-index effect-index]
  [:div
   (let [frames      (<sub [::frame-subs/all-frame-metadata])
         curr-target (<sub [::subs/selected-saved-frame-target
                            timeline-id step-index effect-index])]
     (if (empty? frames)
       [no-saved-frames-message]
       [frame-list/frame-list
        #:frame-list{:active-id   curr-target
                     :click-event [::events/select-saved-frame-target
                                   timeline-id step-index effect-index]}]))])

(def frame-target-editors
  {"SOLID_FRAME"  {::editor   solid-frame-target-editor
                   ::tab-name "Solid Color"
                   ::icon     "paint-bucket"}
   "RANDOM_FRAME" {::editor   random-frame-target-editor
                   ::tab-name "Random Frame"
                   ::icon     "bolt"}
   "SAVED_FRAME"  {::editor   saved-frame-target-editor
                   ::tab-name "Saved Frame"
                   ::icon     "image"}
   ::all          ["RANDOM_FRAME" "SAVED_FRAME" "SOLID_FRAME"]})

;; TODO: disable frame tab if no saved frames
(defn frame-target-tab [timeline-id step-index effect-index target-type
                        {::keys [tab-name]}]
  (let [current (<sub [::subs/frame-target timeline-id
                       step-index effect-index])
        active? (= (:type current) target-type)]
    [:li (when active? {:class "uk-active"})
     (if active?
       [:a tab-name]
       [:a {:on-click #(>evt [::events/choose-target-type
                              timeline-id step-index effect-index
                              target-type])}
        tab-name])]))

(defn frame-target-editor [timeline-id step-index effect-index]
  (let [target (<sub [::subs/frame-target timeline-id step-index
                      effect-index])
        type   (:type target)
        {::keys [editor]} (get frame-target-editors type)]
    (if editor
      [editor timeline-id step-index effect-index]
      [:p (str "Can't find editor '" type "'!")])))

(defn frame-target-nav [timeline-id step-index effect-index]
  [:ul.uk-subnav.uk-subnav-pill
   (for [target-type (::all frame-target-editors)
         :let [props (get frame-target-editors target-type
                          (first frame-target-editors))]]
     ^{:key target-type}
     [frame-target-tab timeline-id step-index effect-index
      target-type props])])

(defn frame-target-chooser [timeline-id step-index effect-index]
  [:div
   [:p "Choose Frame Target"]
   [frame-target-nav timeline-id step-index effect-index]
   [frame-target-editor timeline-id step-index effect-index]])

(defn tween-controls [timeline-id step-index effect-index effect]
  [:p "tween-controls, granularity "
   (:granularity effect) ", durationMs " (:durationMs effect)])

(def effect-types
  {"REPLACE_EFFECT" {::text       "Replace"
                     ::tab-name   "Replace"
                     ::frame?     true
                     ::immediate? true
                     ::icon       "grid"}
   "TWEEN_EFFECT"   {::text       "Tween"
                     ::tab-name   "Tween"
                     ::frame?     true
                     ::icon       "move"
                     ::immediate? false
                     ::controls   tween-controls}
   ::all            ["REPLACE_EFFECT" "TWEEN_EFFECT"]})

(defn effect-type-controls [timeline-id step-index effect-index]
  (assert (some? timeline-id))
  (let [effect    (<sub [::subs/effect timeline-id step-index effect-index])
        curr-type (:type effect)]
    [:div
     [:ul.uk-subnav.uk-subnav-pill
      (for [eff-type (::all effect-types)
            :let [{::keys [text]} (get effect-types eff-type)
                  active? (= curr-type eff-type)]]
        ^{:key eff-type}
        [:li (when active? {:class "uk-active"})
         [:a
          {:on-click #(>evt [::events/choose-effect-type timeline-id
                             step-index effect-index eff-type])}
          text]])]
     (when-let [controls (get-in effect-types [curr-type ::controls])]
       [controls timeline-id step-index effect-index effect])
     (when (get-in effect-types [curr-type ::frame?])
       [frame-target-chooser timeline-id step-index effect-index])]))

(defn effect-editor [timeline-id step-index effect-index effect]
  [:div
   (assert (some? timeline-id))
   [effect-type-controls timeline-id step-index effect-index]])

(defn effect-thumb-effect-type-icon
  [timeline-id step-index effect-index effect]
  (let [config     (get effect-types (:type effect))
        icon-name  (get config ::icon "warning")
        edit-tuple [::edit/effect-type timeline-id
                    step-index effect-index]
        editing?   (<sub [::edit/editing? ::edit/timeline edit-tuple])
        show-props {:data-uk-tooltip (str "title: Click to edit effect type"
                                          "; pos: top")
                    :on-click        #(>evt [::edit/set-editing ::edit/timeline
                                             edit-tuple])}
        edit-props {:class           "uk-background-primary"
                    :data-uk-tooltip "title: Click to cancel; pos: top"
                    :on-click        #(>evt [::edit/clear-editing
                                             ::edit/timeline])}]
    [:div
     (if editing? edit-props show-props)
     [component/icon icon-name nil "1.5"]]))

(defn effect-thumb-effect-target-type-icon
  [timeline-id step-index effect-index effect]
  (let [config     (get frame-target-editors (-> effect :target :type))
        icon-name  (get config ::icon "ban")
        edit-tuple [::edit/target-type timeline-id
                    step-index effect-index]
        editing?   (<sub [::edit/editing? ::edit/timeline edit-tuple])
        show-props {:data-uk-tooltip (str "title: Click to edit target type"
                                          "; pos: bottom")
                    :on-click        #(>evt [::edit/set-editing ::edit/timeline
                                             edit-tuple])}
        edit-props {:class           "uk-background-primary"
                    :data-uk-tooltip "title: Click to cancel; pos: bottom"
                    :on-click        #(>evt [::edit/clear-editing ::edit/timeline])}]
    [:div
     (if editing? edit-props show-props)
     [component/icon icon-name nil "1.5"]]))

(defn effect-thumb-effect-target-type-editor
  [timeline-id step-index effect-index effect]
  (let [edit-tuple [::edit/target-type timeline-id
                    step-index effect-index]
        editing?   (<sub [::edit/editing? ::edit/timeline edit-tuple])
        current    (some-> effect :target :type)]
    (if-not editing?
      [:div]
      [:div
       [:div.uk-card.uk-card-default
        (for [t-type (::all frame-target-editors)
              :let [{::keys [icon tab-name]} (get frame-target-editors t-type)
                    active? (= t-type current)
                    event   (if active?
                              [::edit/clear-editing ::edit/timeline]
                              [::events/choose-target-type
                               timeline-id step-index effect-index
                               t-type])]]
          ^{:key t-type}
          [:div
           (when active? {:class "uk-background-primary"})
           [component/icon icon
            {:data-uk-tooltip (str "title: " tab-name "; pos: top")
             :on-click        #(>evt event)}
            "1.5"]])]])))

(defn effect-thumb-effect-type-editor
  [timeline-id step-index effect-index effect]
  (let [edit-tuple [::edit/effect-type timeline-id
                    step-index effect-index]
        editing?   (<sub [::edit/editing? ::edit/timeline edit-tuple])
        current    (some-> effect :type)]
    (if-not editing?
      [:div]
      [:div
       [:div.uk-card.uk-card-default
        (for [e-type (::all effect-types)
              :let [{::keys [icon tab-name]} (get effect-types e-type)
                    active? (= e-type current)
                    event   (if active?
                              [::edit/clear-editing ::edit/timeline]
                              [::events/choose-effect-type
                               timeline-id step-index effect-index
                               e-type])]]
          ^{:key e-type}
          [:div
           (when active? {:class "uk-background-primary"})
           [component/icon icon
            {:data-uk-tooltip (str "title: " tab-name "; pos: top")
             :on-click        #(>evt event)}
            "1.5"]])]])))

(defn effect-thumb-effect-icons
  [timeline-id step-index effect-index effect]
  [:div.uk-flex.uk-flex-column
   [effect-thumb-effect-type-icon timeline-id step-index effect-index effect]
   [effect-thumb-effect-target-type-icon
    timeline-id step-index effect-index effect]])

(defn effect-thumb-frame-target
  [timeline-id step-index effect-index {:keys [target]}]
  (let [editing?  (<sub [::edit/editing? ::edit/timeline
                         [::edit/target timeline-id step-index effect-index]])
        editable? (or (not= (:type target) "RANDOM_FRAME") editing?)]
    [:div.uk-padding-small
     (cond
       editing?
       {:data-uk-tooltip "title: Click to close; pos: top"
        :on-click        #(>evt [::edit/clear-editing ::edit/timeline])}
       editable?
       {:data-uk-tooltip "title: Click to edit target details; pos: top"
        :on-click        #(>evt [::edit/set-editing ::edit/timeline
                                 [::edit/target timeline-id step-index
                                  effect-index]])})
     (case (:type target)
       "RANDOM_FRAME"
       [grid/random-grid "60px"]
       "SOLID_FRAME"
       [grid/solid-grid "60px" (:color target)]
       "SAVED_FRAME"
       [grid/thumbnail-grid (:frameId target) "60px"]
       [:p "Unknown type " (:type target)])]))

(defn effect-thumb-pause-control
  [{:keys [props] :as fork-props}]
  ;; TODO: this should be click-to-edit
  [:div
   [component/number-control fork-props
    #:component{:field-name :pauseMs
                :blur-event ::events/set-effect-pause-ms
                :event-args (::addr props)
                :label      "Pause (ms)"
                :tooltip    (str "title: Pause for this many milliseconds "
                                 "after this event finishes; pos: top")}]])

(defn effect-thumb-duration-control
  [{:keys [props] :as fork-props}]
  [:div
   (when-not (-> props ::config ::immediate?)
     [component/number-control fork-props
      #:component{:field-name :durationMs
                  :blur-event ::events/set-effect-duration-ms
                  :event-args (::addr props)
                  :label      "Duration (ms)"
                  :tooltip    (str "title: This event will occur over this "
                                   "many milliseconds; pos: top")}])])

(defn effect-thumb-granularity-control
  [{:keys [props] :as fork-props}]
  [:div
   (when-not (-> props ::config ::immediate?)
     [component/number-control fork-props
      #:component{:field-name :granularity
                  :blur-event ::events/set-effect-granularity
                  :event-args (::addr props)
                  :label      "Granularity"
                  :minimum    1
                  :step       1
                  :tooltip    (str "title: The number of intermediate frames "
                                   "generated during the effect's duration; "
                                   "pos: top")}])])

(defn effect-thumb-effect-controls
  [{:keys [props] :as fork-props}]
  [:div {:data-uk-grid ""}
   [:div.uk-width-auto
    [effect-thumb-pause-control fork-props]]
   [:div.uk-width-auto
    [effect-thumb-duration-control fork-props]]
   [:div.uk-width-auto
    [effect-thumb-granularity-control fork-props]]
   [:div.uk-width-expand.uk-text-center]])
;[:p.uk-text-muted (str (::effect props))]]])

(defn effect-thumb-effect-form
  [timeline-id step-index effect-index effect]
  [:form.uk-form-stacked
   [fork/form
    {:props             {::addr   [timeline-id step-index effect-index]
                         ::effect effect
                         ::config (get effect-types (:type effect))}
     :initial-values    {:pauseMs     (:pauseMs effect)
                         :durationMs  (:durationMs effect)
                         :granularity (:granularity effect)}
     :prevent-default?  true
     :clean-on-unmount? true
     :keywordize-keys   true
     :path              [::edit-effect-form timeline-id step-index effect-index]}
    effect-thumb-effect-controls]])

(defn effect-insert-delete-controls
  [timeline-id step-index effect-index effect]
  [:div.uk-flex.uk-flex-column
   [component/icon "chevron-up"
    {:data-uk-tooltip "title: Insert effect above; pos: left"
     :on-click        #(>evt [::events/insert-effect timeline-id
                              step-index effect-index])}]
   [component/icon "trash"
    {:data-uk-tooltip "title: Delete effect; pos: left"
     :on-click        #(>evt [::events/delete-effect timeline-id
                              step-index effect-index])}]
   [component/icon "chevron-down"
    {:data-uk-tooltip "title: Insert effect below; pos: left"
     :on-click        #(>evt [::events/insert-effect timeline-id
                              step-index (inc effect-index)])}]])

(defn effect-detail-controls
  [timeline-id step-index effect-index effect]
  (let [frame-type (-> effect :target :type)
        config     (get frame-target-editors frame-type)
        control    (get config ::editor)]
    (if (and (<sub [::edit/editing? ::edit/timeline
                    [::edit/target timeline-id step-index effect-index]])
             (some? control))
      [control timeline-id step-index effect-index effect]
      [effect-thumb-effect-form timeline-id step-index effect-index effect])))

(defn effect-thumbnail [timeline-id step-index effect-index effect]
  [:div {:data-uk-grid ""}
   [:div.uk-width-auto.uk-text-center
    [:div.uk-flex.uk-flex-around.uk-flex-middle
     [effect-thumb-effect-icons timeline-id step-index effect-index effect]
     [effect-thumb-effect-target-type-editor
      timeline-id step-index effect-index effect]
     [effect-thumb-effect-type-editor
      timeline-id step-index effect-index effect]
     [effect-thumb-frame-target timeline-id step-index effect-index effect]]]
   [:div.uk-width-expand
    [effect-detail-controls timeline-id step-index effect-index effect]]
   [:div.uk-width-1-6.uk-text-center
    [effect-insert-delete-controls timeline-id step-index effect-index]]])


(defn effect-display
  [timeline-id step-index effect-index effect]
  (let [editing? (<sub [::subs/editing-effect? timeline-id
                        step-index effect-index])]
    (if editing?
      [effect-editor timeline-id step-index effect-index effect]
      [effect-thumbnail timeline-id step-index effect-index effect])))
