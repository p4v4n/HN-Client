(ns app.api
  (:require [ajax.core :refer [GET]]
            [app.state :as state]
            [app.helpers :as helpers]))

(def base-url "http://hacker-news.firebaseio.com")

(defn error-handler
  "Log failed requests"
  [{:keys [status status-text]}]
  (.log js/console
        (str "something bad happened: " status " " status-text)))

(defn item-handler
  "Store item details in app-state"
  [response]
  (swap! state/app-state assoc (:id response) response))

(defn get-item
  "Make request for an particular item"
  [integer-id handler]
  (GET (str base-url "/v0/item/" integer-id ".json")
       {:handler handler
        :error-handler error-handler
        :response-format :json
        :keywords? true}))

(defn recursive-handler
  "Store item details and initiate requests for all its kids"
  [response]
  (item-handler response)
  (doall (map #(get-item % recursive-handler)
              (:kids response))))

(defn make-requests-for-items
  "Initiate requests for items on a particular page"
  [section page-number]
  (let [ipp (:ipp @state/app-state)]
    (doall (for [id (->> (keyword section)
                         (get @state/app-state)
                         (drop (* ipp (- page-number 1)))
                         (take ipp))]
             (get-item id item-handler)))))

(defn id-handler
  "Store ids of section and initiate requests for items to display"
  [response section page-number]
  (swap! state/app-state assoc (keyword section) response)
  (make-requests-for-items section page-number))

(defn get-section-ids
  "Initiate requests for ids of a particular section"
  [section page-number]
  (GET (str base-url "/v0/" section "stories.json")
       {:handler #(id-handler % section page-number)
        :error-handler error-handler
        :response-format :json
        :keywords? true}))
