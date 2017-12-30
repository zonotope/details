(ns details.data.internet
  (:require [details.data.company :as company]
            [details.data.person :as person]
            [details.data :as data :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (data/read-data-resource "details/data/internet.edn"))

(def username-regex #"^[\w\-\.]+$")
(def domain-suffix-regex #"(^[a-z0-9]+$)|(^[a-z0-9]+(.[a-z0-9]+){1,2}$)")
(def subdomain-regex #"^[a-z0-9\-]+$")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ->hacker-data-gen [hacker-key]
  (-> (get-in data [::hacker hacker-key])
      gen/elements))

(def username-a-gen
  (gen/let [adj (->hacker-data-gen :adjective)
            noun (->hacker-data-gen :noun)
            n gen/nat]
    (-> (str adj "-" noun n)
        (string/replace #" " "-"))))

(def username-b-gen
  (gen/let [ingverb (->hacker-data-gen :ingverb)
            abbrev (->gen data ::abbreviation)]
    (-> (str ingverb "-" abbrev)
        (string/replace #" " "")
        (string/lower-case))))

(def username-c-gen
  (gen/fmap #(-> %
                 string/lower-case
                 (string/replace #"\." "")
                 (string/replace #" " "."))
            (->gen person/data ::person/funny-name)))

(defn ->username-gen []
  (gen/one-of [username-a-gen username-b-gen username-c-gen]))

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
                           #(< 0 (count %) 2)))

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
