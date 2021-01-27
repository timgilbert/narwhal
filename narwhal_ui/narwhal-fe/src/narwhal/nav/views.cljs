(ns narwhal.nav.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.nav.events :as events]
            [narwhal.frame.events :as frame-events]
            [narwhal.nav.subs :as subs]))

(defn top-nav []
  [:nav.uk-navbar-container {:data-uk-navbar ""}
   [:div.uk-navbar-left
    [:ul.uk-navbar-nav
     [:li.uk-active
      [:a.brand {:href "/"}
       [:img {:class "logo" :src "/images/narwhal.svg" :height 50 :width 50}]
       [:span "Narwhal"]]]
     [:li [:button.uk-text
           {:on-click #(>evt [:route/nav :timeline-page/list])}
           "tl"]]
     [:li [:button.uk-text
           {:on-click #(>evt [:route/nav :frame-page/list])}
           "fr"]]]]
   [:div.uk-navbar-right
    [:ul.uk-navbar-nav
     [:li [component/link :timeline-page/list "Timelines"]]
     [:li [component/link :frame-page/list "Frames"]]
     ;; TODO: below should be conditional based on elixir settings
     [:li [:a {:href "/dashboard" :target "_new"} "Dashboard"]]]]])

(defn frame-nav-item
  [{::subs/keys [item item-id active?]}]
  (let [href (str "/frame/" item-id) ; TODO: use route/whatever
        class (if active? "uk-active" "")]
    [:li
     [:a {:href href :class class}
      (:name item)]]))

(defn side-nav-menu
  [menu-sub last-component]
  (into [:ul.uk-sub-nav]
        (concat
          (for [{::subs/keys [item-id] :as entry} (<sub menu-sub)
                :let [key (str menu-sub "." item-id)]]
            ^{:key key}
            [frame-nav-item entry]) ; TODO: handle timelines
          ^{:key (str menu-sub ".default")}
          [last-component])))

(defn side-nav []
  (let [active-attrs   {:class (str "uk-parent uk-active")}
        inactive-attrs {:class (str "uk-parent")}
        timeline-attrs (if (<sub [::subs/page-type? :timeline-page])
                         active-attrs inactive-attrs)
        frame-attrs    (if (<sub [::subs/page-type? :frame-page])
                         active-attrs inactive-attrs)]
    [:div
     [:ul.uk-nav.uk-nav-side.uk-nav-primary
      [:li timeline-attrs
       [:a {:href "#"} "Timelines"]
       [side-nav-menu ::subs/timelines
        [:li [:a {:href "/timeline"}
              [component/icon "plus-circle"]
              "Create Timeline"]]]]

      [:li frame-attrs
       [:a {:href "#"} "Frames"]
       [side-nav-menu ::subs/frames
        [:li [:button.uk-button.uk-button-link
              {:on-click #(>evt [::frame-events/new-blank-frame])}
              [component/icon "plus-circle"]
              " Create Frame"]]]]]]))
