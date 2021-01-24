(ns narwhal.util
  (:require [goog.string :as gstring]
            [re-frame.core :as rf]
            [narwhal.color :as color]))

(defn <sub [sub]
  (let [sub-vec (if (keyword? sub) [sub] sub)]
    (deref (rf/subscribe sub-vec))))

(def >evt rf/dispatch)

(def nbsp (gstring/unescapeEntities "&nbsp;"))
(def tooltips? true)

(def black (::color/black color/named))

(def default-frame-name "*scratch*")
(def default-frame-id ::scratch-frame)
(def default-timeline-name "*scratch*")
(def default-timeline-id ::scratch-timeline)
