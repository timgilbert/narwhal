(ns user
  (:require [figwheel.main.api :as fig]))

(defn cljs-repl []
  (fig/start "app")) ; "app" is the build profile
