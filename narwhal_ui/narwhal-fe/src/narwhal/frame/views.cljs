(ns narwhal.frame.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.views.component :as component]
            [narwhal.util :as util :refer [<sub >evt]]
            narwhal.frame.events
            narwhal.frame.subs))
