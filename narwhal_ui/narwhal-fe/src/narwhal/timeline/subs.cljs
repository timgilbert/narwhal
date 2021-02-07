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

(rf/reg-sub
  ::timeline-steps
  (util/signal ::timeline-meta-by-id)
  (fn [timeline]
    (:steps timeline [])))

(rf/reg-sub
  ::timeline-step
  (fn [_ timeline-id _step]
    (rf/subscribe [::timeline-steps timeline-id]))
  (fn [timeline-steps [_ _ step]]
    (nth timeline-steps step nil)))

(rf/reg-sub
  ::edit-state-root
  (fn [db _] (get-in db (db/timeline-path :t/edit))))

(rf/reg-sub
  ::effect-chosen
  :<- [::edit-state-root]
  (fn [root [_ timeline-id step]]
    (get-in root [timeline-id step :t/selected-effect])))
