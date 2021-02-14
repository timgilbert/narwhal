(ns narwhal.grid.views
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.grid.events :as events]
            [narwhal.grid.subs :as subs]
            [narwhal.util.color :as color]
            [narwhal.util.component :as component]
            [narwhal.grid.db :as db]))

(defn tool-icon [tool icon-name]
  (let [selected? (= (<sub [::subs/active-tool]) tool)
        class     (if selected? "uk-active" "")]
    [:li {:on-click #(>evt [::events/set-active-tool tool])
          :class    class}
     [component/icon icon-name (when selected? {:class "uk-icon-button"})]]))

(def palette-colors
  [(::color/black color/named)
   (::color/aqua color/named)
   (::color/chartreuse color/named)
   (::color/dark-orange color/named)
   (::color/dark-violet color/named)
   (::color/gray color/named)
   (::color/medium-blue color/named)
   (::color/red color/named)
   (::color/white color/named)
   (::color/yellow color/named)
   (::color/hot-pink color/named)
   (::color/green color/named)])

(defn color-swatch [swatch-index]
  (let [color (<sub [::subs/swatch swatch-index])]
    [:div.palette-swatch
     {:style           {:background-color color
                        :cursor           :crosshair}
      :on-click        #(>evt [::events/set-active-color color])
      :on-double-click #(>evt [::events/edit-swatch swatch-index])}
     util/nbsp]))

(defn color-picker []
  (let [swatch-index (<sub [::subs/swatch-under-edit])
        curr-color   (when swatch-index (<sub [::subs/swatch swatch-index]))]
    [:div
     (when curr-color
       [component/color-picker
        #:component{:start-color  curr-color
                    :change-event [::events/set-swatch-color swatch-index]
                    :blur-event   [::events/clear-edit-swatch]}])]))

(defn color-palette []
  [:div.uk-padding-small
   [:div.palette-grid
    [:div.palette-selected.palette-swatch
     {:style {:background-color (<sub [::subs/active-color])}
      :data-uk-tooltip "title: Double-click a swatch to update its color"}
     util/nbsp]
    (for [i (range (<sub [::subs/swatch-count]))]
      ^{:key i} [color-swatch i])]
   [color-picker]])

(defn controls []
  [:div.uk-align-center
   [color-palette]
   [:p "Controls"]
   [:ul.uk-iconnav.uk-iconnav-vertical
    [tool-icon :tools/pencil "pencil"]
    [tool-icon :tools/bucket "paint-bucket"]
    [:li {:on-click #(>evt [:frame-edit/random])} [component/icon "bolt"]]
    [:li {:on-click #(>evt [:frame-edit/blank])} [component/icon "trash"]]]])

;; ----------------------------------------------------------------------
;; SVG stuff

(defn ^:private base-svg-cell
  [{::keys [cell-size x y color gutter-size gutter-color props tooltip-fn]
    :as    attrs}]
  (let [tooltip-attr (when tooltip-fn
                       {:data-uk-tooltip (tooltip-fn attrs)})]
    [:rect
     (merge {:x            x
             :y            y
             :height       cell-size
             :width        cell-size
             :fill         color
             :stroke       gutter-color
             :stroke-width (or gutter-size 1)}
            props
            tooltip-attr)]))

(defn ^:private grid-edit-tooltip
  [{::keys [i j pixel-index]}]
  (str "title:" pixel-index " (" i "," j ");pos:bottom-left"))

(defn ^:private svg-edit-cell
  [{::keys [frame-id pixel-index cell-size] :as attrs}]
  (let [on-click #(>evt [::events/click frame-id pixel-index])]
    [base-svg-cell
     (merge attrs
            {::tooltip-fn grid-edit-tooltip
             ::props      {:style    {:cursor "crosshair"}
                           :on-click on-click}})]))

(defn ^:private svg-display-cell
  [{::keys [] :as attrs}]
  [base-svg-cell attrs])

(defn ^:private svg-thumb-cell
  [{::keys [color] :as attrs}]
  [base-svg-cell (assoc attrs ::gutter-color color)])

(defn ^:private base-grid
  [{::keys [frame-id grid-length cell-component gutter-size gutter-color
            grid-data]}]
  (let [data      (or grid-data (<sub [::subs/frame-data frame-id]))
        {:keys [height width pixels]} data
        cell-size 100
        vb-width  (* width cell-size)
        vb-height (* height cell-size)
        view-box  (str "0 0 "
                       vb-width
                       " "
                       vb-height)]
    (into
      [:svg {:xmlns    "http://www.w3.org/2000/svg"
             :version  "1.1"
             :view-box view-box
             :width    grid-length
             :height   grid-length}
       (for [i (range width)
             j (range height)
             :let [index (+ i (* j height))
                   color (nth pixels index)
                   x     (+ gutter-size (* i cell-size))
                   y     (+ gutter-size (* j cell-size))]]
         ^{:key index}
         [cell-component {::frame-id     frame-id
                          ::x            x
                          ::y            y
                          ::i            i
                          ::j            j
                          ::color        color
                          ::cell-size    cell-size
                          ::gutter-size  gutter-size
                          ::gutter-color gutter-color
                          ::pixel-index  index}])])))

(defn edit-grid [frame-id]
  [base-grid {::frame-id       frame-id
              ::tooltip-fn     grid-edit-tooltip
              ::grid-length    "600px"
              ::gutter-size    7
              ::gutter-color   (::color/white color/named)
              ::cell-component svg-edit-cell}])

(defn display-grid [frame-id size]
  [base-grid {::frame-id       frame-id
              ::grid-length    (or size "600px")
              ::gutter-size    3
              ::gutter-color   (::color/white color/named)
              ::cell-component svg-display-cell}])

(defn thumbnail-grid [frame-id size]
  [base-grid {::frame-id       frame-id
              ::grid-length    (or size "600px")
              ::cell-component svg-thumb-cell}])

(defn random-grid [size]
  [base-grid {::grid-length    (or size "600px")
              ::cell-component svg-thumb-cell
              ::grid-data      db/random-grid-data}])

(defn solid-grid [size color]
  [base-grid {::grid-length    (or size "600px")
              ::cell-component svg-thumb-cell
              ::grid-data      (db/solid-grid-data color)}])
