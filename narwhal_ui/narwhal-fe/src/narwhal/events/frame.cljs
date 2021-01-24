(ns narwhal.events.frame
  (:require [narwhal.views.grid :as grid]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [re-graph.core :as re-graph]
            [vimsical.re-frame.cofx.inject :as inject]))

;; Frame events
(defn active-frame-id [db]
  ;; TODO: check :page/slug etc
  (get-in db [::frames ::active-frame-id] util/default-frame-id))

(defn frame-by-id [db frame-id]
  (get-in db [::frames ::named frame-id]))

(defn pixel-by-frame-id [db frame-id pixel-index]
  (get-in (frame-by-id db frame-id) [:pixels pixel-index]))

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
  :frame/active-frame
  :<- [::frames-by-id]
  :<- [:frame/active-frame-id]
  (fn [[frames active-id] _]
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

(defn add-defaults [data]
  (merge {:id   util/default-frame-id
          :name util/default-frame-name}
         data))

(rf/reg-event-db
  :frame-gql/frame-loaded
  (fn [db [_ data]]
    (assoc-in db [::frames ::named util/default-frame-id]
              (add-defaults data))))

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
                      :frame-gql/create-frame :frame-gql/update-frame)]
      {:dispatch [:graphql/query #:graphql{:query gql-query
                                           :vars  {:frame    frame
                                                   :frame-id frame-id}}]})))

(rf/reg-event-fx
  :frame-gql/frame-created
  (fn [{:keys [db]} [_ data]]
    (js/console.log "Created!" data)
    ;; Update saved frame list in db
    ;; Redirect to edit page for new ID
    {}))

(rf/reg-event-fx
  :frame/revert-frame
  (fn [{:keys [db]} [_ frame-id]]
    (js/console.log "Revert" frame-id)
    {}))

;; Palette events

(rf/reg-event-db
  :palette/set-active-color
  (fn [db [_ color]]
    (assoc db :palette/active-color color)))

(defn active-color [db]
  (get db :palette/active-color util/black))

(rf/reg-sub
  :palette/active-color
  (fn [db _] (active-color db)))

;; Grid events - TODO: these probably can have their own namespace

(rf/reg-event-db
  :grid/set-active-tool
  (fn [db [_ tool]]
    (assoc db :grid/active-tool tool)))

(defn active-tool [db]
  (get db :grid/active-tool :tools/pencil))

(rf/reg-sub
  :grid/active-tool
  (fn [db _] (active-tool db)))

(rf/reg-sub
  :grid/pixels
  :<- [:frame/active-frame]
  (fn [active-frame _]
    (get active-frame :pixels)))

(rf/reg-event-fx
  :grid/click
  (fn [{:keys [db]} [_ frame-id index]]
    (let [color (active-color db)
          next  (case (active-tool db)
                  :tools/pencil [:pencil/click frame-id index color]
                  :tools/bucket [:bucket/click frame-id index color])]
      (if next {:dispatch next}
               {}))))

(defn pencil-click [db frame-id index color]
  (if (= (pixel-by-frame-id db frame-id index) color)
    db
    (-> db
        (assoc-in [::frames ::dirty?] true)
        (assoc-in [::frames ::named frame-id :pixels index] color))))

(defn bucket-click [db frame-id _index color]
  (let [frame  (frame-by-id db frame-id)
        total  (* (:height frame) (:width frame))
        pixels (into [] (repeat total color))]
    (-> db
        (assoc-in [::frames ::dirty?] true)
        (assoc-in [::frames ::named frame-id :pixels] pixels))))

(rf/reg-event-db
  :pencil/click
  (fn [db [_ frame-id index color]]
    (pencil-click db frame-id index color)))

(rf/reg-event-db
  :bucket/click
  (fn [db [_ frame-id index color]]
    (bucket-click db frame-id index color)))

