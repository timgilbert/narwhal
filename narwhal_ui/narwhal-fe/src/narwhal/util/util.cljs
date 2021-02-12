(ns narwhal.util.util ; doubly useful
  (:require [lambdaisland.glogi :as log]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [narwhal.util.color :as color])
  (:import [goog.format JsonPrettyPrinter]))

(defn <sub [sub]
  (let [sub-vec (if (keyword? sub) [sub] sub)]
    (deref (rf/subscribe sub-vec))))

(def >evt rf/dispatch)

(def nbsp (gstring/unescapeEntities "&nbsp;"))
(def tooltips? true)

(defn signal
  "Produce a re-frame signal function which combines the given subscription
  name with whatever args were in the query vector."
  [sub]
  (fn [query-vector]
    (rf/subscribe (into [sub] (rest query-vector)))))

(defn json-dump [clj-thing]
  (let [js      (clj->js clj-thing)
        json-pp (JsonPrettyPrinter.)
        js-str  (.format json-pp js)]
    [:div
     ;[:p.uk-text-muted (str clj-str)]
     [:pre js-str]]))
