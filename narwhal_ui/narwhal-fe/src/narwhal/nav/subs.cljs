(ns narwhal.nav.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.nav.db :as db]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.router.subs :as router-subs]
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
  ::active-frame-id
  :<- [::router-subs/current-route]
  (fn [route]
    (some-> route :path-params :frame-id)))

(rf/reg-sub
  ::active-timeline-id
  :<- [::router-subs/current-route]
  (fn [route]
    (some-> route :path-params :timeline-id)))

(rf/reg-sub
  ::frame-id-active?
  :<- [::active-frame-id]
  (fn [active-id [_ this-id]]
    (= this-id active-id)))

(rf/reg-sub
  ::page-type?
  :<- [::router-subs/current-page]
  (fn [current-page [_ page-type]]
    (= (some-> current-page namespace) (name page-type))))

;; ----------------------------------------------------------------------
;; Frames
(rf/reg-sub
  ::frames
  (fn [_ _]
    [(rf/subscribe [::frame-subs/all-frame-metadata])
     (rf/subscribe [::page-type? :frame])
     (rf/subscribe [::active-frame-id])])
  ;
  ;:<- [::frame-subs/all-frame-metadata]
  ;:<- [::page-type? :frame]
  ;:<- [::active-frame-id]
  (fn [[frames frame-page? active-id] _]
    (for [frame frames
          :let [item-id (:id frame)
                active? (= item-id active-id)]]
      {::active?  active?
       ::item-id  item-id
       ::scratch? (:scratch? frame false)
       ::item     frame})))

;; ----------------------------------------------------------------------
;; Timelines
;; TODO: this and ::frames should be generalizable, right?
(rf/reg-sub
  ::timelines
  :<- [::timeline-subs/all-timelines]
  :<- [::page-type? :timeline]
  :<- [::active-timeline-id]
  (fn [[timelines active-page? active-id] _]
    (for [item timelines
          :let [item-id (:id item)
                active? (and active-page? (= item-id active-id))]]
      {::active? active?
       ::item-id item-id
       ::item    item})))
