(ns narwhal.grid.db
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.db :as frame-db]))

;; ----------------------------------------------------------------------
;; Tool stuff

(defn grid-path
  ([& rest]
   (concat [:g/grid] rest)))

(defn active-tool [db]
  (get db (grid-path :grid/active-tool) :tools/pencil))

(defn set-active-tool [db tool]
  (assoc-in db (grid-path :grid/active-tool) tool))

(defn active-color [db]
  (get db (grid-path :palette/active-color)))

(defn set-active-palette-color [db color]
  (assoc-in db (grid-path :palette/active-color) color))

;; ----------------------------------------------------------------------
;; Tool stuff

(defn pixel-by-frame-id [db frame-id pixel-index]
  (get-in (frame-db/frame-by-id db frame-id)
          [:frame :pixels pixel-index]))

(defn pencil-click [db frame-id index color]
  (if (= (pixel-by-frame-id db frame-id index) color)
    db
    (-> db
        (assoc-in [::frames ::dirty?] true)
        (assoc-in [::frames ::named frame-id :frame :pixels index] color))))

(defn bucket-click [db frame-id _index color]
  (let [frame  (frame-db/frame-by-id db frame-id)
        total  (* (:height frame) (:width frame))
        pixels (into [] (repeat total color))]
    (-> db
        (assoc-in [::frames ::dirty?] true)
        (assoc-in [::frames ::named frame-id :frame :pixels] pixels))))


(defn init-db [db]
  (-> db
      (set-active-palette-color "#000000")
      (set-active-tool :tools/pencil)))
