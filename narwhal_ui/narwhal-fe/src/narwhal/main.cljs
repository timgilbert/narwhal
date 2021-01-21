(ns ^:figwheel-hooks narwhal.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]
            [narwhal.app :as app]
            [narwhal.router :as router]
            narwhal.events
            narwhal.graphql
            narwhal.subs))


(defn mount-app-element! []
  (when-let [el (js/document.getElementById "app")]
    (rdom/render [app/app] el)))

(defn start! []
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
