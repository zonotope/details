(ns details.data.name
  (:require [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

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
  (gen/one-of [(->gen data ::last)
               (gen/let [maiden (->gen data ::last)
                         married (->gen data ::last)]
                 (string/join "-" [maiden married]))]))

(defn ->suffix-gen []
  (->gen data ::suffix))

(defn ->initial-gen []
  (gen/elements letters))

(defn ->first-last-gen []
  (gen/let [first-name (->given-name-gen)
            last-name (->given-name-gen)]
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
            prefix (->prefix-gen)]
    (str prefix " " n)))

(defn ->name-with-suffix-gen []
  (gen/let [n (gen/one-of [(->first-last-gen)
                           (->name-with-middle-gen)
                           (->name-with-initial-gen)
                           (->name-with-prefix-gen)])
            suffix (->suffix-gen)]
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

(s/def ::prefix (s/with-gen string? ->prefix-gen))

(s/def ::given (s/with-gen string? ->given-name-gen))
(s/def ::first ::given)
(s/def ::middle ::given)

(s/def ::initial letters)

(s/def ::last (s/with-gen string? ->last-name-gen))

(s/def ::suffix (s/with-gen string? ->suffix-gen))

(s/def ::full (s/with-gen string? ->full-name-gen))
