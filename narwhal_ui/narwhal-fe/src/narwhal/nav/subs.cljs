(ns narwhal.nav.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.nav.db :as db]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.timeline.subs :as timeline-subs]))

;; ----------------------------------------------------------------------
;; Pages and slugs
(rf/reg-sub
  ::pages-root
  (fn [db _]
    (get-in db (db/page-path))))

(rf/reg-sub
  ::active-page
  :<- [::pages-root]
  (fn [root _]
    (:nav/page root)))

(rf/reg-sub
  ::active-id
  :<- [::pages-root]
  (fn [root _] (:nav/id root)))

(rf/reg-sub
  ::id-active?
  :<- [::active-id]
  (fn [active-id [_ this-id]]
    (= this-id active-id)))

(rf/reg-sub
  ::page-type?
  :<- [::active-page]
  (fn [active-page [_ page-type]]
    (= (namespace active-page) (name page-type))))

;; ----------------------------------------------------------------------
;; Frames
(rf/reg-sub
  ::frames
  :<- [::frame-subs/all-frames]
  :<- [::page-type? :frame]
  :<- [::active-id]
  (fn [[frames frame-page? active-id] _]
    (for [frame frames
          :let [item-id (:id frame)
                active? (and frame-page? (= item-id active-id))]]
      {::active?  active?
       ::item-id  item-id
       ::scratch? (:scratch? frame)
       ::item     frame})))

;; ----------------------------------------------------------------------
;; Timelines
;; TODO: this and ::frames should be generalizable, right?
(rf/reg-sub
  ::timelines
  :<- [::timeline-subs/all-timelines]
  :<- [::page-type? :timeline]
  :<- [::active-id]
  (fn [[timelines active-page? active-id] _]
    (for [item timelines
          :let [item-id (:id item)
                active? (and active-page? (= item-id active-id))]]
      {::active? active?
       ::item-id item-id
       ::item    item})))
