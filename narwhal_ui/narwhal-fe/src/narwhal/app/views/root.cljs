(ns narwhal.app.views.root
  (:require [lambdaisland.glogi :as log]
            [narwhal.nav.views :as nav]
            [narwhal.nav.subs :as nav-subs]
            [narwhal.router.subs :as router-subs]
            [narwhal.timeline.views.list :as timeline-list]
            [narwhal.timeline.views.editor :as timeline-edit]
            [narwhal.frame.views.editor :as frame-edit]
            [narwhal.frame.views.list :as frame-list]
            [narwhal.app.views.home :as home]
            [narwhal.util.util :as util :refer [<sub]]
            [narwhal.util.component :as component]
            narwhal.app.events))

(def handlers
  {:home-page/home     home/home-page
   :timeline-page/list timeline-list/timeline-list-page
   :timeline-page/edit timeline-edit/timeline-edit-page
   :frame-page/list    frame-list/frame-list-page
   :frame-page/edit    frame-edit/frame-editor-page})

(defn app
  []
  (let [route   (<sub ::router-subs/current-route)
        page    (or (-> route :data :name) :home-page/home)
        handler (get handlers page component/error-page)]
    (assert (some? route))
    (assert (some? handler))
    [:div
     [nav/top-nav]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler route]]]]))
