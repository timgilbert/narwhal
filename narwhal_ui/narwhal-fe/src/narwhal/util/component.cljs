(ns narwhal.util.component
  (:require [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.router.core :as router]
            [re-frame.core :as rf]))

(defn link
  ([route-name text]
   (link route-name nil nil text))
  ([route-name params props text]
   (let [href (router/href route-name params)]
     [:a (assoc props :href href) text])))

(defn icon
  ([icon-name] (icon icon-name nil))
  ([icon-name props] (icon icon-name props 1))
  ([icon-name props ratio]
   [:span (merge {:data-uk-icon
                  (str "icon:" icon-name "; ratio:" ratio)}
                 props)]))

(defn error-page [& msg]
  [:article.uk-article
   [:h1.uk-article-title "Ruh roh!"]
   [:p.uk-article
    (if msg (apply str msg) "An error occurred!")]
   [:p.uk-article
    [link :home-page/home "Home"]]])

(defn slider [props]
  [:input.uk-range
   (merge {:type :range
           :value "0"
           :min 0
           :max 10000
           :step 100})])