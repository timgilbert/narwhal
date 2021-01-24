(ns narwhal.timeline.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.views.component :as component]
            [narwhal.util :as util :refer [<sub >evt]]
            narwhal.timeline.events
            narwhal.timeline.subs))

(defn new-timeline []
  [:div
   [:h1 "timelines"]
   [:p "coming soon!"]])
