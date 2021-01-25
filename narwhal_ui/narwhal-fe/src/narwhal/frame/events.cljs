(ns narwhal.frame.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]))

;; ----------------------------------------------------------------------
;; Pages and slugs

(rf/reg-event-fx
  :frame-edit/blank
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame-gql/blank}]]]}))

(rf/reg-event-fx
  :frame-edit/random
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame-gql/random}]]]}))

(rf/reg-event-db
  :frame-gql/frame-loaded
  (fn [db [_ frame-data]]
    (log/debug "frame-data" frame-data)
    (log/debug "meta" (db/with-blank-metadata frame-data))
    (assoc-in db [::frames ::named util/default-frame-id]
              (db/with-blank-metadata frame-data))))

(rf/reg-event-fx
  :frame/create-scratch
  (fn [{:keys [db]} [_ {:page/keys [active title slug]}]]
    (when (nil? (get-in db [::frames ::named util/default-frame-id]))
      {:fx [[:dispatch [:frame-edit/blank]]]})))

;; Called when the user has submitted an update to the frame-name form
;; NB: Should probably validate versus the list of named frames
(rf/reg-event-db
  :frame/update-title
  (fn [db [_ frame-id {:keys [values] :as evt}]]
    (let [new-name (:name values)
          ;; TODO: Figure out a better way to deal with the namespaces below
          new-db   (dissoc db :narwhal.views.frame/frame-edit?)]
      (if (not= new-name "")
        (-> new-db
            (assoc-in [::frames ::named frame-id :name] new-name)
            (assoc-in [::frames ::dirty?] true))
        new-db))))

(rf/reg-event-fx
  :frame/save-frame
  (fn [{:keys [db]} [_ frame-id]]
    (let [frame     (db/frame-by-id db frame-id)
          gql-query (if (= frame-id util/default-frame-id)
                      :frame-gql/create-frame :frame-gql/update-frame)
          args      #:graphql{:query gql-query
                              :vars  {:i (dissoc frame :id)}}]
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

(rf/reg-event-fx
  :frame/revert-frame
  (fn [{:keys [db]} [_ frame-id]]
    (log/debug "Revert" frame-id)
    {}))
