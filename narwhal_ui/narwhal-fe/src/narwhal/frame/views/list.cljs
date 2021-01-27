(ns narwhal.frame.views.list
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.events :as events]
            [narwhal.frame.subs :as subs]
            [narwhal.nav.subs :as nav-subs]
            [fork.re-frame :as fork]
            [narwhal.grid.views :as grid]))

(defn frame-display
  [frame-id frame-name]
  [:div.uk-card.uk-card-default.uk-card-body.uk-card-small
   [:div.uk-media-top.uk-align-center
    [grid/thumbnail-grid frame-id "100px"]]
   [:div.uk-card-footer
    [:p.uk-align-center.uk-text-center
     frame-name]]])

(defn frame-list
  []
  (let [frame-meta (<sub [::subs/all-frames])]
    [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
     (for [{:keys [id name] :as f} frame-meta]
       ^{:key id}
       [frame-display id name])]))

(defn create-frame-button []
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [::events/new-blank-frame])}
   "Create Frame"])

(defn frame-list-page []
  [:div
   [:h1 "Saved Frames"]
   [frame-list]
   [create-frame-button]])
