(ns ^:figwheel-hooks narwhal.main
  (:require [reagent.dom :as rdom]
            [narwhal.grid :as grid]))

(defn root []
  (let [pixels (grid/random-pixels)]
    [:div
     [grid/grid pixels]]))

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
