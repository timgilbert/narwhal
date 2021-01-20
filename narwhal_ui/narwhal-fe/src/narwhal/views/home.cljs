(ns narwhal.views.home)

(defn timeline-list []
  (let [timeline []]
    [:h2 "timelines"]))

(defn frame-list []
  (let [frames []]
    [:h2 "frames"]))

(defn home [slug]
  [:article
   [:img {:src "/images/sea-unicorn.jpg" :width 800 :height 583}]
   [:h1.uk-heading-small.uk-heading-divider "Welcome to Narwhal"]
   [:p "To get started, create a new timeline or a new frame."]])
