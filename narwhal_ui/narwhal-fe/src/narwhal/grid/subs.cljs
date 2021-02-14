(ns narwhal.grid.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.grid.db :as db]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.util.color :as color]))

(rf/reg-sub
  ::active-tool
  (fn [db _] (db/active-tool db)))

(rf/reg-sub
  ::active-color
  (fn [db _] (db/active-color db)))

(rf/reg-sub
  ::swatches
  (fn [db _] (get-in db (db/grid-path :palette/swatches))))

(rf/reg-sub
  ::swatch-count
  :<- [::swatches]
  (fn [swatches _]
    (count swatches)))

(rf/reg-sub
  ::swatch
  :<- [::swatches]
  (fn [swatches [_ swatch-index]]
    (assert (number? swatch-index))
    (assert (<= swatch-index (count swatches)))
    (nth swatches swatch-index)))

(rf/reg-sub
  ::swatch-under-edit
  (fn [db _]
    (get-in db (db/grid-path :palette/swatch-edit))))

;; NB, this is the frame, not the metadata
(rf/reg-sub
  ::frame-data
  (fn [[_ frame-id]]
    (rf/subscribe [::frame-subs/frame-meta-by-id frame-id]))
  (fn [frame-metadata]
    (-> frame-metadata :frame)))

(rf/reg-sub
  ::pixels
  (fn [[_ frame-id]]
    (rf/subscribe [::frame-data frame-id]))
  (fn [frame]
    (-> frame :pixels)))

(rf/reg-sub
  ::single-pixel
  (fn [[_ frame-id index]]
    [(rf/subscribe [::pixels frame-id]) index])
  (fn [[pixels index]]
    (nth pixels index (::color/hot-pink color/named))))

