(ns narwhal.util.util                                       ; doubly useful
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
    (assert (seqable? query-vector))
    (rf/subscribe (into [sub] (rest query-vector)))))

(defn json-dump [clj-thing]
  (let [js      (clj->js clj-thing)
        json-pp (JsonPrettyPrinter.)
        js-str  (.format json-pp js)]
    [:div
     ;[:p.uk-text-muted (str clj-str)]
     [:pre js-str]]))

(defn for-children
  "Given a parent hiccup vector, a seq of items, and one or more
  child vectors, produce a hiccup vector where each of the child
  vectors in turn is a child of the parent. Every child vector
  will have the index into the seq and the element at that index
  appended to its props.

  This is useful for situations where multiple elements need to be
  produced from the same seq, but it's inconvenient to nest them
  all under a common parent."
  [input-seq first-element & output-vecs]
  (assert (vector? first-element))
  (assert (not-empty first-element))
  (assert (not-empty output-vecs))
  (assert (every? vector? output-vecs))
  (let [kids (for [[i element] (map-indexed vector input-seq)
                   [j output-vec] (map-indexed vector output-vecs)]
               ^{:key [i j]}
               (into output-vec [i element]))]
    (into first-element kids)))

(defn ^:private _kid-a [prop index item]
  [:p "first. prop: " prop ", index: " index ", item: " item])

(defn ^:private _kid-b [index item]
  [:p "second. index: " index ", item: " item])

(defn ^:private _test-kid []
  [for-children
   ["a" "b" "c" "d"]
   [:div "Results"]
   [_kid-a "hello"]
   [_kid-b]])

