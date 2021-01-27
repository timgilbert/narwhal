(ns narwhal.timeline.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]
            narwhal.timeline.events
            narwhal.timeline.subs))

(defn timeline-list []
  [:div
   [:h1 "Timelines"]
   [:p "coming soon!"]])
