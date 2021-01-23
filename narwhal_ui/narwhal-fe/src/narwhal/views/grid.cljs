(ns narwhal.views.grid
  (:require [narwhal.util :as util :refer [<sub >evt]]))

(def height 16)
(def width 16)

(defn rand-color []
  (let [b ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9" "a" "b" "c" "d" "e" "f"]]
    (apply str (concat "#" (repeatedly 6 #(rand-nth b))))))

(defn random-pixels []
  (repeatedly (* height width) rand-color))

(defn cell [frame-id color index]
  [:div.pixel-cell
   (merge {:style    {:background-color color}
           :on-click #(>evt [:grid/click frame-id index])}
          (when util/tooltips?
            {:data-uk-tooltip (str "title: " index "; pos: bottom-left")}))
   util/nbsp])

(defn grid-footer []
  [:div.uk-flex.uk-flex-middle
   [:fieldset.uk-fieldset
    [:div.uk-margin
     [:input.uk-input.uk-form-width-medium
      {:type "text" :placeholder "Frame name"}]]]])

(defn header []
  (let [ital (if (<sub [:frame/dirty?]) :i :span)]
    [:h1
     [ital (<sub [:frame/active-frame-name])]]))

(defn grid [frame-id]
  (let [pixels (<sub [:grid/pixels frame-id])]
    (when pixels
      [:div.pixel-grid
       (for [[i color] (map-indexed vector pixels)]
         ^{:key i} [cell frame-id color i])
       [:div.pixel-footer [grid-footer]]])))
