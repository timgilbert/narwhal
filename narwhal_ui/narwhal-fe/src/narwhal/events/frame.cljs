(ns narwhal.events.frame
  (:require [narwhal.views.grid :as grid]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [re-graph.core :as re-graph]))

;; Frame events

(rf/reg-event-fx
  :frame-edit/blank
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame/blank}]]]}))

(rf/reg-event-fx
  :frame-edit/random
  (fn [_ _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame/random}]]]}))

(rf/reg-event-db
  :frame/new-frame
  (fn [db [_ data]]
    (assoc-in db [::frames util/default-frame-name] data)))

(rf/reg-event-fx
  :frame/create-scratch
  (fn [{:keys [db]} [_ {:page/keys [active title slug]}]]
    (when (nil? (get-in db [::frames util/default-frame-name]))
      {:fx [[:dispatch [:frame-edit/blank]]]})))

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
  (fn [db _]
    (get-in db [::frames util/default-frame-name :pixels])))

(rf/reg-event-fx
  :grid/click
  (fn [{:keys [db]} [_ index]]
    (let [color (active-color db)
          next (case (active-tool db)
                 :tools/pencil [:pencil/click color index]
                 :tools/bucket [:bucket/click color index])]
      (if next {:dispatch next}
               {}))))

(defn pencil-click [db color index]
  (assoc-in db [::frames util/default-frame-name :pixels index] color))

(defn bucket-click [db color index]
  (let [frame (get-in db [::frames util/default-frame-name])
        total (* (:height frame) (:width frame))
        pixels (into [] (repeat total color))]
    (assoc-in db [::frames util/default-frame-name :pixels] pixels)))

(rf/reg-event-db
  :pencil/click
  (fn [db [_ color index]] (pencil-click db color index)))

(rf/reg-event-db
  :bucket/click
  (fn [db [_ color index]] (bucket-click db color index)))

