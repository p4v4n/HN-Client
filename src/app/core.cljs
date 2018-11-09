(ns app.core
  (:require [reagent.core :as r]
            [app.views :as views]
            [app.api :as api]
            [app.routing :as routing]
            [app.state :as state]))

(defn ^:dev/after-load start
  []
  (routing/app-routes)
  (r/render-component [views/app]
                      (.getElementById js/document "app")))

(defn ^:export main
  []
  (start))
