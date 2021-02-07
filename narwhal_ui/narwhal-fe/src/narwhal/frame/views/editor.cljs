(ns narwhal.frame.views.editor
  (:require [lambdaisland.glogi :as log]
            [re-frame.core :as rf]
            [narwhal.util.component :as component]
            [narwhal.components.persist :as persist]
            [narwhal.components.name-edit :as name-edit]
            [narwhal.util.util :as util :refer [<sub >evt]]
            [narwhal.frame.events :as events]
            [narwhal.frame.subs :as subs]
            [fork.re-frame :as fork]
            [narwhal.grid.views :as grid]))

;; ----------------------------------------------------------------------
;; Frame editor

(defn frame-name-controls [frame-id]
  [name-edit/editable-name
   #:name-edit{:item-id     frame-id
               :type        ::name-edit/frame
               :name-sub    ::subs/frame-name
               :dirty-sub   ::subs/dirty?
               :scratch-sub ::subs/scratch?
               :on-submit   [::events/update-title frame-id]}])


(defn frame-persist-controls [frame-id]
  [persist/persist-controls
   #:persist{:item-id      frame-id
             :name-type    ::frame
             :scratch-sub  ::subs/scratch?
             :clean-sub    ::subs/clean?
             :update-event ::events/save-frame
             :create-event ::events/create-frame
             :delete-event ::events/delete-frame
             :revert-event ::events/revert-frame}])

(defn frame-editor [frame-id]
  [:div.uk-grid.uk-grid-divider {:data-uk-grid ""}
   [:div {:class "uk-width-expand"}
    [grid/edit-grid frame-id]
    [frame-name-controls frame-id]
    [frame-persist-controls frame-id]]
   [:div {:class "uk-width-1-6@s"}
    [grid/controls]]])

(defn frame-editor-page
  [route]
  (let [frame-id (-> route :path-params :frame-id)]
    (if (<sub [::subs/frame-exists? frame-id])
      [frame-editor frame-id]
      [component/error-page "Can't find frame-id " frame-id])))
