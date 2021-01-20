(ns narwhal.util
  (:require [bidi.bidi :as bidi]
            [narwhal.router :as router]
            [re-frame.core :as rf]))

(def <sub (comp deref rf/subscribe))

(defn link
  ([route-name text]
   (link route-name nil text))
  ([route-name params text]
   (js/console.log [route-name params text])
   (js/console.log (apply bidi/path-for (concat [router/bidi-routes route-name] params)))
   (let [href (apply bidi/path-for (concat [router/bidi-routes route-name] params))]
     [:a {:href href} text])))

(defn icon [icon-name]
  [:span {:data-uk-icon icon-name}])