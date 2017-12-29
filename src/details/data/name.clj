(ns details.data.name
  (:require [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]))

(def data (util/read-data-resource "details/data/name.edn"))

(def letters (->> (range 65 91)
                  (map char)
                  set))

(s/def ::prefix
  (-> string?
      (s/with-gen #(->gen data ::prefix))))

(s/def ::given
  (-> string?
      (s/with-gen #(->gen data ::given))))

(s/def ::first ::given)
(s/def ::middle ::given)

(s/def ::initial letters)

(s/def ::last
  (-> string?
      (s/with-gen #(->gen data ::last))))

(s/def ::suffix
  (-> string?
      (s/with-gen #(->gen data ::suffix))))


(s/def ::full (s/keys :req [::first ::last]
                      :opt [::prefix ::suffix ::middle]))
