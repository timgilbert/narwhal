(ns narwhal.grid.views
  (:require [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.grid.events :as events]
            [narwhal.grid.subs :as subs]
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
    :on-click #(>evt [:palette/set-active-color color])}
   util/nbsp])

(defn color-palette []
  (let [active (<sub [::subs/active-color])]
    [:div
     [:p "Palette"]
     [:div.palette-grid
      [:div.palette-selected.palette-swatch
       {:style {:background-color active}}
       util/nbsp]
      (for [[i c] (map-indexed vector palette-colors)]
        ^{:key i} [color-preset c])]]))

(defn controls []
  [:div
   [color-palette]
   [:p "Controls"]
   [:ul.uk-iconnav.uk-iconnav-vertical
    [tool-icon :tools/pencil "pencil"]
    [tool-icon :tools/bucket "paint-bucket"]
    [:li {:on-click #(>evt [:frame-edit/random])} [component/icon "bolt"]]
    [:li {:on-click #(>evt [:frame-edit/blank])} [component/icon "trash"]]]])

(defn cell [frame-id color index]
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

(defn grid [frame-id]
  (let [pixels (<sub [::subs/pixels frame-id])]
    (when pixels
      [:div.pixel-grid
       (for [[i color] (map-indexed vector pixels)]
         ^{:key i} [cell frame-id color i])])))
