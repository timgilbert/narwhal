(ns narwhal.app.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [narwhal.app.db :as db]))

(def re-graph-options
  {:ws   nil
   :http {}})

(rf/reg-event-fx
  ::initialize-app
  (fn [_db _]
    {:db (db/initial-db {})
     :fx [[:dispatch [::re-graph/init :rg-instance re-graph-options]]
          [:dispatch [:graphql/query {:graphql/query :nav-gql/nav}]]]}))

