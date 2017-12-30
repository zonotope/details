(ns details.data.internet
  (:require [details.data.company :as company]
            [details.data :as data :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (data/read-data-resource "details/data/internet.edn"))

(def username-regex #"^[\W\-\.]+$")
(def domain-suffix-regex #"(^[a-z0-9]+$)|(^[a-z0-9]+(.[a-z0-9]+){1,2}$)")
(def subdomain-regex #"^[a-z0-9\-]$")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; starts and ends with a lower-case letter, only contains alphanumeric
;; characters as well as "-", "_", "+", is non-empty, and is between 3 and 16
;; characters.
(defn ->username-gen []
  (gen/let [head (data/->lowercase-alnum-char-gen)
            mid (-> data
                    (data/->gen ::username-char)
                    (data/->string-gen 1 14))
            tail (data/->lowercase-alnum-char-gen)]
    (str head mid tail)))

(defn ->domain-suffix-gen []
  (->gen data ::domain-suffix))

(defn name->subdomain [n]
  (-> n
      (string/replace #" " "-")
      (string/replace #"\W" "")
      string/lower-case))

(defn ->subdomain-gen []
  (gen/fmap name->subdomain (company/->name-gen)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; specs                                                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::username (-> (s/and string?
                             #(< 0 (count %) 16)
                             (partial re-matches username-regex))
                      (s/with-gen ->username-gen)))

(s/def ::subdomain (-> (s/and string?
                              (partial re-matches subdomain-regex))
                       (s/with-gen ->subdomain-gen)))

(s/def ::subdomains (s/and (s/coll-of ::subdomain)
                           seq))

(s/def ::domain (s/keys :req [::subdomains ::domain-suffix]))

(s/def ::domain-suffix (-> (s/and string?
                                  (partial re-matches domain-suffix-regex))
                           (s/with-gen ->domain-suffix-gen)))

(s/def ::email (s/keys :req [::username ::domain]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; render                                                                   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmethod data/render ::domain [_ {::keys [subdomains domain-suffix]}]
  (->> (concat subdomains [domain-suffix])
       (string/join ".")))

(defmethod data/render ::email [_ {::keys [username domain]}]
  (str username "@" (data/render ::domain domain)))
