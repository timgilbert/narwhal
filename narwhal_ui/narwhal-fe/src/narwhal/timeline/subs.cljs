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
  ::timeline-name
  (util/signal ::timeline-meta-by-id)
  (fn [timeline]
    (:name timeline)))

(rf/reg-sub
  ::scratch?
  (util/signal ::timeline-meta-by-id)
  (fn [timeline]
    (:scratch? timeline false)))

(rf/reg-sub
  ::dirty-root
  (fn [db _]
    (get-in db (db/timeline-path :t/dirty?))))

(rf/reg-sub
  ::dirty?
  :<- [::dirty-root]
  (fn [dirty-root [_ timeline-id]]
    (get dirty-root timeline-id false)))

(rf/reg-sub
  ::clean?
  (util/signal ::dirty?)
  (fn [dirty?]
    (not dirty?)))

(rf/reg-sub
  ::timeline-steps
  (util/signal ::timeline-meta-by-id)
  (fn [timeline-meta _]
    (get-in timeline-meta [:timeline :steps])))

(rf/reg-sub
  ::timeline-step
  (util/signal ::timeline-steps)
  (fn [timeline-steps [_ _timeline-id step-index]]
    (log/spy step-index)
    (log/spy timeline-steps)
    (log/spy (number? step-index))
    (assert (number? step-index))
    (nth timeline-steps step-index nil)))

(rf/reg-sub
  ::effects-for-step
  (util/signal ::timeline-step)
  (fn [step _]
    (:effects step)))

(rf/reg-sub
  ::effect
  (util/signal ::effects-for-step)
  (fn [effects [_ _timeline-id _step-index effect-index]]
    (assert (number? effect-index))
    (nth effects effect-index nil)))

(rf/reg-sub
  ::edit-state-root
  (fn [db _] (get-in db (db/timeline-path :t/edit))))

(rf/reg-sub
  ::frame-target
  (util/signal ::effect)
  (fn [effect _]
    (:target effect)))

(rf/reg-sub
  ::selected-saved-frame-target
  (util/signal ::frame-target)
  (fn [target _]
    (get target :frameId)))

(rf/reg-sub
  ::solid-frame-color
  (util/signal ::frame-target)
  (fn [target _]
    (get target :color)))
