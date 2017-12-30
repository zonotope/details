(ns details.util
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(defn read-data-resource [path]
  (-> path io/resource slurp edn/read-string))

(defn ->gen [data k]
  (-> data
      (get k)
      gen/elements))

(def gen-lowercase-letter
  (gen/fmap string/lower-case gen/char-alpha))

(defn ->string-gen [char-gen min max]
  (gen/fmap string/join (gen/vector char-gen min max)))
