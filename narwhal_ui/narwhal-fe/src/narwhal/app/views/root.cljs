(ns narwhal.app.views.root
  (:require [lambdaisland.glogi :as log]
            [narwhal.nav.views :as nav]
            [narwhal.nav.subs :as nav-subs]
            [narwhal.router.subs :as router-subs]
            [narwhal.timeline.views :as timeline]
            [narwhal.frame.views.editor :as frame-edit]
            [narwhal.frame.views.list :as frame-list]
            [narwhal.app.views.home :as home]
            [narwhal.util.util :as util :refer [<sub]]
            [narwhal.util.component :as component]
            narwhal.app.events))

(def handlers
  {:home-page/home     home/home-page
   :timeline-page/list timeline/timeline-list-page
   :frame-page/list    frame-list/frame-list-page
   :frame-page/edit    frame-edit/frame-editor-page})

(defn app
  []
  (let [;handler home/home-page
        ;item-id ""
        route   (<sub ::router-subs/current-route)
        page    (or (-> route :data :name) :home-page/home)
        handler (get handlers page)]
    (log/info :route route)
    ;(assert (some? active))
    ;(assert (some? handler))
    [:div
     [nav/top-nav]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler route]]]]))
