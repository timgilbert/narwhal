(ns narwhal.frame.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]
            [narwhal.grid.db :as grid-db]))

;; ----------------------------------------------------------------------
;; Title stuff

;; Called when the user has submitted an update to the frame-name form
;; NB: Should probably validate versus the list of named frames
(rf/reg-event-db
  ::update-title
  (fn [db [_ frame-id {:keys [values] :as evt}]]
    (let [new-name (:name values)
          old-name (db/frame-name db frame-id)
          changed? (and (not= "" new-name) (not= new-name old-name))]
      (if changed?
        (-> db
            (db/set-dirty frame-id)
            (db/set-frame-name frame-id new-name))
        db))))

;; ----------------------------------------------------------------------
;; Wipe frame (refactor)
(rf/reg-event-fx
  ::new-blank-frame
  (fn [_ _]
    {:dispatch [:graphql/run :frame-gql/new-blank-frame]}))

(rf/reg-event-db
  ::randomize-frame
  (fn [db [_ frame-id]]
    (grid-db/randomize-grid db frame-id)))

;; TODO: instead of this, randomize colors via grid controls
(rf/reg-event-fx
  ::new-random-frame
  (fn [_ _]
    {:dispatch [:graphql/run :frame-gql/new-random-frame]}))

;; GraphQL return event from either of the above
(rf/reg-event-fx
  :frame-gql/scratch-frame-loaded
  (fn [{:keys [db]} [_ frame-data]]
    (let [new-frame    (db/with-scratch-metadata db frame-data)
          new-frame-id (:id new-frame)]
      {:dispatch [:route/nav :frame-page/edit {:frame-id new-frame-id}]
       :db       (-> db
                     (db/replace-single-frame new-frame)
                     (db/set-dirty new-frame-id))})))

;; ----------------------------------------------------------------------
;; Frame persistence
(rf/reg-event-fx
  ::create-frame
  (fn [{:keys [db]} [_ frame-id]]
    (let [frame (db/frame-by-id db frame-id)]
      {:dispatch [:graphql/run :frame-gql/create-frame
                  (dissoc frame :id :scratch?)]})))

(rf/reg-event-fx
  :frame-gql/frame-created
  (fn [{:keys [db]} [_ data]]
    (let [new-frame-id (-> data :frame :id)]
      (log/debug "Created!" data)
      ;; Update saved frame list in db
      ;; Redirect to edit page for new ID
      {:db       (-> db
                     (db/set-clean new-frame-id)
                     (db/replace-all-frames (:allFrames data)))
       :dispatch [:route/nav :frame-page/edit {:frame-id new-frame-id}]})))

;; Save an update to an existing frame
(rf/reg-event-fx
  ::save-frame
  (fn [{:keys [db]} [_ frame-id]]
    (let [frame (db/frame-by-id db frame-id)]
      {:dispatch [:graphql/run :frame-gql/update-frame frame]})))

(rf/reg-event-db
  :frame-gql/frame-reverted
  (fn [db [_ payload]]
    (let [frame-id (-> payload :id)]
      (assert (some? (db/frame-by-id db frame-id)))
      (log/info "Reverted frame" frame-id)
      (-> db
          (db/set-clean frame-id)
          (db/replace-single-frame payload)))))

(rf/reg-event-db
  :frame-gql/frame-updated
  (fn [db [_ payload]]
    (let [frame-id (-> payload :frame :id)]
      (assert (some? (db/frame-by-id db frame-id)))
      (log/info "Updated frame" frame-id)
      (-> db
          (db/set-clean frame-id)
          (db/replace-all-frames (:allFrames payload))))))

;; Delete a frame
(rf/reg-event-fx
  ::delete-frame
  (fn [_ [_ frame-id]]
    {:dispatch [:graphql/run :frame-gql/delete-frame {:id frame-id}]}))

;; TODO: second delete crashes this with a re-graph error, why?
(rf/reg-event-fx
  :frame-gql/frame-deleted
  (fn [{:keys [db]} [_ payload]]
    (log/info "Deleted frame" (:frameId payload))
    {:db       (db/replace-all-frames db (:allFrames payload))
     :dispatch [:route/nav :frame-page/list]}))

;; Revert an edited frame back to its saved version
(rf/reg-event-fx
  ::revert-frame
  (fn [_ [_ frame-id]]
    (log/debug "Revert!" frame-id)
    {:dispatch [:graphql/run :frame-gql/get-frame-by-id frame-id]}))
