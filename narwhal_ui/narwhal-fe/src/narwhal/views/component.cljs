(ns narwhal.views.component
  (:require [bidi.bidi :as bidi]
            [narwhal.router :as router]
            [narwhal.util :as util :refer [<sub >evt]]
            [re-frame.core :as rf]))

(defn link
  ([route-name text]
   (link route-name nil text))
  ([route-name params text]
   (let [href (apply bidi/path-for (concat [router/bidi-routes route-name] params))]
     [:a {:href href} text])))

(defn icon [icon-name active?]
  (let [class (if active? "uk-icon-button" "")]
    [:span {:class class :data-uk-icon icon-name}]))