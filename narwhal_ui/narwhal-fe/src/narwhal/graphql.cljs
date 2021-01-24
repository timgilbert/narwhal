(ns narwhal.graphql
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
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
   :nav-gql/nav
   {::dispatch [:nav-gql/nav-loaded]
    ::text     "
{
  frames: allFrames { ...FrameFields }
  timelines: allTimelines {id name}
}
fragment FrameFields on FrameMetadata {
  id
  name
  frame {
    height
    width
    pixels
  }
}
"}
   :frame-gql/create-frame
   {::dispatch  [:frame-gql/frame-created]
    ::mutation? true
    ::text      "
mutation ($i: NewFrameMetadata!) {
  createFrame(input: $i) {
    frame { ...FrameFields }
    allFrames { id name  }
  }
}
fragment FrameFields on FrameMetadata {
  id
  name
  frame {
    height
    width
    pixels
  }
}
"}})

(rf/reg-event-fx
  :graphql/query
  (fn [{:keys [db]} [_ {:graphql/keys [query vars]}]]
    (let [{::keys [text mutation?]} (get queries query)
          event-name (if mutation? ::re-graph/mutate ::re-graph/query)
          event [event-name ::query-id text (or vars {}) [::query-return query]]]
      (log/debug "Event:" event)
      {:db       (assoc-in db [::in-flight? query] true)
       :dispatch event})))

(rf/reg-event-fx
  ::query-errors
  (fn [{:keys [db]} [_ query errors]]
    (doseq [message (map :message errors)
            :let [note (str "GraphQL errors from " query ": " message)]]
      (.notification js/UIkit note #js {:status "danger"})
      (log/warn :gql-err note))
    {:db (update-in db [::in-flight?] dissoc query)}))

(rf/reg-event-fx
  ::query-return
  (fn [{:keys [db]} [_ query {:keys [data errors] :as payload}]]
    (if errors
      {:dispatch [::qmv narwhal.views.component query errors]}
      (let [dispatch (get-in queries [query ::dispatch])
            process  (get-in queries [query ::process] identity)]
        (log/debug "q" query "handler" dispatch "payload " payload)
        (merge {:db (update-in db [::in-flight?] dissoc query)}
               (when dispatch
                 {:dispatch (conj dispatch (process data))}))))))
