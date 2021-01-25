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

(defn rand-color []
  (let [b ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "a" "b" "c" "d" "e" "f"]]
    (apply str (concat "#" (repeatedly 6 #(rand-nth b))))))

(defn random-pixels
  ([] random-pixels 16 16)
  ([height width]
   (repeatedly (* height width) rand-color)))
