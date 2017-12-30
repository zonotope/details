(ns details.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defmulti render (fn [spec data] spec))
(defmethod render :default [_ data] data)

(defn generate
  ([spec]
   (->> spec
        s/gen
        gen/generate
        (render spec)))

  ([spec gen]
   (generate (s/with-gen spec (constantly gen)))))
