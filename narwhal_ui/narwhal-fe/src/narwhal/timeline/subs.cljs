(ns narwhal.timeline.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]))

(rf/reg-sub
  ::timeline-root
  (fn [db _] (get-in db db/timeline-path)))

(rf/reg-sub
  ::all-timelines
  :<- [::timeline-root]
  (constantly []))
