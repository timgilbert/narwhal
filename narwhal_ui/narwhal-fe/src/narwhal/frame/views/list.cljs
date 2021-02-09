(ns narwhal.frame.views.list
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.events :as events]
            [narwhal.frame.subs :as subs]
            [narwhal.nav.subs :as nav-subs]
            [fork.re-frame :as fork]
            [narwhal.grid.views :as grid]
            [re-frame.core :as rf]))

(defn frame-clicker
  [frame-id click-event & children]
  (let [click-fn (when (vector? click-event)
                   #(>evt (conj click-event frame-id)))
        root     (if click-fn
                   [:a {:on-click click-fn}]
                   [:div])]
    (into root children)))

(defn frame-display
  [frame-id frame-name active? click-event]
  (let [card-attr (if active? {:class "uk-card-primary"}
                              {:class "uk-card-default"})]
    [:div.uk-card.uk-card-body.uk-card-small.uk-card-hover
     card-attr
     [frame-clicker frame-id click-event
      [:div.uk-media-top.uk-align-center
       [grid/thumbnail-grid frame-id "100px"]]
      [:div.uk-card-footer
       [:p.uk-align-center.uk-text-center
        frame-name]]]]))

(defn frame-list
  [{:frame-list/keys [active-id click-event]}]
  (let [frame-meta (<sub [::subs/all-frame-metadata])]
    [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
     (for [{:keys [id name] :as f} frame-meta
           :let [active? (= active-id id)]]
       ^{:key id}
       [frame-display id name active? click-event])]))

(defn create-frame-button []
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [::events/new-blank-frame])}
   "Create Frame"])

(rf/reg-event-fx
  ::frame-page-click
  (fn [_cofx [_ frame-id]]
    {:dispatch [:route/nav :frame-page/edit {:frame-id frame-id}]}))

(defn frame-list-page []
  [:div
   [:h1 "Saved Frames"]
   [frame-list {:frame-list/click-event [::frame-page-click]}]
   [create-frame-button]])
