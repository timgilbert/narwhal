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
  (let [ital (if (<sub [::subs/dirty?]) :i :span)]
    [:div {:data-uk-tooltip "title:Click to Edit; pos: bottom-left"
           :on-click        #(>evt [::title-clicked])}
     [:p.uk-text-lead
      [ital (<sub [::subs/frame-name frame-id])]]]))

(defn save-button
  [{::keys [frame-id dirty?]} submitting?]
  [:button.uk-button.uk-button-primary
   {:type     "submit"
    :disabled (or (not dirty?) submitting?)}
   "Update"])

(defn create-button
  [{::keys [frame-id dirty?]} submitting?]
  [:button.uk-button.uk-button-primary
   {:type     "submit"
    :disabled (or (not dirty?) submitting?)}
   "Create"])

(defn edit-frame-name-form
  [{:keys [values handle-change handle-blur form-id handle-submit
           submitting? props]}]
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
    (if [::scratch? props]
      [create-button props submitting?]
      [save-button props submitting?])
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
  (let [curr-name (<sub [::subs/frame-name frame-id])
        init-name (if (= curr-name util/default-frame-name)
                    "" curr-name)]
    [fork/form
     {:form-id           frame-id
      :props             {::scratch? (<sub [::subs/scratch? frame-id])
                          ::dirty?   (<sub [::subs/dirty? frame-id])
                          ::frame-id frame-id}
      :initial-values    {:name        init-name
                          :placeholder curr-name}
      :prevent-default?  true
      :clean-on-unmount? true
      :keywordize-keys   true
      :path              [::edit-name-form]
      :on-submit         #(>evt [:frame/update-title frame-id %])}
     edit-frame-name-form]))

(defn frame-name [frame-id]
  (if (<sub [::subs/editing-title? frame-id])
    [edit-frame-name frame-id]
    [display-frame-name frame-id]))

(defn save-controls [frame-id]
  ;; TODO: change buttons when editing/creating
  ;; TODO: no revert on *scratch*
  (let [disabled? (if (<sub [::subs/dirty? frame-id]) false true)]
    [:div.uk-flex
     [:button.uk-button.uk-button-primary
      {:on-click #(>evt [:frame/save-frame frame-id])
       :disabled disabled?}
      [component/icon "cloud-upload"]
      " Save"]
     [:button.uk-button.uk-button-default
      {:on-click #(>evt [:frame/revert-frame frame-id])
       :disabled disabled?}
      [component/icon "refresh"]
      " Revert"]]))

(defn frame-editor [frame-id]
  [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
   [:div {:class "uk-width-expand"}
    [grid/grid frame-id]
    [frame-name frame-id]
    [save-controls frame-id]]
   [:div {:class "uk-width-1-6@s"}
    [grid/controls]]])
