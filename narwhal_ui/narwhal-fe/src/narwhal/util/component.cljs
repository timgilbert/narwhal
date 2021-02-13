(ns narwhal.util.component
  (:require [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.router.core :as router]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [re-graph.logging :as log]
            [fork.re-frame :as fork]))

(defn link
  ([route-name text]
   (link route-name nil nil text))
  ([route-name params props text]
   (let [href (router/href route-name params)]
     [:a (assoc props :href href) text])))

(defn icon
  ([icon-name] (icon icon-name nil))
  ([icon-name props] (icon icon-name props 1))
  ([icon-name props ratio]
   [:span (merge {:data-uk-icon
                  (str "icon:" icon-name "; ratio:" ratio)}
                 props)]))

(defn error-page [& msg]
  [:article.uk-article
   [:h1.uk-article-title "Ruh roh!"]
   [:p.uk-article
    (if msg (apply str msg) "An error occurred!")]
   [:p.uk-article
    [link :home-page/home "Home"]]])

(defn spinner-page [& msg]
  [:article.uk-article.uk-height-1-1
   [:div {:data-uk-spinner "3"}]])

(defn number-control
  [{:keys [values handle-change handle-blur set-values] :as _fork-props}
   {:component/keys [label field-name blur-event event-args minimum
                     tooltip step]}]
  (let [min (or minimum 0)]
    [:span
     (:when label
       [:label.uk-form-label
        (merge {:for field-name}
               (when tooltip
                 {:data-uk-tooltip tooltip}))
        label])
     [:input.uk-input.uk-text-center
      (merge
        {:style     {:width "80px"}
         :type      "number"
         :min       min
         :step      (or step 500)
         :name      field-name
         :on-change handle-change
         :on-blur   (fn [js-evt]
                      (handle-blur js-evt)
                      (let [str-val (fork/retrieve-event-value js-evt)
                            parsed  (gstring/parseInt str-val)
                            value   (if (or (js/isNaN parsed)
                                            (< parsed min))
                                      (do
                                        (set-values {field-name min})
                                        min)
                                      parsed)
                            rf-evt  [blur-event event-args value]]
                        (log/debug "Blur" value)
                        (>evt rf-evt)))
         :value     (get values field-name)})]]))

(defn slider [props]
  [:input.uk-range
   (merge {:type  :range
           :value "0"
           :min   0
           :max   10000
           :step  100})])