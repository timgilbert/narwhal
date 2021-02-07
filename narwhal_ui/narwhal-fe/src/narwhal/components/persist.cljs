(ns narwhal.components.persist
  (:require [lambdaisland.glogi :as log]
            [narwhal.util.component :as component]
            [narwhal.util.util :as util :refer [<sub >evt]]))

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
   " Save"])

;; TODO: should delete scratch frames (from memory) too
(defn delete-button
  [{:persist/keys [item-id scratch-sub delete-event]}]
  (let [scratch? (<sub [scratch-sub item-id])]
    [:button.uk-button.uk-button-text.uk-button-danger
     (if scratch?
       {:disabled true}
       {:on-click #(>evt [delete-event item-id])})
     " Delete frame"]))

(defn revert-button
  [{:persist/keys [item-id scratch-sub clean-sub revert-event]}]
  [:button.uk-button.uk-button-default
   {:on-click #(>evt [revert-event item-id])
    :disabled (or (<sub [clean-sub item-id])
                  (<sub [scratch-sub item-id]))}
   [component/icon "refresh"]
   " Revert"])

(defn actions-dropdown [options]
  [:div
   [:button.uk-button.uk-button-default
    "Actions " [component/icon "triangle-down"]]
   [:div {:data-uk-dropdown ""}
    [:ul.uk-nav.uk-dropdown-nav
     [:li [delete-button options]]
     [:li [:a "Duplicate"]]
     [:li [:a "Fill"]]
     [:li [:a "Randomize"]]]]])

(defn persist-controls
  [{:persist/keys [item-id scratch-sub] :as options}]
  [:div.uk-flex
   (if (<sub [scratch-sub item-id])
     [create-button options]
     [update-button options])
   [revert-button options]
   [actions-dropdown options]])
