(ns narwhal.views.frame
  (:require [narwhal.views.grid :as grid]))

(defn new-frame [slug]
  [:div
   [:h1 "new-frame"]
   [grid/grid]])
