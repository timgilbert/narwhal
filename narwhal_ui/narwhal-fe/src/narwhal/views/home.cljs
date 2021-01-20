(ns narwhal.views.home)

(defn timeline-list []
  (let [timeline []]
    [:h2 "timelines"]))

(defn frame-list []
  (let [frames []]
    [:h2 "frames"]))

(defn home [slug]
  [:main
   [:h1 "Home"]
   [timeline-list]
   [frame-list]])
