(ns narwhal.graphql
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [re-graph.core :as re-graph]))

(def frame-meta-fragment "
fragment FrameFields on FrameMetadata {
  id
  name
  frame {
    height
    width
    pixels
  }
}
")
(def timeline-fragment "
fragment TimelineFields on Timeline {
  isRepeat
  steps {
    ...StepFields
  }
}
fragment StepFields on Step {
  pauseMs
  repeat
  effects {
    ...EffectFields
  }
}
fragment EffectFields on Effect {
  __typename
  durationMs
}
")
(def timeline-meta-fragment (str "
fragment TimelineMetadataFields on TimelineMetadata {
  id
  name
  timeline {
    ...TimelineFields
  }
}
" timeline-fragment))

(def queries
  {:frame-gql/new-random-frame
   {::dispatch [:frame-gql/scratch-frame-loaded]
    ::process  :frame
    ::text     "
{ frame: randomFrame { height width pixels } }
"}
   :frame-gql/new-blank-frame
   {::dispatch [:frame-gql/scratch-frame-loaded]
    ::process  :frame
    ::text     "
{ frame: solidFrame(color:\"000000\") { height width pixels } }
"}
   :timeline-gql/new-empty-timeline
   {::dispatch [:timeline-gql/empty-timeline-loaded]
    ::frags    [timeline-fragment]
    ::process  :result
    ::text     "
query {
  result: emptyTimeline {
    ... TimelineFields
  }
}
"}
   :timeline-gql/create-timeline
   {::dispatch  [:timeline-gql/timeline-created]
    ::mutation? true
    ::process   :result
    ::frags     [timeline-meta-fragment]
    ::text      "
mutation ($i: CreateTimelineRequest!) {
  result: createTimeline(input: $i) {
    timeline { id }
    allTimelines { ...TimelineMetadataFields }
  }
}
"}
   :timeline-gql/update-timeline
   {::dispatch  [:timeline-gql/timeline-updated]
    ::mutation? true
    ::process   :result
    ::frags     [timeline-meta-fragment]
    ::text      "
mutation ($i: UpdateTimelineRequest!) {
  result: updateTimeline(input: $i) {
    timeline { ...TimelineMetadataFields }
    allTimelines { ...TimelineMetadataFields }
  }
}
"}
   :timeline-gql/delete-timeline
   {::dispatch  [:timeline-gql/timeline-deleted]
    ::mutation? true
    ::process   :result
    ::frags     [timeline-meta-fragment]
    ::text      "
mutation ($i: DeletedTimelineRequest!) {
  result: deleteTimeline(input: $i) {
    timelineId
    allTimelines { ...TimelineMetadataFields }
  }
}
"}
   :frame-gql/get-frame-by-id
   {::dispatch [:frame-gql/frame-reverted]
    ::process  :result
    ::frags    [frame-meta-fragment]
    ::text     "
query ($i: String!) {
  result: frame(id: $i) { ...FrameFields }
}
"}
   :timeline-gql/get-timeline-by-id
   {::dispatch [:timeline-gql/timeline-reverted]
    ::process  :result
    ::frags    [timeline-meta-fragment]
    ::text     "
query ($i: String!) {
  result: timeline(id: $i) { ...TimelineMetadataFields }
}
"}
   :nav-gql/nav
   {::dispatch [:nav-gql/nav-loaded]
    ::frags    [frame-meta-fragment timeline-meta-fragment]
    ::text     "
{
  frames: allFrames { ...FrameFields }
  timelines: allTimelines { ...TimelineMetadataFields }
}
"}
   :frame-gql/create-frame
   {::dispatch  [:frame-gql/frame-created]
    ::mutation? true
    ::process   :result
    ::frags     [frame-meta-fragment]
    ::text      "
mutation ($i: NewFrameMetadata!) {
  result: createFrame(input: $i) {
    frame { id }
    allFrames { ...FrameFields }
  }
}
"}
   :frame-gql/update-frame
   {::dispatch  [:frame-gql/frame-updated]
    ::mutation? true
    ::process   :result
    ::frags     [frame-meta-fragment]
    ::text      "
mutation ($i: UpdateFrameRequest!) {
  result: updateFrame(input: $i) {
    frame { ...FrameFields }
    allFrames { ...FrameFields }
  }
}
"}
   :frame-gql/delete-frame
   {::dispatch  [:frame-gql/frame-deleted]
    ::mutation? true
    ::process   :result
    ::frags     [frame-meta-fragment]
    ::text      "
mutation ($i: DeletedFrameRequest!) {
  result: deleteFrame(input: $i) {
    frameId
    allFrames { ...FrameFields }
  }
}
"}})

(rf/reg-event-fx
  :graphql/run
  (fn [{:keys [db]} [_ query-name vars]]
    (assert (contains? queries query-name))
    (let [{::keys [text frags mutation?]} (get queries query-name)
          q-text     (str text (string/join "\n" frags))
          send-vars  (if vars {:i vars} {})
          event-name (if mutation?
                       ::re-graph/mutate
                       ::re-graph/query)
          event      [event-name :rg-instance q-text send-vars
                      [::query-return query-name]]]
      (log/debug "Event:" event)
      {:db       (assoc-in db [::in-flight? query-name] true)
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
      {:dispatch [::query-errors query errors]}
      (let [dispatch (get-in queries [query ::dispatch])
            process  (get-in queries [query ::process] identity)]
        (log/debug "q" query "handler" dispatch "payload " payload)
        (merge {:db (update-in db [::in-flight?] dissoc query)}
               (when dispatch
                 {:dispatch (conj dispatch (process data))}))))))
