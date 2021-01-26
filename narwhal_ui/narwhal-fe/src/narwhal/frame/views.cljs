(ns narwhal.frame.views
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.events :as events]
            [narwhal.frame.subs :as subs]
            [fork.re-frame :as fork]
            [narwhal.grid.views :as grid]))

;; ----------------------------------------------------------------------
;; Frame name

(defn display-frame-name [frame-id]
  (let [ital (if (<sub [::subs/dirty? frame-id]) :i :span)]
    [:div {:data-uk-tooltip "title:Click to Edit; pos: bottom-left"
           :on-click        #(>evt [::events/title-clicked frame-id])}
     [:p.uk-text-lead
      [ital (<sub [::subs/frame-name frame-id])]]]))

(defn edit-frame-name-form
  [{:keys [values handle-change handle-blur form-id handle-submit
           submitting? props]}]
  (let [{::keys [frame-id]} props]
    [:form {:id        form-id
            :on-submit handle-submit}
     [:div.uk-flex
      [:input.uk-input.uk-form-large.uk-form-width-medium
       {:type        "text"
        :name        "name"
        :on-change   handle-change
        :on-blur     handle-blur
        :value       (:name values)
        :auto-focus  "autoFocus"
        :placeholder (:placeholder values)}]
      [:button.uk-button.uk-button-primary
       {:type     "submit"
        :disabled submitting?}
       "Update"]
      [:button.uk-button.uk-button-default
       {:type     "submit"
        :disabled submitting?
        :on-click #(>evt [::events/title-cancel-clicked frame-id])}
       "Cancel"]]]))

(defn edit-frame-name [frame-id]
  (let [curr-name (<sub [::subs/frame-name frame-id])
        init-name (if (= curr-name util/default-frame-name)
                    "" curr-name)]
    [fork/form
     {:props             {::scratch? (<sub [::subs/scratch? frame-id])
                          ::dirty?   (<sub [::subs/dirty? frame-id])
                          ::frame-id frame-id}
      :initial-values    {:name        init-name
                          :placeholder curr-name}
      :prevent-default?  true
      :clean-on-unmount? true
      :keywordize-keys   true
      :path              [::edit-name-form]
      :on-submit         #(>evt [::events/update-title frame-id %])}
     edit-frame-name-form]))

(defn frame-name [frame-id]
  (if (<sub [::subs/editing-title? frame-id])
    [edit-frame-name frame-id]
    [display-frame-name frame-id]))

(defn frame-create-button
  [frame-id disabled?]
  [:button.uk-button.uk-button-primary
   {:on-click #(>evt [::events/create-frame frame-id])
    :disabled disabled?}
   [component/icon "cloud-upload"]
   " Create"])

(defn frame-save-button
  [frame-id disabled?]
  [:button.uk-button.uk-button-primary
   {:on-click #(>evt [::events/save-frame frame-id])
    :disabled disabled?}
   [component/icon "cloud-upload"]
   " Save"])

(defn delete-button [frame-id]
  (let [disabled? (<sub [::subs/scratch? frame-id])]
    [:button.uk-button.uk-button-text.uk-button-danger
     (if disabled?
       {:disabled true}
       {:on-click #(>evt [::events/delete-frame frame-id])})
     " Delete frame"]))

(defn actions-dropdown [frame-id]
  [:div
   [:button.uk-button.uk-button-default
    "Actions " [component/icon "triangle-down"]]
   [:div {:data-uk-dropdown ""}
    [:ul.uk-nav.uk-dropdown-nav
     [:li [delete-button frame-id]]
     [:li [:a "Duplicate"]]
     [:li [:a "Fill"]]
     [:li [:a "Randomize"]]]]])


(defn save-controls [frame-id]
  ;; TODO: change buttons when editing/creating
  ;; TODO: no revert on *scratch*
  (let [disabled? (if (<sub [::subs/dirty? frame-id]) false true)]
    [:div.uk-flex
     (if (<sub [::subs/scratch? frame-id])
       [frame-create-button frame-id disabled?]
       [frame-save-button frame-id disabled?])
     [:button.uk-button.uk-button-default
      {:on-click #(>evt [::events/revert-frame frame-id])
       :disabled disabled?}
      [component/icon "refresh"]
      " Revert"]
     [actions-dropdown frame-id]]))

(defn frame-editor
  [frame-id]
  (let [frame-id (or frame-id util/default-frame-id)]
    [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
     [:div {:class "uk-width-expand"}
      [:div
       [:h3 "thumb"]
       [grid/thumbnail-grid frame-id "1000px"]
       [:h3 "edit"]]

      [grid/edit-grid frame-id]
      ;[grid/grid frame-id "600px"]
      [frame-name frame-id]
      [save-controls frame-id]]
     [:div {:class "uk-width-1-6@s"}
      [grid/controls]]]))

