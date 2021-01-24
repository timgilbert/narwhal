(ns narwhal.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]
            [narwhal.nav.db :as nav-db]))

(def pages
  "Big list of our top-level pages, along with their routes and any events
  we should fire off when we navigate to them"
  {:home-page/home     {::url   ""
                        ::title "Home"}
   :timeline-page/new  {::url   "timeline"
                        ::title "New Timeline"}
   :timeline-page/edit {::url   "timeline/"
                        ::param [:id]
                        ::title "Edit Timeline"}
   :frame-page/new     {::url      "frame"
                        ::title    "New Frame"
                        ::dispatch [:frame/create-scratch]}
   :frame-page/edit    {::url   "frame/"
                        ::param [:id]
                        ::title "Edit Frame"}})

(rf/reg-event-fx
  :route/navigate
  (fn [{:keys [db]} [_ destination]]
    (let [config (get pages (:route/page destination))]
      (merge
        {:db (nav-db/set-page db destination)}
        (when-let [dispatch (::dispatch config)]
          {:dispatch (conj dispatch destination)})))))

(defn gen-bidi-routes [pages]
  (let [r-map (->> (for [[page {::keys [url param]}] pages]
                     [url (if param {param page} page)])
                   (into {}))]
    ["/" r-map]))

(def bidi-routes (gen-bidi-routes pages))
;; This code cribbed from https://github.com/jacekschae/conduit/blob/master/src/conduit/router.cljs

(def history
  (let [dispatch #(rf/dispatch [:route/navigate
                                #:route{:page (:handler %)
                                        :id   (get-in % [:route-params :id])}])
        match    #(bidi/match-route bidi-routes %)]
    (pushy/pushy dispatch match)))

(defn start! []
  (pushy/start! history))

(defn set-token!
  [token]
  (pushy/set-token! history token))
