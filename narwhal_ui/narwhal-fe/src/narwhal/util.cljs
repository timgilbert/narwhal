(ns narwhal.util
  (:require [bidi.bidi :as bidi]
            [narwhal.router :as router]))

(defn link
  ([route-name text]
   (link route-name nil text))
  ([route-name params text]
   (js/console.log [route-name params text])
   (js/console.log (apply bidi/path-for (concat [router/bidi-routes route-name] params)))
   (let [href (apply bidi/path-for (concat [router/bidi-routes route-name] params))]
     [:a {:href href} text])))
