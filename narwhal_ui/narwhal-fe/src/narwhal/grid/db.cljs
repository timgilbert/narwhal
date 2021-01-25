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
  (get-in db (grid-path :grid/active-tool) :tools/pencil))

(defn set-active-tool [db tool]
  (assoc-in db (grid-path :grid/active-tool) tool))

(defn active-color [db]
  (get-in db (grid-path :palette/active-color)))

(defn set-active-palette-color [db color]
  (assoc-in db (grid-path :palette/active-color) color))

;; ----------------------------------------------------------------------
;; rework

(defn pixel-by-frame-id [db frame-id pixel-index]
  (get-in (frame-db/frame-by-id db frame-id)
          [:frame :pixels pixel-index]))

(defn set-all-pixels-by-frame-id [db frame-id pixels]
  (assoc-in db
            (frame-db/frame-path :f/all frame-id :frame :pixels)
            pixels))

(defn set-pixel-by-frame-id [db frame-id pixel-index color]
  ;; Should we construct the pixels and use set-all-pixels-by-frame-id?
  (assoc-in db
            (frame-db/frame-path :f/all frame-id
                                 :frame :pixels pixel-index)
            color))

(defn pencil-click [db frame-id index color]
  (if (= (pixel-by-frame-id db frame-id index) color)
    db
    (-> db
        (frame-db/set-dirty frame-id)
        (set-pixel-by-frame-id frame-id index color))))

(defn bucket-click [db frame-id _index color]
  (let [frame  (frame-db/frame-by-id db frame-id)
        total  (* (-> frame :frame :height) (-> frame :frame :width))
        pixels (into [] (repeat total color))]
    (-> db
        (frame-db/set-dirty frame-id)
        (set-all-pixels-by-frame-id frame-id (log/spy  pixels)))))


(defn init-db [db]
  (-> db
      (set-active-palette-color "#000000")
      (set-active-tool :tools/pencil)))
