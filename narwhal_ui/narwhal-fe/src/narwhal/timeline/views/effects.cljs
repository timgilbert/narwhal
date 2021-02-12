(ns narwhal.timeline.views.effects
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.util.component :as component]
            [narwhal.timeline.subs :as subs]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.timeline.events :as events]
            [narwhal.frame.views.list :as frame-list]
            [re-frame.core :as rf]
            [narwhal.util.color :as color]))

(defn solid-frame-target-editor
  [timeline-id step-index effect-index]
  (let [color (<sub [::subs/solid-frame-color
                     timeline-id step-index effect-index])]
    [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
     [frame-list/solid-frame-display true color]]))

(defn random-frame-target-editor
  [_timeline-id _step-index _effect-index]
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [frame-list/random-frame-display true]])

(defn no-saved-frames-message []
  [:div.uk-flex.uk-flex-wrap.uk-flex-wrap-around
   [:div.uk-card.uk-card-body.uk-card-small
    [:p "No saved frames available! Create one first"]]])

(defn saved-frame-target-editor
  [timeline-id step-index effect-index]
  [:div
   (let [frames      (<sub [::frame-subs/all-frame-metadata])
         curr-target (<sub [::subs/selected-saved-frame-target
                            timeline-id step-index effect-index])]
     (if (empty? frames)
       [no-saved-frames-message]
       [frame-list/frame-list
        #:frame-list{:active-id   curr-target
                     :click-event [::events/select-saved-frame-target
                                   timeline-id step-index effect-index]}]))])

(def frame-target-editors
  {:SOLID_FRAME  {::editor   solid-frame-target-editor
                  ::tab-name "Solid Color"}
   :RANDOM_FRAME {::editor   random-frame-target-editor
                  ::tab-name "Random Frame"}
   :SAVED_FRAME  {::editor   saved-frame-target-editor
                  ::tab-name "Saved Frame"}
   ::all         [:RANDOM_FRAME :SAVED_FRAME :SOLID_FRAME]})

;; TODO: disable frame tab if no saved frames
(defn frame-target-tab [timeline-id step-index effect-index target-type
                        {::keys [tab-name]}]
  (let [current (<sub [::subs/frame-target timeline-id
                       step-index effect-index])
        active? (= (:type current) target-type)]
    [:li (when active? {:class "uk-active"})
     (if active?
       [:a tab-name]
       [:a {:on-click #(>evt [::events/choose-target-type
                              timeline-id step-index effect-index
                              target-type])}
        tab-name])]))

(defn frame-target-editor [timeline-id step-index effect-index]
  (let [target (<sub [::subs/frame-target timeline-id step-index
                      effect-index])
        type   (:type target)
        {::keys [editor]} (get frame-target-editors type)]
    (if editor
      [editor timeline-id step-index effect-index]
      [:p (str "Can't find editor '" type "'!")])))

(defn frame-target-nav [timeline-id step-index effect-index]
  [:ul.uk-subnav.uk-subnav-pill
   (for [target-type (::all frame-target-editors)
         :let [props (get frame-target-editors target-type
                          (first frame-target-editors))]]
     ^{:key target-type}
     [frame-target-tab timeline-id step-index effect-index
      target-type props])])

(defn frame-target-chooser [timeline-id step-index effect-index]
  [:div
   [:p "Choose Frame Target"]
   [frame-target-nav timeline-id step-index effect-index]
   [frame-target-editor timeline-id step-index effect-index]])

(defn tween-controls [timeline-id step-index effect-index effect]
  [:p "tween-controls, granularity "
   (:granularity effect) ", durationMs " (:durationMs effect)])

(def effect-types
  {:REPLACE_EFFECT {::text   "Replace"
                    ::frame? true}
   :TWEEN_EFFECT   {::text     "Tween"
                    ::frame?   true
                    ::controls tween-controls}
   ::all           [:REPLACE_EFFECT :TWEEN_EFFECT]})

(defn effect-type-controls [timeline-id step-index effect-index]
  (assert (some? timeline-id))
  (let [effect    (<sub [::subs/effect timeline-id step-index effect-index])
        curr-type (:type effect)]
    [:div
     [:ul.uk-subnav.uk-subnav-pill
      (for [eff-type (::all effect-types)
            :let [{::keys [text]} (get effect-types eff-type)
                  active? (= curr-type eff-type)]]
        ^{:key eff-type}
        [:li (when active? {:class "uk-active"})
         [:a
          {:on-click #(>evt [::events/choose-effect-type timeline-id
                             step-index effect-index eff-type])}
          text]])]
     (when-let [controls (get-in effect-types [curr-type ::controls])]
       [controls timeline-id step-index effect-index effect])
     (when (get-in effect-types [curr-type ::frame?])
       [frame-target-chooser timeline-id step-index effect-index])]))

(defn effect-chooser [timeline-id step-index]
  [:div
   (assert (some? timeline-id))
   [effect-type-controls timeline-id step-index 0]])

(defn effect-display [timeline-id step-index effect-index effect]
  [:div
   [:p.uk-text-muted
    (str "Timeline " timeline-id ", step " step-index ", effect " effect-index)]
   [:p (str effect)]])
