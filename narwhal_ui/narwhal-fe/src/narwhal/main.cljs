(ns ^:figwheel-hooks narwhal.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [lambdaisland.glogi.console :as glogi-console]
            [lambdaisland.glogi :as log]
            [narwhal.app.views :as app]
            [narwhal.app.router :as router]
            narwhal.graphql))

(defn init-logging!
  ([] (init-logging! :trace))
  ([level]
   (glogi-console/install!)
   (log/set-levels {:glogi/root level})))

(defn mount-app-element! []
  (when-let [el (js/document.getElementById "app")]
    (rdom/render [app/app] el)))

(defn start! []
  (init-logging!)
  (router/start!)
  ;(re-graph/init re-graph-options)
  (rf/dispatch-sync [:initialize-db])
  ;(rf/dispatch-sync [::re-graph/init {:ws nil :http {}}])
  (mount-app-element!))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(start!)

;; specify reload hook with ^;after-load metadata
;(defn ^:after-load on-reload []
;  (mount-app-element))
;  ;; optionally touch your app-state to force rerendering depending on
;  ;; your application
;  ;; (swap! app-state update-in [:__figwheel_counter] inc)
;
