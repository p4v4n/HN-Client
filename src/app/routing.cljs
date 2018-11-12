(ns app.routing
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require [app.state :refer [app-state tab-state]]
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
    (swap! tab-state assoc :current-view :top)
    (swap! tab-state assoc :page-number 1)
    (api/get-section-ids "top" 1))

  (defroute "/item/:id" {id :id}
    (let [id-int (js/parseInt id)]
      (swap! tab-state assoc :current-view id-int)
      (api/get-item id-int api/recursive-handler)))

  (defroute "/:section/:id" {section :section id :id}
    (let [id-int (js/parseInt id)]
      (swap! tab-state assoc :current-view (keyword section))
      (swap! tab-state assoc :page-number id-int)
      (api/get-section-ids section id-int)))

  (hook-browser-navigation!))
