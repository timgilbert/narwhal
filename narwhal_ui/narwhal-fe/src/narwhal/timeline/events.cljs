(ns narwhal.timeline.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]))

(rf/reg-event-db
  ::update-title
  (fn [db [_ timeline-id {:keys [values] :as evt}]]
    (let [new-name (:name values)
          old-name (db/timeline-name db timeline-id)
          changed? (and (not= "" new-name) (not= new-name old-name))]
      (if changed?
        (-> db
            (db/set-dirty timeline-id)
            (db/set-timeline-name timeline-id new-name))
        db))))

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
  ::select-saved-frame-target
  (fn [db [_ timeline-id step-index effect-index frame-id]]
    (db/replace-saved-frame-target-id db timeline-id step-index effect-index
                                      frame-id)))

;; Effect frame targets

(rf/reg-event-db
  ::choose-effect-type
  (fn [db [_ timeline-id step-index effect-index eff-type]]
    (db/replace-effect-type db timeline-id step-index effect-index eff-type)))

(rf/reg-event-db
  ::choose-target-type
  (fn [db [_ timeline-id step-index effect-index target-type]]
    (log/spy [timeline-id step-index effect-index target-type])
    (db/replace-frame-target-type db timeline-id step-index effect-index
                                  target-type)))

;; Steps

(rf/reg-event-db
  ::add-step
  (fn [db [_ timeline-id]]
    (let [timeline     (db/timeline-by-id db timeline-id)
          new-step     (db/new-blank-step db)
          new-timeline (update-in timeline [:timeline :steps] conj new-step)]
      (-> db
          (db/replace-single-timeline new-timeline)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::save-step-update
  (fn [db [_ timeline-id step-index]]
    (db/update-step db timeline-id step-index)))

;; Timeline persistence

(rf/reg-event-fx
  ::create-timeline
  (fn [{:keys [db]} [_ timeline-id]]
    (let [timeline (db/timeline-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/create-timeline
                  (dissoc timeline :id :scratch?)]})))

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

(rf/reg-event-fx
  ::update-timeline
  (fn [{:keys [db]} [_ timeline-id]]
    (let [timeline (db/timeline-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/update-timeline timeline]})))

(rf/reg-event-db
  :timeline-gql/timeline-updated
  (fn [db [_ payload]]
    (let [timeline-id (-> payload :timeline :id)]
      (assert (some? (db/timeline-by-id db timeline-id)))
      (log/info "Updated timeline" timeline-id)
      (-> db
          (db/set-clean timeline-id)
          (db/replace-all-timelines (:allTimelines payload))))))

;; Delete a timeline
(rf/reg-event-fx
  ::delete-timeline
  (fn [_ [_ timeline-id]]
    {:dispatch [:graphql/run :timeline-gql/delete-timeline {:id timeline-id}]}))

;; TODO: second delete crashes this with a re-graph error, why?
(rf/reg-event-fx
  :timeline-gql/timeline-deleted
  (fn [{:keys [db]} [_ payload]]
    (log/info "Deleted timeline" (:timelineId payload))
    {:db       (db/replace-all-timelines db (:allTimelines payload))
     :dispatch [:route/nav :timeline-page/list]}))

;; Revert an edited timeline back to its saved version
(rf/reg-event-fx
  ::revert-timeline
  (fn [_ [_ timeline-id]]
    (log/debug "Revert!" timeline-id)
    {:dispatch [:graphql/run :timeline-gql/get-timeline-by-id timeline-id]}))

(rf/reg-event-db
  :timeline-gql/timeline-reverted
  (fn [db [_ payload]]
    (let [timeline-id (-> payload :id)]
      (assert (some? (db/timeline-by-id db timeline-id)))
      (log/info "Reverted timeline" timeline-id)
      (-> db
          (db/set-clean timeline-id)
          (db/replace-single-timeline payload)))))

