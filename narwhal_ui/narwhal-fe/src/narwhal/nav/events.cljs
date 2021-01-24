(ns narwhal.nav.events
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]))

(rf/reg-event-db
  :nav-gql/nav-loaded
  (fn [db [_ payload]]
    (-> db (assoc-in [::nav ::frames] (:frames payload))
        (assoc-in [::nav ::timelines] (:timelines payload)))))
