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
  [:div.pixel-cell
   (merge {:style    {:background-color color}
           :on-click #(>evt [:grid/click index])}
          (when util/tooltips?
            {:data-uk-tooltip (str "title: " index "; pos: bottom-left")}))
   util/nbsp])

(defn grid-footer []
  [:div.uk-flex.uk-flex-middle
   [:fieldset.uk-fieldset
    [:div.uk-margin
     [:input.uk-input.uk-form-width-medium
      {:type "text" :placeholder "Frame name"}]]]])

(defn grid []
  [:div.pixel-grid
   (for [[i color] (map-indexed vector (<sub [:grid/pixels]))]
     ^{:key i} [cell color i])
   [:div.pixel-footer [grid-footer]]])
