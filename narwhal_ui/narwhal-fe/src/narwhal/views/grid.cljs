(ns narwhal.views.grid
  (:require [goog.string :as gstring]))

(def height 16)
(def width 16)
(def nbsp (gstring/unescapeEntities "&nbsp;"))

(defn rand-color []
  (let [b ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "a" "b" "c" "d" "e" "f"]]
    (apply str (concat "#" (repeatedly 6 #(rand-nth b))))))

(defn random-pixels []
  (repeatedly (* height width) rand-color))

(defn cell [color]
  [:div.cell {:style {:background-color color}} nbsp])

(defn grid-row [pixels start]
  [:div.row
   (for [i (range start (+ start height))
         :let [c (nth pixels i)]]
     ^{:key i} [cell c])])

(defn grid [pixels]
  [:div.grid
   (for [row (range height)
         col (range width)
         :let [y (* row width)
               i (+ y col)
               c (nth pixels i)]]
     ^{:key i} [cell c])])
;col (range height))])

