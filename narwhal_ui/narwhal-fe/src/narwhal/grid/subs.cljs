(ns narwhal.grid.subs
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.grid.db :as db]))

(rf/reg-sub
  ::active-tool
  (fn [db _] (db/active-tool db)))

(rf/reg-sub
  ::active-color
  (fn [db _] (db/active-color db)))
