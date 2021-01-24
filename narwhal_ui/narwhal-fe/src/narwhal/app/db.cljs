(ns narwhal.app.db
  (:require [lambdaisland.glogi :as log]
            [narwhal.nav.db :as nav-db]
            [narwhal.frame.db :as frame-db]
            [narwhal.util :as util]))

(defn initial-db [db]
  (-> db
      (merge {:palette/active-color util/black})
      nav-db/init-db
      frame-db/init-db))
