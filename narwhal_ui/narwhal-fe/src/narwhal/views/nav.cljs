(ns narwhal.views.nav
  (:require [narwhal.util :as util :refer [<sub]]
            [re-frame.core :as rf]))

(defn picnic-nav [title]
  [:nav
   [:a.brand {:href "/"}
    ;[:img {:class "logo" :src "/images/narwhal.svg"}]
    [:span "Narwhal"]]
   [:div.menu
    [util/link :timeline/new "New Timeline"]
    [util/link :frame/new "New Frame"]
    ;; TODO: below should be conditional based on elixir settings
    [:a {:href "/dashboard"} "Dashboard"]]])

(defn top-nav [title]
  [:nav.uk-navbar-container {:data-uk-navbar ""}
   [:div.uk-navbar-left
    [:ul.uk-navbar-nav
     [:li.uk-active
      [:a.brand {:href "/"}
       ;[:img {:class "logo" :src "/images/narwhal.svg"}]
       [:span "Narwhal"]]]]]
   [:div.uk-navbar-right
    [:ul.uk-navbar-nav
     [:li [util/link :timeline/new "New Timeline"]]
     [:li [util/link :frame/new "New Frame"]]
     ;; TODO: below should be conditional based on elixir settings
     [:li [:a {:href "/dashboard"} "Dashboard"]]]]])

(defn side-nav []
  (let [timeline-class (if (<sub [:page/timeline?]) " uk-active" "")
        frame-class    (if (<sub [:page/frame?]) " uk-active" "")]
    [:div
     [:ul.uk-nav-primary.uk-nav-parent-icon {:data-uk-nav ""}
      [:li {:class (str "uk-parent" timeline-class)}
       [:a {:href "#"} "Timelines"]
       [:ul.uk-sub-nav
        [:li [:a {:href "/timeline"}
              [util/icon "plus-circle"]
              "Create Timeline"]]]]
      [:li {:class (str "uk-parent" frame-class)}
       [:a {:href "#"} "Frames"]
       [:ul.uk-sub-nav
        [:li [:a {:href "/frame"}
              [util/icon "plus-circle"]
              "Create Frame"]]]]]]))
