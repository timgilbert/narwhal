(ns narwhal.graphql
  (:require [re-frame.core :as rf]
            [re-graph.core :as re-graph]))

(def queries
  {:frame-gql/random
   {::dispatch [:frame-gql/frame-loaded]
    ::process  :frame
    ::text     "
{ frame: randomFrame { height width pixels } }
"}
   :frame-gql/blank
   {::dispatch [:frame-gql/frame-loaded]
    ::process  :frame
    ::text     "
{ frame: solidFrame(color:\"000000\") { height width pixels } }
"}
   :frame-gql/create-frame
   {::dispatch [:frame-gql/frame-created]
    ::process  :frame
    ::text     "
mutation m($i: NewNamedFrame!) {
  createFrame(input: $i) {
    frame {
      id
      name
    }
    allFrames {
      id
      name
    }
  }
}
"}})

(rf/reg-event-fx
  :graphql/query
  (fn [{:keys [db]} [_ {:graphql/keys [query vars]}]]
    (let [gql (get-in queries [query ::text])]
      {:db       (assoc-in db [::in-flight? query] true)
       :dispatch [::re-graph/query query gql vars [::query-return query]]})))

(rf/reg-event-fx
  ::query-return
  (fn [{:keys [db]} [_ query {:keys [data errors] :as payload}]]
    (let [dispatch (get-in queries [query ::dispatch])
          process  (get-in queries [query ::process] identity)]
      (js/console.log "q" query "handler" dispatch "payload " payload)
      (merge {:db (update-in db [::in-flight?] dissoc query)}
             (when dispatch
               {:dispatch (conj dispatch (process data))})))))
