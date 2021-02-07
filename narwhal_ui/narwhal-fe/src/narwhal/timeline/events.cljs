(ns narwhal.timeline.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]))

;; New timeline
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

(rf/reg-event-db
  ::choose-effect
  (fn [db [_ timeline-id step effect-id]]
    (assoc-in db (db/timeline-path :t/edit timeline-id step :t/selected-effect)
              effect-id)))

;; Timeline persistence
(rf/reg-event-fx
  ::create-timeline
  (fn [{:keys [db]} [_ timeline-id]]
    (let [frame (db/timeline-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/create-timeline
                  (dissoc frame :id :scratch?)]})))

(rf/reg-event-fx
  :timeline-gql/timeline-created
  (fn [{:keys [db]} [_ data]]
    (let [timeline-id (-> data :timeline :id)]
      (log/debug "Created timeline!" data)
      {:db       (-> db
                     (db/set-clean timeline-id)
                     (db/replace-all-timelines (:allTimelines data)))
       :dispatch [:route/nav :timeline-page/edit
                  {:timeline-id timeline-id}]})))

;; Save an update to an existing frame
(rf/reg-event-fx
  ::update-timeline
  (fn [{:keys [db]} [_ timeline-id]]
    (let [frame (db/timeline-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/update-timeline frame]})))

(rf/reg-event-db
  :timeline-gql/timeline-reverted
  (fn [db [_ payload]]
    (let [timeline-id (-> payload :id)]
      (assert (some? (db/timeline-by-id db timeline-id)))
      (log/info "Reverted frame" timeline-id)
      (-> db
          (db/set-clean timeline-id)
          (db/replace-single-timeline payload)))))

(rf/reg-event-db
  :timeline-gql/timeline-updated
  (fn [db [_ payload]]
    (let [timeline-id (-> payload :timeline :id)]
      (assert (some? (db/timeline-by-id db timeline-id)))
      (log/info "Updated frame" timeline-id)
      (-> db
          (db/set-clean timeline-id)
          (db/replace-all-timelines (:allTimelines payload))))))
