(ns app.views
  (:require [app.state :refer [app-state tab-state]]
            [app.api :as api]
            [app.events :as events]
            [app.helpers :as helpers]))

(defn header
  []
  [:div.header
   [:div.hl
    [:h3
     [:a {:href (str "#/top/1")}
      "Hacker News Client"]]
    [:div.header-sec
     (for [view ["new" "show" "ask" "job"]]
       [:div.hi
        {:key view}
        [:a {:href (str "#/" view "/1")}
         view]])
     [:div.ipp
      [:select {:default-value (:ipp @tab-state)
                :on-change #(events/change-ipp %)}
       [:option {:value 5} "5"]
       [:option {:value 10} "10"]
       [:option {:value 15} "15"]
       [:option {:value 20} "20"]]]]]])

(defn item-card
  [{:keys [id title time score url by descendants]}]
  (if-not (nil? id)
    [:div.item
     [:div.item__body
      [:div.item__fl
       [:a.item__title {:href url} title]]
      [:div.item__sl
       [:a.subtext {:href (str "#/item/" id)}
        (str score " points by " by " " (helpers/time-elapsed time)
             " hours ago | " descendants " comments")]]]]))

(defn front-page-items
  []
  (let [{:keys [page-number current-view ipp] :as state} @tab-state]
    [:div.fpi
     (doall (for [id (->> (get @app-state current-view)
                          (drop (* ipp (dec page-number)))
                          (take ipp))]
              [:div {:key id}
               [item-card (get @app-state id)]]))]))

(defn comment-card
  [{:keys [id title time score url by descendants text kids]}]
  (if-not (nil? id)
    [:div.comment
     [:div.comment__body
      [:div {:dangerouslySetInnerHTML {:__html  (or title text)}}]
      [:div.comment__sl
       (str "posted by " by " " (helpers/time-elapsed time)
            " hours ago | " (count kids) " kids")]]]))

(defn show-complete-item
  [integer-id]
  (let [item (get @app-state integer-id)]
    [:div.item__all
     [comment-card item]
     (doall (for [kid-item (:kids item)]
              [:div {:key (str integer-id kid-item)}
               [show-complete-item kid-item]]))]))

(defn page-bar
  []
  (let [{:keys [page-number current-view ipp]} @tab-state]
    [:div.pagebar
     [:div.pi
      [:a {:href (str "#/" (name current-view) "/"
                      (if (not= page-number 1)
                        (dec page-number)
                        page-number))}
       "<<"]]
     [:div.pi (str "page-" page-number)]
     [:div.pi
      [:a  {:href (str "#/" (name current-view) "/"
                       (if (< (* ipp page-number)
                              (count (current-view @app-state)))
                         (inc page-number)
                         page-number))}
       ">>"]]]))

(defn display-view
  "select components based on current-view"
  []
  (let [cv (:current-view @tab-state)]
    (if (integer? cv)
      [:div.item_a (show-complete-item cv)]
      [:div.item_a
       (front-page-items)
       (page-bar)])))

(defn app []
  [:div.app
   [header]
   [display-view]])
