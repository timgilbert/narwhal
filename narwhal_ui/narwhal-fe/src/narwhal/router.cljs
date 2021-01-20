(ns narwhal.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :refer [dispatch]]))

(def pages
  "Big list of our top-level pages, along with their routes and any events
  we should fire off when we navigate to them"
  {:home/home     {::url   ""
                   ::title "Home"}
   ;::handler home/home}
   :timeline/new  {::url   "timeline"
                   ::title "New Timeline"}
   :timeline/edit {::url   "timeline/"
                   ::param [:slug]
                   ::title "Edit Timeline"}
   :frame/new     {::url   "frame"
                   ::title "New Frame"}
   :frame/edit    {::url   "frame/"
                   ::param [:slug]
                   ::title "Edit Frame"}})

(defn gen-bidi-routes [pages]
  (let [r-map (->> (for [[page {::keys [url param]}] pages]
                     [url (if param {param page} page)])
                (into {}))]
    ["/" r-map]))

(def bidi-routes (gen-bidi-routes pages))
;; This code cribbed from https://github.com/jacekschae/conduit/blob/master/src/conduit/router.cljs

(def history
  (let [dispatch #(dispatch [:route/navigate
                             {:page (:handler %)
                              :slug (get-in % [:route-params :slug])}])
        match    #(bidi/match-route bidi-routes %)]
    (pushy/pushy dispatch match)))

(defn start! []
  (pushy/start! history))

(defn set-token!
  [token]
  (pushy/set-token! history token))