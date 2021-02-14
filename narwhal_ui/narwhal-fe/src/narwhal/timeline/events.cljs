(ns narwhal.timeline.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]
            [narwhal.util.edit-state :as edit]))

;; Edit state

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

(rf/reg-event-db
  ::toggle-repeat
  (fn [db [_ timeline-id]]
    (-> db
        (db/set-dirty timeline-id)
        (db/replace-single-timeline
          (-> (db/timeline-meta-by-id db timeline-id)
              (update-in [:timeline :isRepeat] not))))))

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

;; TODO: just alter type here
(rf/reg-event-fx
  ::choose-effect-type
  (fn [{:keys [db]} [_ timeline-id step-index effect-index eff-type]]
    {:db       (-> db
                   (db/replace-effect-type
                     timeline-id step-index effect-index eff-type)
                   (db/set-dirty timeline-id))
     :dispatch [::edit/clear-editing ::edit/timeline]}))

(rf/reg-event-fx
  ::choose-target-type
  (fn [{:keys [db]} [_ timeline-id step-index effect-index target-type]]
    {:db       (-> db
                   (db/replace-frame-target-type
                     timeline-id step-index effect-index target-type)
                   (db/set-dirty timeline-id))
     :dispatch [::edit/clear-editing ::edit/timeline]}))

;; Effects

(rf/reg-event-db
  ::insert-effect
  (fn [db [_ timeline-id step-index effect-index]]
    (-> db
        (db/insert-effect timeline-id step-index effect-index)
        (db/set-dirty timeline-id))))

(rf/reg-event-db
  ::insert-step
  (fn [db [_ timeline-id step-index]]
    (-> db
        (db/insert-step timeline-id step-index)
        (db/set-dirty timeline-id))))

(rf/reg-event-db
  ::set-step-pause-ms
  (fn [db [_ [timeline-id step-index] value]]
    (let [step (-> db
                   (db/get-step timeline-id step-index)
                   (assoc :pauseMs value))]
      (-> db
          (db/assoc-step timeline-id step-index step)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::set-step-repetitions
  (fn [db [_ [timeline-id step-index] value]]
    (let [step (-> db
                   (db/get-step timeline-id step-index)
                   (assoc :repetitions value))]
      (-> db
          (db/assoc-step timeline-id step-index step)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::set-effect-pause-ms
  (fn [db [_ [timeline-id step-index effect-index] value]]
    (let [effect (-> db
                     (db/get-effect timeline-id step-index effect-index)
                     (assoc :pauseMs value))]
      (-> db
          (db/assoc-effect timeline-id step-index effect-index effect)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::set-effect-duration-ms
  (fn [db [_ [timeline-id step-index effect-index] value]]
    (let [effect (-> db
                     (db/get-effect timeline-id step-index effect-index)
                     (assoc :durationMs value))]
      (-> db
          (db/assoc-effect timeline-id step-index effect-index effect)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::set-effect-granularity
  (fn [db [_ [timeline-id step-index effect-index] value]]
    (let [effect (-> db
                     (db/get-effect timeline-id step-index effect-index)
                     (assoc :granularity value))]
      (-> db
          (db/assoc-effect timeline-id step-index effect-index effect)
          (db/set-dirty timeline-id)))))

(rf/reg-event-db
  ::set-solid-frame-color
  (fn [db [_ [timeline-id step-index effect-index] color]]
    (let [effect (-> db
                     (db/get-effect timeline-id step-index effect-index)
                     (assoc-in [:target :color] color))]
      (-> db
          (db/assoc-effect timeline-id step-index effect-index effect)
          (db/set-dirty timeline-id)))))

;; Steps

(rf/reg-event-db
  ::add-step
  (fn [db [_ timeline-id]]
    (let [timeline     (db/timeline-by-id db timeline-id)
          new-step     (db/new-blank-step db)
          new-timeline (update timeline :steps conj new-step)]
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
    (let [timeline-meta (db/timeline-meta-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/create-timeline
                  (db/dehydrate timeline-meta ::db/create)]})))

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
    (let [timeline-meta (db/timeline-meta-by-id db timeline-id)]
      {:dispatch [:graphql/run :timeline-gql/update-timeline
                  (db/dehydrate timeline-meta)]})))

(rf/reg-event-db
  :timeline-gql/timeline-updated
  (fn [db [_ payload]]
    (let [timeline-id (-> payload :timeline :id)]
      (assert (some? (db/timeline-meta-by-id db timeline-id)))
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
      (assert (some? (db/timeline-meta-by-id db timeline-id)))
      (log/info "Reverted timeline" timeline-id)
      (-> db
          (db/set-clean timeline-id)
          (db/replace-single-timeline payload)))))

