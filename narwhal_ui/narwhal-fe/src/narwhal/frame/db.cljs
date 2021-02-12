(ns narwhal.frame.db
  (:require [lambdaisland.glogi :as log]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]))

(defn frame-path
  ([& rest]
   (concat [:f/frames] rest)))

(defn replace-all-frames
  [db frame-list]
  (assoc-in db (frame-path :f/all)
            (->> frame-list
                 ;; Why isn't (group-by) ever useful for anything?
                 (map (fn [f]
                        [(:id f) f]))
                 (into {}))))

(defn replace-single-frame
  [db new-frame]
  (let [frame-id (:id new-frame)]
    (assert (some? frame-id))
    (assoc-in db (frame-path :f/all frame-id) new-frame)))

(defn frame-by-id [db frame-id]
  (get-in db (frame-path :f/all frame-id)))

(defn first-frame-id [db]
  (some->> (get-in db (frame-path :f/all))
           keys
           sort
           first))

(defn ^:private scratch-id [i]
  (str "scratch-" (gstring/padNumber i 2)))

(defn next-scratch-id [db]
  ;; Brute force, but we won't need to run this often
  (loop [i 1]
    (let [potential-id (scratch-id i)]
      (if (some? (frame-by-id db potential-id))
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
     :frame    frame-data}))

(defn frame-name
  [db frame-id]
  (get-in db (frame-path :f/all frame-id :name)))

(defn set-frame-name
  [db frame-id new-name]
  (assoc-in db (frame-path :f/all frame-id :name) new-name))

(defn set-dirty
  [db frame-id]
  (assoc-in db (frame-path :f/dirty? frame-id) true))

(defn set-clean
  [db frame-id]
  (update-in db (frame-path :f/dirty?) dissoc frame-id))

(defn init-db [db]
  (-> db
      (replace-all-frames [])))
