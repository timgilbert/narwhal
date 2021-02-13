(ns narwhal.nav.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as frame-db]
            [narwhal.timeline.db :as timeline-db]
            [narwhal.nav.db :as db]))

(rf/reg-event-db
  :nav-gql/nav-loaded
  (fn [db [_ {:keys [frames timelines]}]]
    (-> db
        (frame-db/replace-all-frames frames)
        (timeline-db/replace-all-timelines timelines)
        (assoc-in (db/page-path :nav/loaded?) true))))
