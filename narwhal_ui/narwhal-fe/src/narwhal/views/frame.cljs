(ns narwhal.views.frame
  (:require [narwhal.views.grid :as grid]
            [re-frame.core :as rf]
            [narwhal.util :as util :refer [<sub >evt]]
            [re-graph.core :as re-graph]
            narwhal.events.frame))

(def default-name "*scratch*")

(rf/reg-event-db
  ::tool-change
  (fn [db [_ tool]]
    (assoc db ::selected-tool tool)))

(rf/reg-sub
  ::selected-tool
  (fn [db _]
    (get db ::selected-tool ::pencil)))

(defn tool-icon [tool icon-name]
  (let [selected? (= (<sub [::selected-tool]) tool)
        class     (if selected? "uk-active" "")]
    [:li {:on-click #(>evt [::tool-change tool])
          :class class}
     [util/icon icon-name selected?]]))

(defn controls []
  [:div
   [:p "Controls"]
   [:ul.uk-iconnav.uk-iconnav-vertical
    [tool-icon ::pencil "pencil"]
    [tool-icon ::bucket "paint-bucket"]
    [:li {:on-click #(>evt [:frame-edit/trash])} [util/icon "bolt"]]
    [:li {:on-click #(>evt [:frame-edit/blank])} [util/icon "trash"]]]])

(defn new-frame [slug]
  [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
   [:div {:class "uk-width-expand"}
    [:h1 "new-frame"]
    [grid/grid]]
   [:div {:class "uk-width-1-6@s"}
    [controls]]])
