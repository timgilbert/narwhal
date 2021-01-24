(ns narwhal.app
  (:require [lambdaisland.glogi :as log]
            [narwhal.views.nav :as nav]
            [narwhal.views.home :as home]
            [narwhal.views.timeline :as timeline]
            [narwhal.views.frame :as frame]
            [narwhal.util :as util :refer [<sub]]))

(def handlers
  {:home/home    home/home
   :timeline/new timeline/new-timeline
   :frame/new    frame/frame-editor
   :frame/edit   frame/frame-editor})

(defn app
  []
  (let [active  (<sub [:page/active])
        slug    (<sub [:page/slug])
        title   (<sub [:page/title])
        handler (get handlers (or active :home/home))]
    (log/debug :foo [active slug title])
    [:div
     [nav/top-nav title]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler slug]]]]))
