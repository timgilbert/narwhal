(ns narwhal.frame.db
  (:require [lambdaisland.glogi :as log]
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
  (let [frame-id (or (:id new-frame) util/default-frame-id)]
    (assoc-in db (frame-path :f/all frame-id) new-frame)))

(defn frame-by-id [db frame-id]
  (get-in db (frame-path :f/all frame-id)))

(defn with-blank-metadata [frame-data]
  (merge {:id    util/default-frame-id
          :name  util/default-frame-name
          :frame frame-data}))

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
