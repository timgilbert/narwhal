(ns narwhal.events
  (:require [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [narwhal.graphql :as graphql]))

(def re-graph-options
  {:ws   nil
   :http {}})

(rf/reg-event-fx :initialize-db
  (fn [_db _]
    {:db {:page/active :home/home}
     :fx [[:dispatch [::re-graph/init re-graph-options]]]}))

(rf/reg-event-fx :route/navigate
  (fn [{:keys [db]} [_ {:keys [page slug title]}]]
    (let [nav-info #:page{:active page :title title :slug slug}]
      {:db (assoc db :nav/page nav-info)})))

