(ns narwhal.components.name-edit
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [fork.re-frame :as fork]))

(rf/reg-event-db
  ::title-clicked
  (fn [db [_ name-type item-id]]
    (assoc-in db [:n/editing? name-type item-id] true)))

(rf/reg-event-fx
  ::submitted
  (fn [{:keys [db]} [_ name-type item-id on-submit data]]
    {:db       (update-in db [:n/editing? name-type] dissoc item-id)
     :dispatch (conj on-submit data)}))

(rf/reg-event-db
  ::title-cancel-clicked
  (fn [db [_ name-type item-id]]
    (update-in db [:n/editing? name-type] dissoc item-id)))

(rf/reg-sub
  ::editing-title?
  (fn [db [_ name-type item-id]]
    (get-in db [:n/editing? name-type item-id] false)))

(defn display-frame-name
  [{:name-edit/keys [item-id name-type dirty-sub name-sub]}]
  (let [ital (if (<sub [dirty-sub item-id]) :i :span)]
    [:div {:data-uk-tooltip "title:Click to Edit; pos: bottom-left"
           :on-click        #(>evt [::title-clicked name-type item-id])}
     [:p.uk-text-lead
      [ital (<sub [name-sub item-id])]]]))

(defn edit-name-form
  [{:keys [values handle-change handle-blur form-id handle-submit
           submitting? props]}]
  (let [{::keys [item-id name-type]} props]
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
        :on-click #(>evt [::title-cancel-clicked name-type item-id])}
       "Cancel"]]]))

(defn edit-name
  [{:name-edit/keys [item-id name-type name-sub dirty-sub scratch-sub
                     on-submit on-cancel]}]
  (let [curr-name (<sub [name-sub item-id])
        scratch?  (<sub [scratch-sub item-id])
        init-name (if scratch? "" curr-name)]
    [fork/form
     {:props             {::scratch?  scratch?
                          ::dirty?    (<sub [dirty-sub item-id])
                          ::item-id   item-id
                          ::on-cancel on-cancel}
      :initial-values    {:name        init-name
                          :placeholder curr-name}
      :prevent-default?  true
      :clean-on-unmount? true
      :keywordize-keys   true
      :path              [::edit-name-form]
      :on-submit         #(>evt [::submitted name-type item-id on-submit %])}
     edit-name-form]))

(defn editable-name
  [{:name-edit/keys [item-id name-type] :as props}]
  (if (<sub [::editing-title? name-type item-id])
    [edit-name props]
    [display-frame-name props]))
