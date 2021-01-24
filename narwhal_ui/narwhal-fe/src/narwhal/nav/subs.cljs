(ns narwhal.nav.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.timeline.subs :as timeline-subs]))

;; Page
(rf/reg-sub
  ::pages-root
  (fn [db _]
    (get-in db db/page-path)))

;; Frames
(rf/reg-sub
  ::frames
  :<- [::frame-subs/all-frames]
  (fn [frames _] frames))

;; Timeline
(rf/reg-sub
  ::timelines
  :<- [::timeline-subs/all-timelines]
  (fn [timelines _] timelines))
