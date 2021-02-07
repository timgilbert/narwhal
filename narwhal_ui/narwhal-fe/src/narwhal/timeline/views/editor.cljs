(ns narwhal.timeline.views.editor
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.events :as events]
            [narwhal.timeline.subs :as subs]
            [narwhal.timeline.views.effects :as effects]
            [narwhal.components.persist :as persist]))

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

(defn timeline-editor [timeline-id]
  [:div
   [:h1 "Edit Timeline"]
   [:p "Editing: "
    timeline-id]
   [effects/effect-chooser timeline-id 0]
   [timeline-persist-controls timeline-id]])

(defn timeline-edit-page
  [route]
  (let [timeline-id (-> route :path-params :timeline-id)]
    (if (<sub [::subs/timeline-exists? timeline-id])
      [timeline-editor timeline-id]
      [component/error-page "Can't find timeline-id " timeline-id])))
