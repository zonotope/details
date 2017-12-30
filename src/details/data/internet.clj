(ns details.data.internet
  (:require [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (util/read-data-resource "details/data/internet.edn"))

(def username-regex #"^[a-z\-\_\+]+$")

;; starts with a lower-case letter, only contains alpanumeric characters as well
;; as "-", "_", "+", is non-empty, and is between 3 and 16 characters.
(def username-gen
  (gen/let [head util/gen-lowercase-letter
            tail (-> data
                     (util/->gen ::username-char)
                     (util/->string-gen 2 15))]
    (str head tail)))

(s/def ::username (s/with-gen (s/and string?
                                     #(< 0 (count %) 16)
                                     (partial re-matches username-regex))
                    (fn [] username-gen)))
