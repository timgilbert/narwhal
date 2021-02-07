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

(defn timeline-editor [timeline-id]
  [:div
   [timeline-name-controls timeline-id]
   [effects/effect-chooser timeline-id 0]
   [timeline-persist-controls timeline-id]])

(defn timeline-edit-page
  [route]
  (let [timeline-id (-> route :path-params :timeline-id)]
    (if (<sub [::subs/timeline-exists? timeline-id])
      [timeline-editor timeline-id]
      [component/error-page "Can't find timeline-id " timeline-id])))
