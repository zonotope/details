(ns details.data.internet
  (:require [details.data.company :as company]
            [details.util :as util :refer [->gen]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test.check.generators :as gen]))

(def data (util/read-data-resource "details/data/internet.edn"))

(def username-regex #"^[a-zA-Z0-9._%+-]+$")

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; generators                                                               ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; starts and ends with a lower-case letter, only contains alphanumeric
;; characters as well as "-", "_", "+", is non-empty, and is between 3 and 16
;; characters.
(defn ->username-gen []
  (gen/let [head (util/->lowercase-alnum-char-gen)
            mid (-> data
                    (util/->gen ::username-char)
                    (util/->string-gen 1 14))
            tail (util/->lowercase-alnum-char-gen)]
    (str head mid tail)))

(defn ->domain-suffix-gen []
  (->gen data ::domain-suffix))

(defn ->subdomain-gen []
  (gen/fmap #(-> %
                 (string/replace #" " "")
                 (string/replace #"\W" "-")
                 string/lower-case)
            (company/->name-gen)))

(defn ->domain-gen []
  (gen/let [subdomain (->subdomain-gen)
            tld (->domain-suffix-gen)]
    (str subdomain "." tld)))

(defn ->email-gen []
  (gen/let [username (->username-gen)
            domain (->domain-gen)]
    (str username "@" domain)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; specs                                                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::username (s/with-gen (s/and string?
                                     #(< 0 (count %) 16)
                                     (partial re-matches username-regex))
                    ->username-gen))

(s/def ::subdomain (s/with-gen string?
                     ->subdomain-gen))

(s/def ::domain (s/with-gen string?
                  ->domain-gen))

(s/def ::email (s/with-gen (s/and string?
                                  (partial re-matches email-regex))
                 ->email-gen))
