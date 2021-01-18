(ns ^:figwheel-hooks narwhal.narwhal-fe
  (:require
    [goog.string :as gstring]
    [reagent.core :as reagent]
    [reagent.dom :as rdom]
    [re-frame.core :as rf]))

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

(defn root []
  (let [pixels (random-pixels)]
    [:div
     [grid pixels]]))

(defn mount [el]
  (rdom/render [root] el))

(defn mount-app-element []
  (when-let [el (js/document.getElementById "app")]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
;(defn ^:after-load on-reload []
;  (mount-app-element))
;  ;; optionally touch your app-state to force rerendering depending on
;  ;; your application
;  ;; (swap! app-state update-in [:__figwheel_counter] inc)
;
