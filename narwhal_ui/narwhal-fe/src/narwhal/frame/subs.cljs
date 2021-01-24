(ns narwhal.frame.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as db]))

(rf/reg-sub
  ::frame-root
  (fn [db _] (get-in db (db/frame-path))))

(rf/reg-sub
  ::all-frames
  :<- [::frame-root]
  (fn [root _]
    (->> root
         (map second)
         (sort-by :name) ; TODO: more sort options
         (into []))))

(rf/reg-sub
  ::frame
  :<- [::frame-root]
  (fn [root [_ frame-id]]
    (get root frame-id)))
