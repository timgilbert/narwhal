(ns narwhal.app
  (:require [narwhal.views.nav :as nav]
            [narwhal.views.home :as home]
            [narwhal.views.timeline :as timeline]
            [narwhal.views.frame :as frame]
            [re-frame.core :as rf]))

(def handlers
  {:home/home    home/home
   :timeline/new timeline/new-timeline
   :frame/new    frame/new-frame})

(defn app
  []
  (let [active  @(rf/subscribe [:page/active])
        slug    @(rf/subscribe [:page/slug])
        title   @(rf/subscribe [:page/title])
        handler (get handlers (or active :home/home))]
    [:div
     [nav/top-nav title]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler slug]]]]))
