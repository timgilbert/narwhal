(ns narwhal.events
  (:require [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [narwhal.util :as util]
            [narwhal.events.frame :as frame-events]))

(def re-graph-options
  {:ws   nil
   :http {}})

(defn initial-db []
  {:page/active :home/home
   :palette/active-color util/black
   ::nav {::frames [] ::timelines []}})

(rf/reg-event-fx :initialize-db
  (fn [_db _]
    {:db (initial-db)
     :fx [[:dispatch [::re-graph/init re-graph-options]]
          [:dispatch [:graphql/query {:graphql/query :nav-gql/nav}]]]}))

(rf/reg-event-db
  :nav-gql/nav-loaded
  (fn [db [_ payload]]
    (-> db (assoc-in [::nav ::frames] (:frames payload))
           (assoc-in [::nav ::timelines] (:timelines payload)))))

(rf/reg-sub
  ::nav
  (fn [db _] (::nav db)))

(rf/reg-sub
  :nav/frames
  :<- [::nav]
  (fn [nav _]
    (::frames nav)))

(rf/reg-sub
  :nav/timelines
  :<- [::nav]
  (fn [nav _]
    (::timelines nav)))
