(ns narwhal.timeline.db
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [goog.string :as gstring]
            [narwhal.util.util :as util :refer [<sub >evt]]))

;; TODO: lots of copypasta from frames here, refactor to generic CRUD stuff

(defn timeline-path
  ([& rest]
   (concat [:t/timelines] rest)))

(defn replace-all-timelines
  [db timeline-list]
  (assoc-in db (timeline-path :t/all)
            (->> timeline-list
                 ;; Why isn't (group-by) ever useful for anything?
                 (map (fn [f]
                        [(:id f) f]))
                 (into {}))))

(defn replace-single-timeline
  [db new-timeline]
  (let [timeline-id (:id new-timeline)]
    (assert (some? timeline-id))
    (assoc-in db (timeline-path :t/all timeline-id) new-timeline)))

(defn timeline-by-id [db timeline-id]
  (get-in db (timeline-path :f/all timeline-id)))

(defn ^:private scratch-id [i]
  (str "timeline-" (gstring/padNumber i 2)))

(defn next-scratch-id [db]
  (loop [i 1]
    (let [potential-id (scratch-id i)]
      (if (some? (timeline-by-id db potential-id))
        (recur (inc i))
        potential-id))))

(defn with-scratch-metadata [db frame-data]
  (let [scratch-id   (next-scratch-id db)
        scratch-name (-> scratch-id
                         (gstring/replaceAll "-" " ")
                         (gstring/toTitleCase))]
    {:id       scratch-id
     :scratch? true
     :name     scratch-name
     :timeline frame-data}))

(defn set-dirty
  [db frame-id]
  (assoc-in db (timeline-path :t/dirty? frame-id) true))

(defn set-clean
  [db frame-id]
  (update-in db (timeline-path :t/dirty?) dissoc frame-id))

(defn init-db [db]
  (-> db
      (replace-all-timelines [])))
