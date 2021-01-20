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
