(ns app.main
  (:require [re-graph.core :as re-graph]
            [re-frame.core :as re-frame]))

(re-frame/dispatch [::re-graph/init {}])

(def value-a 1)
(defonce value-b 2)

(defn main! []
      (println "App loaded!"))

(defn reload! []
      (println "Code updated.")
      (println "Trying values:" value-a value-b))
