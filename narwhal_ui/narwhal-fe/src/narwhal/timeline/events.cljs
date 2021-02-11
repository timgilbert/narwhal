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
  ::choose-effect
  (fn [db [_ timeline-id step effect-id]]
    (assoc-in db (db/timeline-path :t/edit timeline-id step :t/selected-effect)
              effect-id)))

(rf/reg-event-db
  ::select-saved-frame-target
  (fn [db [_ timeline-id step frame-id]]
    (assoc-in db (db/timeline-path :t/edit timeline-id step
                                   :t/frame-target :t/saved)
              frame-id)))


;; Steps

(rf/reg-event-db
  ::add-step
  (fn [db [_ timeline-id]]
    (let [timeline (db/timeline-by-id db timeline-id)
          new-step (db/new-blank-step)
          new-timeline (update timeline :steps conj new-step)]
      (log/spy timeline-id)
      (log/spy new-timeline)
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

