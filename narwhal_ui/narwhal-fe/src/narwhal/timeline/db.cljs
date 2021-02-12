(ns narwhal.timeline.db
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [goog.string :as gstring]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.color :as color]
            [narwhal.frame.db :as frame-db]))

;; TODO: lots of copypasta from frames here, refactor to generic CRUD stuff

(defn timeline-path [& rest]
  (concat [:t/timelines] rest))

(defn replace-all-timelines
  [db timeline-list]
  (let [new-all (->> timeline-list
                     ;; Why isn't (group-by) ever useful for anything?
                     (map (fn [f]
                            [(:id f) f]))
                     (into {}))]
    (assoc-in db (timeline-path :t/all) new-all)))

(defn replace-single-timeline
  [db new-timeline]
  (let [timeline-id (:id new-timeline)]
    (assert (some? timeline-id))
    (assoc-in db (timeline-path :t/all timeline-id) new-timeline)))

(defn timeline-by-id [db timeline-id]
  (get-in db (timeline-path :t/all timeline-id)))

(defn dehydrate
  ([timeline-meta]
   (dehydrate timeline-meta ::update))
  ([timeline-meta type]
   (case type
     ::create (dissoc timeline-meta :id :scratch?)
     ::update (dissoc timeline-meta :scratch?))))

(defn timeline-name [db timeline-id]
  (get-in db (timeline-path :t/all timeline-id :name)))

(defn set-timeline-name [db timeline-id new-name]
  (assoc-in db (timeline-path :t/all timeline-id :name) new-name))

(defn ^:private scratch-id [i]
  (str "timeline-" (gstring/padNumber i 2)))

(defn next-scratch-id [db]
  (loop [i 1]
    (let [potential-id (scratch-id i)]
      (if (some? (timeline-by-id db potential-id))
        (recur (inc i))
        potential-id))))

(defn with-scratch-metadata [db timeline-data]
  (let [scratch-id   (next-scratch-id db)
        scratch-name (-> scratch-id
                         (gstring/replaceAll "-" " ")
                         (gstring/toTitleCase))]
    {:id       scratch-id
     :scratch? true
     :name     scratch-name
     :timeline timeline-data}))

(defn set-dirty
  [db timeline-id]
  (assoc-in db (timeline-path :t/dirty? timeline-id) true))

(defn set-clean
  [db timeline-id]
  (update-in db (timeline-path :t/dirty?) dissoc timeline-id))

(defn new-default-frame-target
  ([db]
   (new-default-frame-target db :RANDOM_FRAME))
  ([db target-type]
   (log/spy target-type)
   (merge
     {:type target-type}
     (case target-type
       :SOLID_FRAME {:color color/black}
       :SAVED_FRAME {:frameId (frame-db/first-frame-id db)}
       nil))))

(defn new-default-effect
  ([db]
   (new-default-effect db :REPLACE_EFFECT))
  ([db effect-type]
   (merge
     {:type    effect-type
      :pauseMs 0
      :target  (new-default-frame-target db)}
     (when (= effect-type :TWEEN_EFFECT)
       {:granularity 10
        :durationMs  1000}))))

(defn new-blank-step [db]
  {:effects     [(new-default-effect db)]
   :repetitions 1
   :pauseMs     1000})

(defn get-step
  [db timeline-id step-index]
  (assert (some? timeline-id))
  (assert (number? step-index))
  (let [timeline (timeline-by-id db timeline-id)]
    (get-in timeline [:steps step-index] nil)))

(defn assoc-step
  [db timeline-id step-index step]
  (assert (every? some? [timeline-id step]))
  (assert (number? step-index))
  (let [timeline (timeline-by-id db timeline-id)
        steps    (-> (get timeline :steps [])
                     (assoc step-index step))
        new-tl   (assoc-in timeline [:timeline :steps] steps)]
    (replace-single-timeline db new-tl)))

(defn get-effect
  [db timeline-id step-index effect-index]
  (assert (some? timeline-id))
  (assert (every? number? [step-index effect-index]))
  (let [timeline (timeline-by-id db timeline-id)]
    (get-in timeline [:timeline :steps step-index :effects effect-index]
            nil)))

(defn get-target
  [db timeline-id step-index effect-index]
  (some-> (get-effect db timeline-id step-index effect-index)
          :target))

(defn assoc-effect
  [db timeline-id step-index effect-index effect]
  (assert (every? some? [timeline-id effect]))
  (assert (every? number? [step-index effect-index]))
  (let [step    (get-step db timeline-id step-index)
        effects (-> (get step :effects [])
                    (assoc effect-index effect))]
    (assoc-step db timeline-id step-index
                (assoc step :effects effects))))

(defn replace-effect-type
  [db timeline-id step-index effect-index effect-type]
  (let [effect (new-default-effect db effect-type)]
    (assoc-effect db timeline-id step-index effect-index
                  effect)))

(defn replace-frame-target
  [db timeline-id step-index effect-index target]
  (let [effect (get-effect db timeline-id step-index effect-index)]
    (assoc-effect db timeline-id step-index effect-index
                  (assoc effect :target target))))

(defn replace-frame-target-type
  [db timeline-id step-index effect-index target-type]
  (replace-frame-target db timeline-id step-index effect-index
                        (new-default-frame-target db target-type)))

(defn replace-saved-frame-target-id
  [db timeline-id step-index effect-index frame-id]
  (let [target (get-target db timeline-id step-index effect-index)]
    (replace-frame-target db timeline-id step-index effect-index
                          (assoc target :frameId frame-id))))

(defn update-step [db timeline-id step-index]
  ;; The user has selected a thing and hit save
  (let [timeline (timeline-by-id db timeline-id)
        selected (timeline-path :t/edit timeline-id step-index)]
    db))

(defn init-db [db]
  (-> db
      (replace-all-timelines [])))

(def default-selected-effect :narwhal.timeline.views.effects/saved)