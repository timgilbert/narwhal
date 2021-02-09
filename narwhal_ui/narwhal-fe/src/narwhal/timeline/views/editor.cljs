(ns narwhal.timeline.views.editor
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.events :as events]
            [narwhal.timeline.subs :as subs]
            [narwhal.timeline.views.effects :as effects]
            [narwhal.components.persist :as persist]
            [narwhal.components.name-edit :as name-edit]))

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

(defn step-display [timeline-id step-index step]
  [:div
   (for [[eff-index effect] (map-indexed vector (:effects step))]
     ^{key eff-index}
     [effects/effect-display timeline-id eff-index effect])])

(defn no-steps-message [timeline-id]
  [:p "No steps yet!"])

(defn step-list [timeline-id]
  (let [steps (<sub [::subs/timeline-steps timeline-id])]
    (if (empty? steps)
      [no-steps-message]
      [:div
       (for [[step-index step] (map-indexed vector steps)]
         ^{key step-index}
         [step-display timeline-id step-index step])])))

(defn step-controls [timeline-id]
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [::events/add-step timeline-id])}
   "Add Step"])

(defn timeline-editor [timeline-id]
  [:div
   [timeline-name-controls timeline-id]
   [step-list timeline-id]
   [step-controls timeline-id]
   ;[effects/effect-chooser timeline-id 0]
   [timeline-persist-controls timeline-id]])

(defn timeline-edit-page
  [route]
  (let [timeline-id (-> route :path-params :timeline-id)]
    (if (<sub [::subs/timeline-exists? timeline-id])
      [timeline-editor timeline-id]
      [component/error-page "Can't find timeline-id " timeline-id])))
