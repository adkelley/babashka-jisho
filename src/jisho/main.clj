(ns jisho.main
  (:require [clj-http.lite.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn get-blob
  [word]
  (let [url "https://jisho.org/api/v1/search/words"]
    (-> (client/get url {:query-params {"keyword" word}, :accept :json})
        (get :body)
        json/parse-string
        (get-in ["data"]))))

(defn comma-separate-senses [coll]
  (if (coll? coll)
    (str/join ", " coll)
    "Error: senses out of range"))

(defn get-query [blob index section query]
  (let [item (nth (get blob section) index nil)]
     (get item query item)))

(defmulti jisho-query
  (fn [_slug action] (first action)))

(defmethod jisho-query
  "-e" [slug [_ index]]
  (comma-separate-senses (get-query slug index "senses" "english_definitions")))

(defmethod jisho-query
  "-d" [slug [_ _]]
  (let [senses (get slug "senses")]
    (reduce (fn [xs sense]
              (str xs (comma-separate-senses (get sense "english_definitions")) "\n"))
            ""
            senses)))

(defmethod jisho-query
  "-j" [slug [_ _]]
  (get-query slug 0 "japanese" "word"))

(defmethod jisho-query
  "-r" [slug [_ _]]
  (get-query slug 0 "japanese" "reading"))

(defmethod jisho-query
  "-p" [slug [_ index]]
  (comma-separate-senses (get-query slug index "senses" "parts_of_speech")))

(defmethod jisho-query
  "-l" [slug [_ _]]
  (get-query slug 0 "jlpt" ""))

(defmethod jisho-query
  :default [_ [_ _]] "Usage: bb -m jisho.main <word> < -e || -j || -r || -p || -l >")

(defn -main [& args]
  (let [n-args (count args)]
    (case n-args
      2 (-> (get-blob (first args))
            first
            (jisho-query [(fnext args) 0])
            print)
      4 (if-let [slug (nth (get-blob (first args)) (Integer/parseInt (nth args 1)) nil)]
          (print
            (jisho-query slug [(nth args 2) (Integer/parseInt (nth args 3))]))
          (print "Error: slug-index out of range"))
      (print "Error: # arguments must be 2 or 4"))))
