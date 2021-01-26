(ns narwhal.grid.views
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.grid.events :as events]
            [narwhal.grid.subs :as subs]
            [narwhal.frame.subs :as frame-subs]
            [narwhal.util.color :as color]
            [narwhal.util.component :as component]))

(defn tool-icon [tool icon-name]
  (let [selected? (= (<sub [::subs/active-tool]) tool)
        class     (if selected? "uk-active" "")]
    [:li {:on-click #(>evt [::events/set-active-tool tool])
          :class    class}
     [component/icon icon-name selected?]]))

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

(defn color-preset [color]
  [:div.palette-swatch
   {:style    {:background-color color
               :cursor           :crosshair}
    :on-click #(>evt [::events/set-active-color color])}
   util/nbsp])

(defn color-palette []
  [:div
   [:p "Palette"]
   [:div.palette-grid
    [:div.palette-selected.palette-swatch
     {:style {:background-color (<sub [::subs/active-color])}}
     util/nbsp]
    (for [[i c] (map-indexed vector palette-colors)]
      ^{:key i} [color-preset c])]])

(defn controls []
  [:div
   [color-palette]
   [:p "Controls"]
   [:ul.uk-iconnav.uk-iconnav-vertical
    [tool-icon :tools/pencil "pencil"]
    [tool-icon :tools/bucket "paint-bucket"]
    [:li {:on-click #(>evt [:frame-edit/random])} [component/icon "bolt"]]
    [:li {:on-click #(>evt [:frame-edit/blank])} [component/icon "trash"]]]])

(defn html-cell [frame-id color index]
  [:div.pixel-cell
   (merge {:style    {:background-color color}
           :on-click #(>evt [::events/click frame-id index])}
          (when util/tooltips?
            {:data-uk-tooltip (str "title: " index "; pos: bottom-left")}))
   util/nbsp])

(defn grid-footer []
  [:div.uk-flex.uk-flex-middle
   [:fieldset.uk-fieldset
    [:div.uk-margin
     [:input.uk-input.uk-form-width-medium
      {:type "text" :placeholder "Frame name"}]]]])

(defn html-grid [frame-id]
  (let [pixels (<sub [::subs/pixels frame-id])]
    (when pixels
      [:div.pixel-grid
       (for [[i color] (map-indexed vector pixels)]
         ^{:key i} [html-cell frame-id color i])])))

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
  [{::keys [frame-id grid-length cell-component gutter-size gutter-color]}]
  (let [{:keys [height width pixels]} (<sub [::subs/frame-data frame-id])
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
