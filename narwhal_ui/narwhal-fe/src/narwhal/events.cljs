(ns narwhal.events
  (:require [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [narwhal.graphql :as graphql]
            [narwhal.util :as util]))

(def re-graph-options
  {:ws   nil
   :http {}})

(defn initial-db []
  {:page/active :home/home
   :palette/active-color util/black})

(rf/reg-event-fx :initialize-db
  (fn [_db _]
    {:db (initial-db)
     :fx [[:dispatch [::re-graph/init re-graph-options]]]}))

