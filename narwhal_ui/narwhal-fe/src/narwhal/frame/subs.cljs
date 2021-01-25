(ns narwhal.frame.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]
            [narwhal.nav.subs :as nav-subs]))

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
         (sort-by :name)))) ; TODO: more sort options

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
    (get-in dirty-root frame-id false)))

;; ----------------------------------------------------------------------
;; Title stuff
(rf/reg-sub
  ::editing-title?
  (fn [db _]
    (get db (db/frame-path :f/editing?) false)))

(rf/reg-sub
  ::frame-name
  (fn [[_ frame-id]]
    (rf/subscribe [::frame frame-id]))
  (fn [frame]
    (:name frame)))

;; ----------------------------------------------------------------------
;; rework this

(rf/reg-sub
  :frame/active-frame-id
  :<- [::nav-subs/active-id]
  (fn [active-id _] active-id))

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

