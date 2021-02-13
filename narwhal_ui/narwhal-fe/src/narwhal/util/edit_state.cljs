(ns narwhal.util.edit-state
  (:require [lambdaisland.glogi :as log]
    [re-frame.core :as rf]
    [goog.string :as gstring]
    [narwhal.util.util :as util :refer [<sub >evt]]
    [narwhal.util.color :as color]
    [narwhal.frame.db :as frame-db]))

(defn edit-path [& rest]
  (concat [:e/root] rest))

(rf/reg-sub
  ::edit-root
  (fn [db _] (get-in db (edit-path))))

(rf/reg-sub
  ::editing?
  :<- [::edit-root]
  (fn [root [_ type tuple]]
    (= (get root type) tuple)))

(rf/reg-event-db
  ::set-editing
  (fn [db [_ type tuple]]
    (assoc-in db (edit-path type) tuple)))

(rf/reg-event-db
  ::clear-editing
  (fn [db [_ type]]
    (update-in db (edit-path) dissoc type)))

(def timeline ::timeline)

(defn init-db [db]
  (assoc-in db (edit-path) {}))