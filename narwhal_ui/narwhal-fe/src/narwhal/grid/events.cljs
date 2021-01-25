(ns narwhal.grid.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.grid.db :as db]))

;; ----------------------------------------------------------------------
;; Tool stuff

(rf/reg-event-db
  ::set-active-tool
  (fn [db [_ tool]]
    (db/set-active-tool db tool)))

;; ----------------------------------------------------------------------
;; Palette events

(rf/reg-event-db
  :palette/set-active-color
  (fn [db [_ color]]
    (db/set-active-palette-color db color)))

;; ----------------------------------------------------------------------
;; Grid interaction
(rf/reg-event-fx
  ::click
  (fn [{:keys [db]} [_ frame-id index]]
    (let [color (db/active-color db)
          next  (case (db/active-tool db)
                  :tools/pencil [::pencil-click frame-id index color]
                  :tools/bucket [::bucket-click frame-id index color])]
      (if next {:dispatch next}
               {}))))

(rf/reg-event-db
  ::pencil-click
  (fn [db [_ frame-id index color]]
    (db/pencil-click db frame-id index color)))

(rf/reg-event-db
  ::bucket-click
  (fn [db [_ frame-id index color]]
    (db/bucket-click db frame-id index color)))

