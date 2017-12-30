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

(defn ->initial-gen []
  (gen/elements letters))

(defn ->first-last-gen []
  (gen/let [first-name (->given-name-gen)
            last-name (->last-name-gen)]
    (str first-name " " last-name)))

(defn ->name-with-middle-gen []
  (gen/let [first-name (->given-name-gen)
            middle-name (->given-name-gen)
            last-name (->last-name-gen)]
    (string/join " " [first-name middle-name last-name])))

(defn ->name-with-initial-gen []
  (gen/let [first-name (->given-name-gen)
            initial (->initial-gen)
            last-name (->last-name-gen)]
    (string/join " " [first-name (str initial ".") last-name])))

(defn ->name-with-prefix-gen []
  (gen/let [n (gen/one-of [(->first-last-gen)
                           (->name-with-middle-gen)
                           (->name-with-initial-gen)])
            prefix (->name-prefix-gen)]
    (str prefix " " n)))

(defn ->name-with-suffix-gen []
  (gen/let [n (gen/one-of [(->first-last-gen)
                           (->name-with-middle-gen)
                           (->name-with-initial-gen)
                           (->name-with-prefix-gen)])
            suffix (->name-suffix-gen)]
    (str n " " suffix)))

(defn ->full-name-gen []
  (gen/one-of [(->first-last-gen)
               (->name-with-middle-gen)
               (->name-with-initial-gen)
               (->name-with-prefix-gen)
               (->name-with-suffix-gen)]))

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

(s/def ::full-name (s/with-gen string? ->full-name-gen))
