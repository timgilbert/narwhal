(ns narwhal.frame.db
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]))

(defn frame-path
  ([& rest]
   (concat [:f/frames] rest)))

(defn init-db [db]
  (assoc-in db (frame-path) {}))

(defn replace-all-frames
  [db frame-list]
  (assoc-in db (frame-path)
            (->> frame-list
                 ;; Why isn't (group-by) ever useful for anything?
                 (map (fn [f]
                        [(:id f) f]))
                 (into {}))))
