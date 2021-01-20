(ns narwhal.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :page/active
  (fn [db _]
    (get-in db [:nav/page :page/active])))

(rf/reg-sub :page/slug
  (fn [db _]
    (get-in db [:nav/page :page/slug])))

(rf/reg-sub :page/title
  (fn [db _]
    (get-in db [:nav/page :page/title])))

(rf/reg-sub :page/timeline?
  (fn [db _]
    (contains? {:timeline/new :timeline/edit}
               (get-in db [:nav/page :page/active]))))

(rf/reg-sub :page/frame?
  (fn [db _]
    (contains? {:frame/new :frame/edit}
               (get-in db [:nav/page :page/active]))))
