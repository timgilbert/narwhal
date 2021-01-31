(ns narwhal.frame.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util]
            [narwhal.frame.db :as db]
            [clojure.string :as string]))

;; ----------------------------------------------------------------------
;; Frame stuff

(rf/reg-sub
  ::frame-root
  (fn [db _] (get-in db (db/frame-path :f/all))))

(rf/reg-sub
  ::all-frame-metadata
  :<- [::frame-root]
  (fn [root _]
    (->> root
         vals
         ;; TODO: more sort options
         (sort-by #(-> % :name string/upper-case)))))

(rf/reg-sub
  ::frame-meta-by-id
  :<- [::frame-root]
  (fn [root [_ frame-id]]
    (get root frame-id)))

(rf/reg-sub
  ::frame-exists?
  (util/signal ::frame-meta-by-id)
  (fn [frame]
    (some? frame)))

(rf/reg-sub
  ::scratch?
  (util/signal ::frame-meta-by-id)
  (fn [frame]
    (:scratch? frame false)))

(rf/reg-sub
  ::dirty-root
  (fn [db _]
    (get-in db (db/frame-path :f/dirty?))))

(rf/reg-sub
  ::dirty?
  :<- [::dirty-root]
  (fn [dirty-root [_ frame-id]]
    (get dirty-root frame-id false)))

(rf/reg-sub
  ::clean?
  (util/signal ::dirty?)
  (fn [dirty?]
    (not dirty?)))

;; ----------------------------------------------------------------------
;; Title stuff
(rf/reg-sub
  ::editing-title?
  (fn [db [_ frame-id]]
    (get-in db (db/frame-path :f/editing? frame-id) false)))


(rf/reg-sub
  ::frame-name
  (util/signal ::frame-meta-by-id)
  ;:<- [::frame]
  (fn [frame]
    (:name frame)))
