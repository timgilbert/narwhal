(ns narwhal.app.views.home
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub]]
            [narwhal.util.component :as component]))

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
