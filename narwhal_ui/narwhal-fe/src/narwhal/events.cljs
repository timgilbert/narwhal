(ns narwhal.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db :initialize-db
  (fn [_db _]
    {:page/active :home/home}))

(rf/reg-event-db :route/go
  (fn [db [_ {:keys [page slug]}]]
    (assoc db :page/active page
              :page/slug slug)))
