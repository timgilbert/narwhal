(ns narwhal.timeline.views.effects
  (:require [lambdaisland.glogi :as log]
    [narwhal.util.util :as util :refer [<sub >evt]]
    [narwhal.util.component :as component]
    [narwhal.timeline.subs :as subs]
    [narwhal.timeline.events :as events]))

(defn chooser-cancel-button [timeline-id step]
  [:button
   {:on-click #(>evt [::events/choose-effect timeline-id step nil])}
   [:span {:data-uk-icon "close"}]])

(defn chooser-confirm-button [timeline-id step]
  [:button
   {:on-click #(>evt [::events/choose-effect timeline-id step nil])}
   [:span {:data-uk-icon "check"}]])

(defn color-effect-editor [timeline-id step]
  [:div "color-effect-editor"
   [chooser-cancel-button timeline-id step]])

(defn random-effect-editor [timeline-id step]
  [:div
   "random-effect-editor"
   [chooser-cancel-button timeline-id step]])

(defn saved-frame-effect-editor [timeline-id step]
  [:div "saved-frame-effect-editor"
   [chooser-cancel-button timeline-id step]])

(defn saved-frame-chooser [timeline-id step]
  [:div.uk-card
   [:button.uk-button.uk-button-text
    {:on-click #(>evt [::events/choose-effect timeline-id step ::saved])}
    "saved frame"]])

(defn color-frame-chooser [timeline-id step]
  [:div.uk-card
   [:div.uk-card-body
    [:button.uk-button.uk-button-text
     {:on-click #(>evt [::events/choose-effect timeline-id step ::color])}
     "color"]]])

(defn random-frame-chooser [timeline-id step]
  [:div.uk-card
   [:div.uk-card-body
    [:p "tl: " timeline-id ", step: " step]
    [:button.uk-button.uk-button-text
     {:on-click #(>evt [::events/choose-effect timeline-id step ::random])}
     "random"]]])

(defn effect-selector [timeline-id step]
  [:div
   [:h2 "Choose Effect"]
   [:p "tl: " timeline-id ", step: " step]
   [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
    [saved-frame-chooser timeline-id step]
    [random-frame-chooser timeline-id step]
    [color-frame-chooser timeline-id step]]])

(def effect-editors
  {::color  color-effect-editor
   ::random random-effect-editor
   ::saved  saved-frame-effect-editor})

;; TODO: pass step in?
(defn effect-chooser [timeline-id step]
  (let [chosen    (<sub [::subs/effect-chosen timeline-id step])
        component (get effect-editors chosen effect-selector)]
    [:div
     [component timeline-id step]
     [:p "Chosen:" chosen ", tl: " timeline-id ", step: " step]]))

