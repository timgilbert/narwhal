(ns narwhal.timeline.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.timeline.db :as db]
            [clojure.string :as string]))

(rf/reg-sub
  ::timeline-root
  (fn [db _] (get-in db (db/timeline-path :t/all))))

(rf/reg-sub
  ::all-timeline-metadata
  :<- [::timeline-root]
  (fn [root _]
    (->> root
         vals
         (sort-by #(-> % :name string/upper-case)))))

(rf/reg-sub
  ::timeline-meta-by-id
  :<- [::timeline-root]
  (fn [root [_ timeline-id]]
    (get root timeline-id)))

(rf/reg-sub
  ::timeline-exists?
  (util/signal ::timeline-meta-by-id)
  (fn [timeline]
    (some? timeline)))

