(ns narwhal.app.db
  (:require [lambdaisland.glogi :as log]
            [narwhal.nav.db :as nav-db]
            [narwhal.frame.db :as frame-db]
            [narwhal.grid.db :as grid-db]
            [narwhal.util.util :as util]))

(defn initial-db [db]
  (-> db
      nav-db/init-db
      frame-db/init-db
      grid-db/init-db))
