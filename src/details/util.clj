(ns details.util
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(defn read-data-resource [path]
  (-> path io/resource slurp edn/read-string))

(defn ->gen [data k]
  (-> (get data k)
      s/gen))
