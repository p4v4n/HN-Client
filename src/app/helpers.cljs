(ns app.helpers
  (:require [goog.string :as gstring]))

(defn current-time
  []
  (/ (.getTime (js/Date.)) 1000))

(defn time-elapsed
  [post-time]
  (->> post-time
       (- (current-time))
       (#(/ % 3600))
       (.round js/Math)))

(defn unescape-html-entities
  [string]
  (gstring/unescapeEntities string))
