(ns narwhal.events.frame
  (:require [narwhal.views.grid :as grid]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [re-graph.core :as re-graph]))

(rf/reg-event-fx
  :frame-edit/click
  (fn [{:keys [db]} [_ index]]
    (js/console.log "Click " index)
    {:db db}))

(rf/reg-event-fx
  :frame-edit/blank
  (fn [{:keys []} _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame/blank}]]]}))

(rf/reg-event-fx
  :frame-edit/random
  (fn [{:keys []} _]
    {:fx [[:dispatch [:graphql/query {:graphql/query :frame/random}]]]}))

(rf/reg-event-db
  :frame/new-frame
  (fn [db [_ data]]
    (js/console.log "DATA" data)
    (assoc-in db [::frames util/default-frame-name] data)))

(rf/reg-sub
  :grid/pixels
  (fn [db _]
    (get-in db [::frames util/default-frame-name :pixels])))
