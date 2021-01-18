(ns narwhal.app
  (:require [narwhal.grid :as grid]
            [re-frame.core :as rf]))

(defn app
  []
  (let [active @(rf/subscribe [:page/active])]
    [:div
     [:h1 "hi there " (str active)]
     (let [pixels (grid/random-pixels)]
       [:div
        [grid/grid pixels]])]))