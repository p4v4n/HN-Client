(ns app.state
  (:require [reagent.core :as r]
            [alandipert.storage-atom :refer [local-storage]]))

(defonce app-state (local-storage
                    (r/atom {})
                    :app-state))

(defonce tab-state (r/atom {:ipp 10}))
