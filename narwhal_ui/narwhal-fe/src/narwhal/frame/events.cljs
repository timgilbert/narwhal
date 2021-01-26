(ns narwhal.frame.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]))

;; ----------------------------------------------------------------------
;; Title stuff

;; Called when the user has submitted an update to the frame-name form
;; NB: Should probably validate versus the list of named frames
(rf/reg-event-db
  ::update-title
  (fn [db [_ frame-id {:keys [values] :as evt}]]
    (let [new-name (:name values)
          ;; TODO: Figure out a better way to deal with the namespaces below
          new-db   (update-in db (db/frame-path :f/editing?)
                              dissoc frame-id)]
      (if (not= new-name "")
        (-> new-db
            (db/set-dirty frame-id)
            (db/set-frame-name frame-id new-name))
        new-db))))

(rf/reg-event-db
  ::title-clicked
  (fn [db [_ frame-id]]
    (assoc-in db (db/frame-path :f/editing? frame-id) true)))

(rf/reg-event-db
  ::title-cancel-clicked
  (fn [db [_ frame-id]]
    (update-in db (db/frame-path :f/editing?) dissoc frame-id)))

;; ----------------------------------------------------------------------
;; Wipe frame (refactor)
(rf/reg-event-fx
  ::new-blank-frame
  (fn [_ _]
    {:dispatch [:graphql/query {:graphql/query
                                :frame-gql/new-blank-frame}]}))

;; TODO: instead of this, randomize colors via grid controls
(rf/reg-event-fx
  ::new-random-frame
  (fn [_ _]
    {:dispatch [:graphql/query {:graphql/query
                                :frame-gql/new-random-frame}]}))

;; GraphQL return event from either of the above
(rf/reg-event-fx
  :frame-gql/scratch-frame-loaded
  (fn [{:keys [db]} [_ frame-data]]
    (let [new-frame    (db/with-scratch-metadata frame-data)
          new-frame-id (:id new-frame)]
      (log/spy new-frame)
      {:dispatch [:route/navigate #:route{:page :frame-page/new}]
       :db       (-> db
                     (db/replace-single-frame new-frame)
                     (db/set-clean new-frame-id))})))

(rf/reg-event-fx
  :frame/create-scratch
  (fn [_ _]
    (log/debug :frame/create-scratch "Argh, get rid of me already")
    {}))

;; ----------------------------------------------------------------------
;; Frame persistence
(rf/reg-event-fx
  ::create-frame
  (fn [{:keys [db]} [_ frame-id]]
    (assert (= frame-id util/default-frame-id))
    (let [frame (db/frame-by-id db frame-id)
          args  #:graphql{:query :frame-gql/create-frame
                          :vars  {:i (dissoc frame :id :scratch?)}}]
      {:dispatch [:graphql/query args]})))

(rf/reg-event-fx
  :frame-gql/frame-created
  (fn [{:keys [db]} [_ data]]
    (let [new-frame-id (-> data :frame :id)]
      (log/debug "Created!" data)
      ;; Update saved frame list in db
      ;; Redirect to edit page for new ID
      {:db       (assoc-in db [:narwhal.events/nav :narwhal.events/frames]
                           (:allFrames data))
       :dispatch [:route/navigate #:route{:page :frame-page/edit
                                          :id   new-frame-id}]})))

;; Save an update to an existing frame
(rf/reg-event-fx
  ::save-frame
  (fn [{:keys [db]} [_ frame-id]]
    (let [frame (db/frame-by-id db frame-id)
          args  #:graphql{:query :frame-gql/update-frame
                          :vars  {:i frame}}]
      {:dispatch [:graphql/query args]})))

(rf/reg-event-db
  :frame-gql/frame-updated
  (fn [db [_ payload]]
    ;; Would be nice to assert on the returned id here or something
    (let [frame-id (-> payload :frame :id)]
      (log/info "Updated frame" frame-id)
      (-> db
          (db/set-clean frame-id)
          (db/replace-all-frames (:allFrames payload))))))

;; Delete a frame
(rf/reg-event-fx
  ::delete-frame
  (fn [_ [_ frame-id]]
    {:dispatch [:graphql/query
                #:graphql{:query :frame-gql/delete-frame
                          :vars  {:i {:id frame-id}}}]}))

;; TODO: second delete crashes this with a re-graph error, why?
(rf/reg-event-fx
  :frame-gql/frame-deleted
  (fn [db [_ payload]]
    (log/info "Deleted frame" (:frameId payload))
    {:db       (db/replace-all-frames db (:allFrames payload))
     ;; TODO: need a frame landing page
     :dispatch [:route/navigate #:route{:page :home-page/home}]}))

;; Revert an edited frame back to its saved version
(rf/reg-event-fx
  ::revert-frame
  (fn [{:keys [db]} [_ frame-id]]
    ;; TODO: implement me
    (log/debug "Revert!" frame-id)))
;{:dispatch [:graphql/query #:graphql{:query :frame-gql/get-frame-by-id
;                                     :vars  {:i frame-id}}]}))
