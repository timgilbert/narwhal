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

(defn color-effect-editor [timeline-id step]
  [:div
   "color-effect-editor"])

(defn random-effect-editor [timeline-id step]
  [:div
   "random-effect-editor"])

(defn no-saved-frames-message []
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [:div.uk-card.uk-card-body.uk-card-small
    [:p "No saved frames available! Create one first"]]])

(defn saved-frame-effect-editor [timeline-id step]
  [:div
   (let [frames (<sub [::frame-subs/all-frame-metadata])
         curr-target (<sub [::subs/selected-saved-frame-target timeline-id step])]
     (if (empty? frames)
       [no-saved-frames-message]
       [frame-list/frame-list
        #:frame-list{:active-id   curr-target
                     :click-event [::events/select-saved-frame-target
                                   timeline-id step]}]))])

(def effect-editors
  {::color  {::editor   color-effect-editor
             ::tab-name "Solid Color"}
   ::random {::editor   random-effect-editor
             ::tab-name "Random Frame"}
   ::saved  {::editor   saved-frame-effect-editor
             ::tab-name "Saved Frame"}})

(def all-effects [::saved ::random ::color])

(defn effect-tab [timeline-id step effect-id {::keys [tab-name]}]
  (let [current (<sub [::subs/effect-chosen timeline-id step])
        active? (= current effect-id)
        attrs   (if active?
                  {:class "uk-active"}
                  {:on-click #(>evt [::events/choose-effect
                                     timeline-id step effect-id])})]
    [:li [:a attrs tab-name]]))

(defn effect-editor [timeline-id step]
  (let [selected-editor (<sub [::subs/effect-chosen timeline-id step])
        {::keys [editor]} (get effect-editors selected-editor)]
    (if editor
      [editor timeline-id step]
      [:p (str "Can't find editor '" selected-editor "'!")])))

(defn effect-nav [timeline-id step]
  [:ul {:data-uk-tab ""}
   (for [effect-id all-effects
         :let [props (get effect-editors effect-id (first effect-editors))]]
     ^{:key effect-id}
     [effect-tab timeline-id step effect-id props])])

(defn effect-chooser [timeline-id step]
  [:div
   [:h2 "Choose Effect"]
   [effect-nav timeline-id step]
   [effect-editor timeline-id step]])

(defn effect-display [timeline-id step-index effect-index effect]
  [:div
   [:p.uk-text-lead
    (str "Timeline " timeline-id ", step " step-index ", effect " effect-index)]
   [:p (str effect)]])