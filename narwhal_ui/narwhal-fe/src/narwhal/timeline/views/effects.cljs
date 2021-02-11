(ns narwhal.timeline.views.effects
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.subs :as subs]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.timeline.events :as events]
            [narwhal.frame.views.list :as frame-list]
            [re-frame.core :as rf]))

(defn chooser-cancel-button [timeline-id step]
  [:button
   {:on-click #(>evt [::events/choose-effect timeline-id step nil])}
   [:span {:data-uk-icon "close"}]])

(defn chooser-confirm-button [timeline-id step]
  [:button
   {:on-click #(>evt [::events/choose-effect timeline-id step nil])}
   [:span {:data-uk-icon "check"}]])

(defn solid-frame-target-editor [timeline-id step-index]
  [:div
   "solid-frame-target-editor"])

(defn random-frame-target-editor [timeline-id step-index]
  [:div
   "random-frame-target-editor"])

(defn no-saved-frames-message []
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [:div.uk-card.uk-card-body.uk-card-small
    [:p "No saved frames available! Create one first"]]])

(defn saved-frame-target-editor [timeline-id step-index]
  [:div
   (let [frames      (<sub [::frame-subs/all-frame-metadata])
         curr-target (<sub [::subs/selected-saved-frame-target
                            timeline-id step-index])]
     (if (empty? frames)
       [no-saved-frames-message]
       [frame-list/frame-list
        #:frame-list{:active-id   curr-target
                     :click-event [::events/select-saved-frame-target
                                   timeline-id step-index]}]))])

(def frame-target-editors
  {::color  {::editor   solid-frame-target-editor
             ::tab-name "Solid Color"}
   ::random {::editor   random-frame-target-editor
             ::tab-name "Random Frame"}
   ::saved  {::editor   saved-frame-target-editor
             ::tab-name "Saved Frame"}})

(def all-effects [::saved ::random ::color])

(defn frame-target-tab [timeline-id step-index effect-id {::keys [tab-name]}]
  (let [current (<sub [::subs/effect-chosen timeline-id step-index])
        active? (= current effect-id)
        attrs   (if active?
                  {:class "uk-active"}
                  {:on-click #(>evt [::events/choose-effect
                                     timeline-id step-index effect-id])})]
    [:li [:a attrs tab-name]]))

(defn frame-target-editor [timeline-id step-index]
  (let [selected (<sub [::subs/effect-chosen timeline-id step-index])
        {::keys [editor]} (get frame-target-editors selected)]
    (if editor
      [editor timeline-id step-index]
      [:p (str "Can't find editor '" selected "'!")])))

(defn frame-target-nav [timeline-id step-index]
  [:ul {:data-uk-tab ""}
   (for [effect-id all-effects
         :let [props (get frame-target-editors effect-id
                          (first frame-target-editors))]]
     ^{:key effect-id}
     [frame-target-tab timeline-id step-index effect-id props])])

(defn effect-chooser [timeline-id step-index]
  [:div
   ;; TODO: effect type, pause, etc
   [:h2 "Choose Frame Target"]
   [frame-target-nav timeline-id step-index]
   [frame-target-editor timeline-id step-index]])

(defn effect-display [timeline-id step-index effect-index effect]
  [:div
   [:p.uk-text-lead
    (str "Timeline " timeline-id ", step " step-index ", effect " effect-index)]
   [:p (str effect)]])
