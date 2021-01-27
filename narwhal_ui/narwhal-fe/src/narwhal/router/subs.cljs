(ns narwhal.router.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]))

(rf/reg-sub
  ::current-route
  (fn [db]
    (:nav/current-route db)))

(rf/reg-sub
  ::current-page
  :<- [::current-route]
  (fn [route]
    (some-> route :data :name)))