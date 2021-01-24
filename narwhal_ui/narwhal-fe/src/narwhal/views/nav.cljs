(ns narwhal.views.nav
  (:require [narwhal.util :as util :refer [<sub]]
            [narwhal.views.component :as component]
            [re-frame.core :as rf]))

(defn top-nav [title]
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

(defn frame-nav-item [frame]
  [:li
   [:a {:href (str "/frame/" (:id frame))}
    (:name frame)]])

(defn side-nav-menu
  [menu-sub last-component]
  (into [:ul.uk-sub-nav]
        (concat
          (for [frame (<sub [menu-sub])]
            ^{:key (str menu-sub (:id frame))}
            [frame-nav-item frame])
          [last-component])))

(defn side-nav []
  (let [active-attrs   {:class (str "uk-parent uk-active")}
        inactive-attrs {:class (str "uk-parent")}
        timeline-attrs (if (<sub [:page/timeline?]) active-attrs inactive-attrs)
        frame-attrs    (if (<sub [:page/frame?]) active-attrs inactive-attrs)]
    [:div
     [:ul.uk-nav.uk-nav-side.uk-nav-primary
      ;{:data-uk-nav "{multiple:true}"}
      [:li timeline-attrs
       [:a {:href "#"} "Timelines"]
       [side-nav-menu :timeline/all-timelines
        [:li [:a {:href "/timeline"}
              [component/icon "plus-circle"]
              "Create Timeline"]]]]

      [:li frame-attrs
       [:a {:href "#"} "Frames"]
       [side-nav-menu :frame/all-frames
        [:li [:a {:href "/timeline"}
              [component/icon "plus-circle"]
              "Create Timeline"]]]]]]))
