(ns narwhal.nav.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.nav.events :as events]
            [narwhal.frame.events :as frame-events]
            [narwhal.nav.subs :as subs]
            [narwhal.frame.subs :as frame-subs]))

(defn top-nav []
  [:nav.uk-navbar-container {:data-uk-navbar ""}
   [:div.uk-navbar-left
    [:ul.uk-navbar-nav
     [:li (when (<sub [::subs/page-type? :home-page])
            {:class "uk-active"})
      [:a.brand {:href "/"}
       [:img {:class "logo" :src "/images/narwhal.svg" :height 50 :width 50}]
       [:span "Narwhal"]]]]]
   [:div.uk-navbar-right
    [:ul.uk-navbar-nav
     [:li (when (<sub [::subs/page-type? :timeline-page])
            {:class "uk-active"})
      [component/link :timeline-page/list "Timelines"]]
     [:li (when (<sub [::subs/page-type? :frame-page])
            {:class "uk-active"})
      [component/link :frame-page/list "Frames"]]
     ;; TODO: below should be conditional based on elixir settings
     [:li [:a {:href "/dashboard" :target "_new"} "Dashboard"]]]]])

(defn frame-nav-item
  [{::subs/keys [item-id item active?] :as e}]
  [:li {:class (if active? "uk-active" "")}
   [component/link
    :frame-page/edit
    {:frame-id item-id}
    {}
    (str (:name item)
         (when (<sub [::frame-subs/dirty? item-id]) " *"))]])

(defn side-nav-menu
  [menu-sub last-component]
  (into
    [:ul.uk-nav-sub]
    (concat
      (for [{::subs/keys [item-id] :as entry} (<sub menu-sub)
            :let [key (str menu-sub "." item-id)]]
        ;; TODO: handle timelines
        ^{:key key}
        [frame-nav-item entry])
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
     [:ul.uk-nav.uk-nav-side.uk-nav-default.uk-nav-center
      [:li timeline-attrs
       [component/link :timeline-page/list "Timelines"]
       [side-nav-menu ::subs/timelines
        [:li [:a {:href "/timeline"}
              [component/icon "plus-circle"]
              " [new timeline]"]]]]

      [:li frame-attrs
       [component/link :frame-page/list "Frames"]
       [side-nav-menu ::subs/frames
        [:li [:a {:href "" :on-click #(>evt [::frame-events/new-blank-frame])}
              [component/icon "plus-circle"]
              " [new frame]"]]]]]]))
