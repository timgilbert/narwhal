(ns narwhal.app
  (:require [lambdaisland.glogi :as log]
    ;[narwhal.views.nav :as nav]
            [narwhal.nav.views :as nav]
            [narwhal.nav.subs :as nav-subs]
            [narwhal.views.home :as home]
            [narwhal.views.timeline :as timeline]
            [narwhal.views.frame :as frame]
            [narwhal.util :as util :refer [<sub]]))

(def handlers
  {:home-page/home    home/home
   :timeline-page/new timeline/new-timeline
   :frame-page/new    frame/frame-editor
   :frame-page/edit   frame/frame-editor})

(defn app
  []
  (let [active  (<sub ::nav-subs/active-page)
        item-id (<sub ::nav-subs/active-id)
        handler (get handlers (or active :home-page/home))]
    (log/debug :nav [active item-id])
    [:div
     [nav/top-nav]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler item-id]]]]))
