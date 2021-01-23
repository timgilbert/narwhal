(ns narwhal.views.frame
  (:require [narwhal.views.grid :as grid]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [narwhal.views.component :as component]
            [narwhal.color :as color]
            [fork.re-frame :as fork]
            narwhal.events.frame))

(rf/reg-event-db
  ::tool-change
  (fn [db [_ tool]]
    (assoc db ::selected-tool tool)))

(rf/reg-event-db
  ::title-clicked
  (fn [db [_ _]]
    (assoc db ::frame-edit? true)))

(rf/reg-event-db
  ::title-cancel-clicked
  (fn [db [_ _]]
    (dissoc db ::frame-edit?)))

(rf/reg-sub
  ::editing-title?
  (fn [db _]
    (get db ::frame-edit? false)))

(rf/reg-sub
  ::selected-tool
  (fn [db _]
    (get db ::selected-tool ::pencil)))

(defn tool-icon [tool icon-name]
  (let [selected? (= (<sub [:grid/active-tool]) tool)
        class     (if selected? "uk-active" "")]
    [:li {:on-click #(>evt [:grid/set-active-tool tool])
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
  (let [active (<sub [:palette/active-color])]
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

(defn display-frame-name [frame-id]
  (let [ital (if (<sub [:frame/dirty?]) :i :span)]
    [:div {:data-uk-tooltip "title:Click to Edit; pos: bottom-left"
           :on-click        #(>evt [::title-clicked])}
     [:p.uk-text-lead
      [ital (<sub [:frame/frame-name frame-id])]]]))

(defn edit-frame-name-form
  [{:keys [values handle-change handle-blur form-id handle-submit
           submitting?]}]
  [:form {:id        form-id
          :on-submit handle-submit}
   [:div.uk-flex
    [:input.uk-input.uk-form-large.uk-form-width-medium
     {:type        "text"
      :name        "name"
      :on-change   handle-change
      :on-blur     handle-blur
      :value       (values "name")
      :placeholder (values "name")}]
    [:button.uk-button.uk-button-primary
     {:type     "submit"
      :disabled submitting?}
     "Update"]
    [:button.uk-button.uk-button-default
     {:type     "submit"
      :disabled submitting?
      :on-click #(>evt [::title-cancel-clicked])}
     "Cancel"]]])

(defn edit-frame-name [frame-id]
  (let [curr-name (<sub [:frame/frame-name frame-id])
        form-id   (str "edit-frame-name." frame-id)]
    [fork/form
     {:initial-values    {"name" curr-name}
      :prevent-default?  true
      :form-id           form-id
      :clean-on-unmount? true
      :path              [::edit-name-form]
      :on-submit         #(>evt [:frame/update-title frame-id %])}
     edit-frame-name-form]))


(defn frame-name [frame-id]
  (if (<sub [::editing-title?])
    [edit-frame-name frame-id]
    [display-frame-name frame-id]))


(defn frame-editor []
  (let [frame-id (<sub [:frame/active-frame-id])]
    [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
     [:div {:class "uk-width-expand"}
      [grid/grid frame-id]
      [frame-name frame-id]]
     [:div {:class "uk-width-1-6@s"}
      [controls]]]))
