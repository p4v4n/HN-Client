(ns app.events
  (:require [app.state :refer [app-state tab-state]]
            [app.api :as api]))

(defn change-ipp
  [evt]
  (let [new-ipp (-> evt
                    .-target
                    .-value
                    js/parseInt)]
    (if-not (js/isNaN  new-ipp)
      (do (swap! tab-state assoc :ipp new-ipp)
          (api/make-requests-for-items (name (:current-view @tab-state))
                                       (:page-number @tab-state))))))
