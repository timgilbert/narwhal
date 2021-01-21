(ns narwhal.util
  (:require [goog.string :as gstring]
            [bidi.bidi :as bidi]
            [narwhal.router :as router]
            [re-frame.core :as rf]))

(def <sub (comp deref rf/subscribe))

(def >evt rf/dispatch)

(def nbsp (gstring/unescapeEntities "&nbsp;"))
(def tooltips? true)

(def default-frame-name "*scratch*")
(def default-timeline-name "*scratch*")

(defn link
  ([route-name text]
   (link route-name nil text))
  ([route-name params text]
   (let [href (apply bidi/path-for (concat [router/bidi-routes route-name] params))]
     [:a {:href href} text])))

(defn icon [icon-name active?]
  (let [class (if active? "uk-icon-button" "")]
    [:span {:class class :data-uk-icon icon-name}]))