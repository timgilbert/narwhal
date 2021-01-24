(ns narwhal.nav.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.views.component :as component]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.nav.events :as events]
            [narwhal.nav.subs :as subs]))

(defn top-nav []
  [:nav.uk-navbar-container {:data-uk-navbar ""}
   [:div.uk-navbar-left
    [:ul.uk-navbar-nav
     [:li.uk-active
      [:a.brand {:href "/"}
       [:img {:class "logo" :src "/images/narwhal.svg" :height 50 :width 50}]
       [:span "Narwhal"]]]]]
   [:div.uk-navbar-right
    [:ul.uk-navbar-nav
     [:li [component/link :timeline/new "New Timeline"]]
     [:li [component/link :frame/new "New Frame"]]
     ;; TODO: below should be conditional based on elixir settings
     [:li [:a {:href "/dashboard"} "Dashboard"]]]]])

(defn frame-nav-item
  [{::subs/keys [item item-id active?]}]
  (let [href (str "/frame" (when (not= item-id util/default-frame-id)
                             (str "/" item-id)))
        class (if active? "uk-active" "")]
    [:li
     [:a {:href href :class class}
      (:name item)]]))

(defn side-nav-menu
  [menu-sub last-component]
  (into [:ul.uk-sub-nav]
        (concat
          (for [{::subs/keys [item-id] :as entry} (log/spy (<sub menu-sub))
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
        [:li [:a {:href "/frame"}
              [component/icon "plus-circle"]
              "Create Frame"]]]]]]))
