(ns narwhal.nav.db
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]))

(defn page-path
  ([& rest]
   (concat [:p/pages] rest)))

(defn set-page [db {:route/keys [page id]}]
  (-> db
      (assoc-in (page-path :nav/page) page)
      (assoc-in (page-path :nav/id) id)))

(defn init-db
  [db]
  (-> db
      (set-page {:route/page :home-page/home})
      (assoc :nav/current-route {:name :home-page/home})))