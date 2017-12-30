(ns details.data.person
  (:require [details.data :as data :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (data/read-data-resource "details/data/person.edn"))

(def letters (->> (range 65 91)
                  (map char)
                  set))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ->name-prefix-gen []
  (->gen data ::name-prefix))

(defn ->given-name-gen []
  (->gen data ::given-name))

(defn ->last-name-gen []
  (gen/one-of [(->gen data ::last-name)
               (gen/let [maiden (->gen data ::last-name)
                         married (->gen data ::last-name)]
                 (string/join "-" [maiden married]))]))

(defn ->name-suffix-gen []
  (->gen data ::name-suffix))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; specs                                                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::name-prefix (s/with-gen string? ->name-prefix-gen))

(s/def ::given-name (s/with-gen string? ->given-name-gen))

(s/def ::first-name ::given-name)

(s/def ::middle-name ::given-name)

(s/def ::initial letters)

(s/def ::last-name (s/with-gen string? ->last-name-gen))

(s/def ::name-suffix (s/with-gen string? ->name-suffix-gen))

(s/def ::full-name (s/keys :req [::first ::last]
                           :opt [::prefix ::suffix ::middle ::initial]))

(defmethod data/render ::full-name [_ {::keys [first last prefix suffix middle
                                               initial]}]
  (->> [prefix first (or middle initial) last suffix]
       (remove nil?)
       (string/join " ")))
