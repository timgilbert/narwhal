(ns narwhal.router.routes)

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
