(ns narwhal.frame.views.list
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.events :as events]
            [narwhal.frame.subs :as subs]
            [fork.re-frame :as fork]
            [narwhal.grid.views :as grid]))

(defn frame-list []
  [:div
   [:h1 "Saved Frames"]
   [:p "coming soon!"]])
