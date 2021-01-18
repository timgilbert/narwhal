(ns narwhal.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :page/active
  (fn [db _]
    (:page/active db)))
