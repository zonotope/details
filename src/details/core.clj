(ns details.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn generate [spec]
  (-> spec s/gen gen/generate))

(defmulti render (fn [spec data] spec))
