(ns narwhal.views.grid
  (:require [narwhal.util :as util :refer [<sub >evt]]))

(def height 16)
(def width 16)

(defn rand-color []
  (let [b ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "a" "b" "c" "d" "e" "f"]]
    (apply str (concat "#" (repeatedly 6 #(rand-nth b))))))

(defn random-pixels []
  (repeatedly (* height width) rand-color))

(defn cell [color index]
  [:div.pixel-cell {:style    {:background-color color}
                    :on-click #(>evt [:frame-edit/click index])}
   [:small (str index)]])

(defn grid-footer []
  [:div.uk-flex.uk-flex-middle
   [:fieldset.uk-fieldset
    [:div.uk-margin
     [:input.uk-input.uk-form-width-medium
      {:type "text" :placeholder "Frame name"}]]]])

(defn grid [pixels]
  [:div.pixel-grid
   (for [row (range height)
         col (range width)
         :let [y (* row width)
               i (+ y col)
               c (nth pixels i)]]
     ^{:key i} [cell c i])
   [:div.pixel-footer [grid-footer]]])
