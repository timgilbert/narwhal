(ns narwhal.views.nav
  (:require [narwhal.util :as util]))

(defn nav [title]
  [:nav
   [:ul
    [:li (str "Narwhal: " title)]
    [:li [util/link :home/home "Home"]]
    [:li [util/link :timeline/new "New Timeline"]]
    [:li [util/link :frame/new "New Frame"]]]])
