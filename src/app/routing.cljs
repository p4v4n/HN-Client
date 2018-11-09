(ns app.routing
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require [app.state :as state]
            [app.api :as api])
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]))

;; add browser history
(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state/app-state assoc :current-view :top)
    (swap! state/app-state assoc :page-number 1)
    (swap! state/app-state assoc :ipp 10)
    (api/get-section-ids "top" 1))

  (defroute "/item/:id" {id :id}
    (let [id-int (js/parseInt id)]
      (swap! state/app-state assoc :current-view id-int)
      (api/get-item id-int api/recursive-handler)))

  (defroute "/:section/:id" {section :section id :id}
    (let [id-int (js/parseInt id)]
      (swap! state/app-state assoc :current-view (keyword section))
      (swap! state/app-state assoc :page-number id-int)
      (swap! state/app-state assoc :ipp 10)
      (api/get-section-ids section id-int)))

  (hook-browser-navigation!))
