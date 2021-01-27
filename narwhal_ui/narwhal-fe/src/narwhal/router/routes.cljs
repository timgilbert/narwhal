(ns narwhal.router.routes)
  ;(:require [narwhal.app.views.home :as home]))

;(def pages
;  "Big list of our top-level pages, along with their routes and any events
;  we should fire off when we navigate to them"
;  {:home-page/home     {::url   ""
;                        ::title "Home"
;                        ::page  home/home-page}
;   :timeline-page/list {::url   "timeline"
;                        ::title "New Timeline"}
;   :timeline-page/edit {::url   "timeline/:frame-id"
;                        ::title "Edit Timeline"}
;   :frame-page/list    {::url   "frame"
;                        ::title "Saved Frames"}
;   :frame-page/edit    {::url   "frame/:timeline-id"
;                        ::title "Edit Frame"}})


(def routes
  [["/"
    [""
     {:name :home-page/home}]
    ["frame"
     {:name :frame-page/list}]
    ["frame/:frame-id"
     {:name :frame-page/edit}]
    ["timeline"
     {:name :timeline-page/list}]
    ["timeline/:timeline-id"
     {:name :timeline-page/edit}]]])

(defn gen-routes []
  routes)
