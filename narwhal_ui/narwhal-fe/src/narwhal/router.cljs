(ns narwhal.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :refer [dispatch]]))

;; This code cribbed from https://github.com/jacekschae/conduit/blob/master/src/conduit/router.cljs

(def routes
  ["/" {""          :home/home
        "timeline"  :timeline/new
        "timeline/" {[:slug] :timeline/edit}
        "frame"     :frame/new
        "frame/"    {[:slug] :frame/edit}}])

(def history
  (let [dispatch #(dispatch [:route/go
                             {:page      (:handler %)
                              :slug      (get-in % [:route-params :slug])}])
        match    #(bidi/match-route routes %)]
    (pushy/pushy dispatch match)))

(defn start! []
  (pushy/start! history))

(defn set-token!
  [token]
  (pushy/set-token! history token))
