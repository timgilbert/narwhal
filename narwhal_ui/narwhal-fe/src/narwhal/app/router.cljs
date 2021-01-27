(ns narwhal.app.router
  (:require [lambdaisland.glogi :as log]
            ;[bidi.bidi :as bidi]
            ;[pushy.core :as pushy]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [reitit.frontend :as retit]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [reitit.frontend.controllers :as rfc]
            [narwhal.nav.db :as nav-db]))

(def pages
  "Big list of our top-level pages, along with their routes and any events
  we should fire off when we navigate to them"
  {:home-page/home     {::url   ""
                        ::title "Home"}
   :timeline-page/list {::url   "timeline"
                        ::title "New Timeline"}
   :timeline-page/edit {::url   "timeline/"
                        ::param [:id]
                        ::title "Edit Timeline"}
   :frame-page/list    {::url   "frame"
                        ::title "Saved Frames"}
   :frame-page/edit    {::url   "frame/"
                        ::param [:id]
                        ::title "Edit Frame"}})

(def routes
    [["/"
      ["" :home-page/home]
      ["frame" :frame-page/list]
      ["frame/:frame-id" :frame-page/edit]
      ["timeline" :timeline-page/list]
      ["timeline/:timeline-id" :timeline-page/edit]]])

(def router
  (retit/router routes {:data {:coercion rss/coercion}}))

;; Triggering navigation from events.
(rf/reg-fx
  :route/navigate!
  (fn [route]
    (apply rfe/push-state route)))

(rf/reg-event-fx
  :route/nav
  (fn [_cofx [_ & route]]
    {:route/navigate! route}))

(rf/reg-sub
  :route/current-route
  (fn [db]
    (:route/current-route db)))

(rf/reg-event-db
  :route/navigated
  (fn [db [_ new-match]]
    (let [old-match   (:route/current-route db)
          controllers (rfc/apply-controllers (:controllers old-match) new-match)]
      (assoc db :route/current-route
                (assoc new-match :controllers controllers)))))

(defn on-navigate [match history]
  (log/spy [match history])
  (when match
    (rf/dispatch [:route/navigated match])))

(defn start! []
  (rfe/start!
    router
    on-navigate
    ;; set to false to enable HistoryAPI
    {:use-fragment true}))
