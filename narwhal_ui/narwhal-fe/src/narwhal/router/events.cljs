(ns narwhal.router.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as rfe]))

;; Triggering navigation from events.
(rf/reg-fx
  ::navigate!
  (fn [route]
    (apply rfe/push-state route)))

(rf/reg-event-fx
  :route/nav
  (fn [_cofx [_ & route]]
    {::navigate! route}))

(rf/reg-event-db
  ::navigated
  (fn [db [_ new-match]]
    (let [old-match   (::current-route db)
          controllers (rfc/apply-controllers (:controllers old-match)
                                             new-match)]
      (assoc db :nav/current-route
                (assoc new-match :controllers controllers)))))
