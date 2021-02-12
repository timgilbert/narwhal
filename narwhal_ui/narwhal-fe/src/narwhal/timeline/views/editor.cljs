(ns narwhal.timeline.views.editor
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.events :as events]
            [narwhal.timeline.subs :as subs]
            [narwhal.timeline.views.effects :as effects]
            [narwhal.components.persist :as persist]
            [narwhal.components.name-edit :as name-edit]
            [narwhal.timeline.db :as db]))

(defn timeline-persist-controls [timeline-id]
  [persist/persist-controls
   #:persist{:item-id       timeline-id
             :name-type     ::timeline
             :scratch-sub   ::subs/scratch?
             :clean-sub     ::subs/clean?
             :update-event  ::events/update-timeline
             :create-event  ::events/create-timeline
             :delete-event  ::events/delete-timeline
             :revert-event  ::events/revert-timeline}])

(defn timeline-name-controls [timeline-id]
  [name-edit/editable-name
   #:name-edit{:item-id     timeline-id
               :type        ::name-edit/timeline
               :name-sub    ::subs/timeline-name
               :dirty-sub   ::subs/dirty?
               :scratch-sub ::subs/scratch?
               :on-submit   [::events/update-title timeline-id]}])

(defn add-effect-control [timeline-id step-index]
  [effects/effect-chooser timeline-id step-index])

(defn no-effects-message [_timeline-id _step-index]
  [:p "No effects yet!"])

(defn step-display [timeline-id step-index step]
  (let [{:keys [effects]} step]
    [:div
     (if (empty? effects)
       [no-effects-message timeline-id step-index]
       (for [[eff-index effect] (map-indexed vector effects)]
         ^{:key eff-index}
         [effects/effect-display timeline-id eff-index effect]))
     [add-effect-control timeline-id step-index]]))

(defn no-steps-message [_timeline-id]
  [:p "No steps yet!"])

(defn step-list [timeline-id]
  (let [steps (<sub [::subs/timeline-steps timeline-id])]
    (if (empty? steps)
      [no-steps-message timeline-id]
      [:div
       (for [[step-index step] (map-indexed vector steps)]
         ^{:key step-index}
         [step-display timeline-id step-index step])])))

(defn step-controls [timeline-id]
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [::events/add-step timeline-id])}
   "Add Step"])

(defn debug-view [timeline-id]
  (let [timeline-meta (<sub [::subs/timeline-meta-by-id timeline-id])
        hydrate-type (if (<sub [::subs/scratch? timeline-id])
                       ::db/create
                       ::db/update)]
    [util/json-dump {:i (db/dehydrate timeline-meta hydrate-type)}]))

(defn timeline-editor [timeline-id]
  [:div
   [timeline-name-controls timeline-id]
   [step-list timeline-id]
   [step-controls timeline-id]
   [timeline-persist-controls timeline-id]
   [debug-view timeline-id]])

(defn timeline-edit-page
  [route]
  (let [timeline-id (-> route :path-params :timeline-id)]
    (if (<sub [::subs/timeline-exists? timeline-id])
      [timeline-editor timeline-id]
      [component/error-page "Can't find timeline-id " timeline-id])))
