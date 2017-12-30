(ns details.data.company
  (:require [details.data.name :as name]
            [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (util/read-data-resource "details/data/company.edn"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ->suffix-gen []
  (->gen data ::suffix))

(defn ->industry-gen []
  (->gen data ::industry))

(defn ->profession-gen []
  (->gen data ::profession))

(defn ->type-gen []
  (->gen data ::type))

(defn ->name-a-gen []
  (gen/let [family-name (name/->last-name-gen)
            suffix (->suffix-gen)]
    (str family-name " " suffix)))

(defn ->name-b-gen []
  (gen/let [partner-a (name/->last-name-gen)
            partner-b (name/->last-name-gen)
            partner-c (name/->last-name-gen)]
    (str partner-a ", " partner-b ", and " partner-c)))

(defn ->name-gen []
  (gen/one-of [(->name-a-gen) (->name-b-gen)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; specs                                                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::name (s/with-gen string? ->name-gen))

(s/def ::industry (s/with-gen string? ->industry-gen))

(s/def ::type (s/with-gen string? ->type-gen))

(s/def ::profession (s/with-gen string? ->profession-gen))
