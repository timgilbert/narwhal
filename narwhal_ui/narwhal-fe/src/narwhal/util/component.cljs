(ns narwhal.util.component
  (:require [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.router.core :as router]
            [re-frame.core :as rf]))

(defn link
  ([route-name text]
   (link route-name nil text))
  ([route-name params text]
   (let [href (router/href route-name params)]
         ;(apply bidi/path-for (concat [router/bidi-routes route-name] params))]
     [:a {:href href} text])))

(defn icon [icon-name active?]
  (let [class (if active? "uk-icon-button" "")]
    [:span {:class class :data-uk-icon icon-name}]))

(defn error-page [& msg]
  [:article.uk-article
   [:h1.uk-article-title "Ruh roh!"]
   [:p.uk-article
    (if msg (apply str msg) "An error occurred!")]])
