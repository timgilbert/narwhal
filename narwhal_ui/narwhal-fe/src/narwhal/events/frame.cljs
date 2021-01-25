(ns narwhal.events.frame
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.grid.views :as grid]
            [narwhal.util :as util :refer [<sub >evt]]))


;; temp timeline
(rf/reg-sub
  :timeline/all-timelines
  (constantly []))

;; nav helpers
(defn set-saved-frames [db frame-list]
  (let [frames-by-id (into {}
                           (for [f frame-list]
                             [(:id f) f]))]
    (assoc-in db [::frames ::named] frames-by-id)))

;; Frame events
(defn active-frame-id [db]
  ;; TODO: check :page/slug etc
  (get-in db [::frames ::active-frame-id] util/default-frame-id))

(defn frame-by-id [db frame-id]
  (get-in db [::frames ::named frame-id]))

(defn pixel-by-frame-id [db frame-id pixel-index]
  (get-in (frame-by-id db frame-id) [:frame :pixels pixel-index]))

(defn active-frame [db]
  (frame-by-id db (active-frame-id db)))

(rf/reg-sub
  :frame/active-frame-id
  (fn [db _] (active-frame-id db)))

(rf/reg-sub
  ::frames-by-id
  (fn [db _] (get-in db [::frames ::named])))

(rf/reg-sub
  :frame/frame-by-id
  :<- [::frames-by-id]
  (fn [frames [_ frame-id]] (get frames frame-id)))

(rf/reg-sub
  :frame/all-frames
  :<- [::frames-by-id]
  (fn [frames _]
    ;(js/console.log "frames" frames)
    (->> frames
         (map second)
         (sort-by :name)
         (into []))))

(rf/reg-sub
  :frame/active-frame
  :<- [::frames-by-id]
  :<- [:frame/active-frame-id]
  (fn [[frames active-id] _]
    (log/debug "[frames active-id]" [frames active-id])
    (get frames active-id)))

(rf/reg-sub
  :frame/frame-name
  :<- [::frames-by-id]
  (fn [frames [_ frame-id]]
    ;; TODO: fix up
    (-> frames (get frame-id) (get :name))))

(rf/reg-sub
  :frame/active-frame-name
  (fn [db _]
    ;; TODO: fix up
    util/default-frame-name))

(rf/reg-sub
  :frame/dirty?
  (fn [db _]
    (get-in db [::frames ::dirty?])))

(rf/reg-event-fx
  :frame-edit/blank
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame-gql/blank}]]]}))

(rf/reg-event-fx
  :frame-edit/random
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame-gql/random}]]]}))

(defn with-blank-metadata [frame-data]
  (merge {:id    util/default-frame-id
          :name  util/default-frame-name
          :frame frame-data}))

(rf/reg-event-db
  :frame-gql/frame-loaded
  (fn [db [_ frame-data]]
    (log/debug "frame-data" frame-data)
    (log/debug "meta" (with-blank-metadata frame-data))
    (assoc-in db [::frames ::named util/default-frame-id]
              (with-blank-metadata frame-data))))

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
    (let [frame     (frame-by-id db frame-id)
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

;; Frame lists


(rf/reg-sub
  :grid/pixels
  :<- [:frame/active-frame]
  (fn [active-frame _]
    (log/debug "active-frame" active-frame)
    (get-in active-frame [:frame :pixels])))
