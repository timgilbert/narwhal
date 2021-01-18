(ns ^:figwheel-hooks narwhal.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [narwhal.app :as app]
            [narwhal.router :as router]
            narwhal.events
            narwhal.subs))

(defn mount-app-element! []
  (when-let [el (js/document.getElementById "app")]
    (rdom/render [app/app] el)))

(defn start! []
  (router/start!)
  (rf/dispatch-sync [:initialize-db])
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
