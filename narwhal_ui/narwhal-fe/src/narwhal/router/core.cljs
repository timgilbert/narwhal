(ns narwhal.router.core
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [reitit.frontend :as retit]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [narwhal.router.routes :as routes]
            [narwhal.router.events :as events]))

(def router
  (retit/router (routes/gen-routes)))

(defn on-navigate [match history]
  (log/spy [match history])
  (when match
    (rf/dispatch [::events/navigated match])))

(defn href [page params]
  (rfe/href page params))

(defn start! []
  (rfe/start!
    router
    on-navigate
    ;; set to false to enable HistoryAPI
    {:use-fragment false}))
