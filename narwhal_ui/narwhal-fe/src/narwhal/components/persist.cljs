(ns narwhal.components.persist
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]))

;; TODO: pass in extra options to this thing for randomize/etc

(defn create-button
  [{:persist/keys [item-id clean-sub create-event]}]
  [:button.uk-button.uk-button-primary
   {:on-click #(>evt [create-event item-id])
    :disabled (<sub [clean-sub item-id])}
   [component/icon "cloud-upload"]
   " Create"])

(defn update-button
  [{:persist/keys [item-id clean-sub update-event]}]
  [:button.uk-button.uk-button-primary
   {:on-click #(>evt [update-event item-id])
    :disabled (<sub [clean-sub item-id])}
   [component/icon "cloud-upload"]
   " Update"])

;; TODO: should delete scratch frames (from memory) too
(defn delete-button
  [{:persist/keys [item-id scratch-sub delete-event]}]
  (let [scratch? (<sub [scratch-sub item-id])]
    [:button.uk-button.uk-button-text.uk-button-danger
     (if scratch?
       {:disabled true}
       {:on-click #(>evt [delete-event item-id])})
     " Delete"]))

(defn revert-button
  [{:persist/keys [item-id scratch-sub clean-sub revert-event]}]
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [revert-event item-id])
    :disabled (or (<sub [clean-sub item-id])
                  (<sub [scratch-sub item-id]))}
   [component/icon "refresh"]
   " Revert"])

(defn actions-dropdown [{:persist/keys [extra-actions item-id] :as options}]
  [:div
   [:button.uk-button.uk-button-default
    "Actions " [component/icon "triangle-down"]]
   [:div {:data-uk-dropdown ""}
    [:ul.uk-nav.uk-dropdown-nav
     [:li [delete-button options]]
     (when extra-actions
       [extra-actions item-id])]]])

(defn persist-controls
  [{:persist/keys [item-id scratch-sub] :as options}]
  [:div.uk-flex
   (if (<sub [scratch-sub item-id])
     [create-button options]
     [update-button options])
   [revert-button options]
   [actions-dropdown options]])
