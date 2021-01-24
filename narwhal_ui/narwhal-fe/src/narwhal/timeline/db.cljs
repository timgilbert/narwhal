(ns narwhal.timeline.db
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]))

(def timeline-path [:t/timelines])

(defn replace-all-timelines
  [db timeline-list]
  (assoc-in db timeline-path (group-by :id timeline-list)))
