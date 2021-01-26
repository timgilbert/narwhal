(ns narwhal.frame.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]))

;; ----------------------------------------------------------------------
;; Frame stuff

(rf/reg-sub
  ::frame-root
  (fn [db _] (get-in db (db/frame-path :f/all))))

(rf/reg-sub
  ::all-frames
  :<- [::frame-root]
  (fn [root _]
    (->> root
         vals
         (sort-by :name))))             ; TODO: more sort options

(rf/reg-sub
  ::frame
  :<- [::frame-root]
  (fn [root [_ frame-id]]
    (get root frame-id)))

(rf/reg-sub
  ::scratch?
  (fn [[_ frame-id]]
    (rf/subscribe [::frame frame-id]))
  (fn [frame]
    (let [frame-id (:id frame)]
      (or (nil? frame-id) (= frame-id util/default-frame-id)))))

(rf/reg-sub
  ::dirty-root
  (fn [db _]
    (get-in db (db/frame-path :f/dirty?))))

(rf/reg-sub
  ::dirty?
  :<- [::dirty-root]
  (fn [dirty-root [_ frame-id]]
    (get dirty-root frame-id false)))

;; ----------------------------------------------------------------------
;; Title stuff
(rf/reg-sub
  ::editing-title?
  (fn [db [_ frame-id]]
    (get-in db (db/frame-path :f/editing? frame-id) false)))

(rf/reg-sub
  ::frame-name
  (fn [[_ frame-id]]
    (rf/subscribe [::frame frame-id]))
  (fn [frame]
    (:name frame)))
