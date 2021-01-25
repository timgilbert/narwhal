(ns narwhal.grid.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.grid.db :as db]
            [narwhal.frame.subs :as frame-subs]))

(rf/reg-sub
  ::active-tool
  (fn [db _] (db/active-tool db)))

(rf/reg-sub
  ::active-color
  (fn [db _] (db/active-color db)))

(rf/reg-sub
  ::pixels
  (fn [_ frame-id]
    (rf/subscribe [::frame-subs/frame frame-id]))
  (fn [frame]
    (get-in frame [:frame :pixels])))
