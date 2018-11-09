(ns app.state
  (:require [reagent.core :as r]
            [alandipert.storage-atom :refer [local-storage]]))

(defonce app-state (r/atom {}))
