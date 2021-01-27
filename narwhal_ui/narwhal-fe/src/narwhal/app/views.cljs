(ns narwhal.app.views
  (:require [lambdaisland.glogi :as log]
            [narwhal.nav.views :as nav]
            [narwhal.nav.subs :as nav-subs]
            [narwhal.timeline.views :as timeline]
            [narwhal.frame.views.editor :as frame-edit]
            [narwhal.frame.views.list :as frame-list]
            [narwhal.util.util :as util :refer [<sub]]
            [narwhal.util.component :as component]
            narwhal.app.events))

(defn home-page []
  [:article
   [:img {:src "/images/sea-unicorn.jpg" :width 800 :height 583}]
   [:h1.uk-heading-small.uk-heading-divider "Welcome to Narwhal"]
   [:p "To get started, create a new timeline or a new frame."]
   [:p
    [component/icon "github"]
    [:span.uk-text-middle [:a {:href "https://github.com/timgilbert/narwhal"}
                           " Github"]]]
   [:p (str "This site uses images from 'Narwhal by Kelly Robinson "
            "from the Noun Project'. Thanks!")]])

(def handlers
  {:home-page/home     home-page
   :timeline-page/list timeline/timeline-list-page
   :frame-page/list    frame-list/frame-list-page
   :frame-page/edit    frame-edit/frame-editor-page})

(defn app
  []
  (let [active  (<sub ::nav-subs/active-page)
        item-id (<sub ::nav-subs/active-id)
        handler (get handlers (or active :home-page/home))]
    (assert (some? active))
    (assert (some? handler))
    [:div
     [nav/top-nav]
     [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
      [:div {:class "uk-width-1-6@s"}
       [nav/side-nav]]
      [:div {:class "uk-width-expand"}
       [handler item-id]]]]))
