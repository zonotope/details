(ns details.data.name
  (:require [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]))

(def data (util/read-data-resource "details/data/name.edn"))

(def letters (->> (range 65 91)
                  (map char)
                  set))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ->prefix-gen []
  (->gen data ::prefix))

(defn ->given-name-gen []
  (->gen data ::given))

(defn ->last-name-gen []
  (->gen data ::last))

(defn ->suffix-gen []
  (->gen data ::suffix))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; specs                                                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::prefix (s/with-gen string? ->prefix-gen))

(s/def ::given (s/with-gen string? ->given-name-gen))
(s/def ::first ::given)
(s/def ::middle ::given)

(s/def ::initial letters)

(s/def ::last (s/with-gen string? ->last-name-gen))

(s/def ::suffix (s/with-gen string? ->suffix-gen))


(s/def ::full (s/keys :req [::first ::last]
                      :opt [::prefix ::suffix ::middle]))
