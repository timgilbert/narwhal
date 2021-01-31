(ns narwhal.timeline.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]))

;; Wipe frame (refactor)
(rf/reg-event-fx
  ::new-empty-timeline
  (fn [_ _]
    {:dispatch [:graphql/run :timeline-gql/new-empty-timeline]}))

(rf/reg-event-fx
  :timeline-gql/empty-timeline-loaded
  (fn [{:keys [db]} [_ timeline-data]]
    (let [new-timeline    (db/with-scratch-metadata db timeline-data)
          new-timeline-id (:id new-timeline)]
      {:dispatch [:route/nav :timeline-page/edit {:timeline-id new-timeline-id}]
       :db       (-> db
                     (db/replace-single-timeline new-timeline)
                     (db/set-dirty new-timeline-id))})))

