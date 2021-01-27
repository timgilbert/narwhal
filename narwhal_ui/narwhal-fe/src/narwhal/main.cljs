(ns ^:figwheel-hooks narwhal.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [lambdaisland.glogi.console :as glogi-console]
            [lambdaisland.glogi :as log]
            [narwhal.app.views.root :as root]
            [narwhal.app.events :as events]
            [narwhal.router.core :as router]
            narwhal.graphql))

(defn init-logging!
  ([] (init-logging! :trace))
  ([level]
   (glogi-console/install!)
   (log/set-levels {:glogi/root level})))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(defn mount-app-element! []
  (when-let [el (js/document.getElementById "app")]
    (rdom/render [root/app] el)))

(defn start! []
  (init-logging!)
  (router/start!)
  (rf/dispatch-sync [::events/initialize-app])
  (mount-app-element!))

(start!)
