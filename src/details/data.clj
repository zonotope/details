(ns details.data
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(defmulti render (fn [spec & args] spec))
(defmethod render :default [_ data] data)

(defn read-data-resource [path]
  (-> path io/resource slurp edn/read-string))

(defn ->gen [data k]
  (-> data
      (get k)
      gen/elements))

(defn ->lowercase-alnum-char-gen []
  (gen/fmap string/lower-case gen/char-alphanumeric))

(defn ->string-gen [char-gen min max]
  (gen/fmap string/join (gen/vector char-gen min max)))
