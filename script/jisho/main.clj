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

(defn comma-separated [coll]
  (str/join ", " coll))

(defmulti jisho-query
  (fn [_blob action] (first action)))

(defmethod jisho-query
  "-e" [blob [_ index]]
  (let [senses (get blob "senses")
        n-senses (count senses)]
    (if (< index n-senses)
      (-> senses
        (nth index)
        (get "english_definitions")
        (comma-separated))
      (str "senses index must be less than " n-senses))))


(defmethod jisho-query
  "-j" [blob [_ _]]
  (-> blob
      (get "japanese")
      first
      (get "word")))

(defmethod jisho-query
  "-r" [blob [_ _]]
  (-> blob
      (get "japanese")
      first
      (get "reading")))

(defmethod jisho-query
  "-p" [blob [_ index]]
  (let [senses (get blob "senses")
        n-senses (count senses)]
    (if (index < n-senses)
      (-> senses
        (nth index)
        (get "parts_of_speech")
        (comma-separated))
      (str "senses index must be less than " n-senses))))

(defmethod jisho-query
  "-l" [blob [_ _]]
  (-> blob
      (get "jlpt")
      first))

(defmethod jisho-query
  :default [_ [_]] "no such query")

(defn -main [& args]
  (let [n-args (count args)]
    (case n-args
      2 (-> (get-blob (first args))
            first
            (jisho-query [(fnext args) 0])
            print)
      4 (let [blob (get-blob (first args))
              n-slugs (count blob)
              slug-index (Integer/parseInt (nth args 1))
              field (nth args 2)
              senses-index (Integer/parseInt (nth args 3))]
          (if (< slug-index n-slugs)
            (-> blob
                (nth slug-index)
                (jisho-query [field senses-index])
                print)
            (print (str "Error: slug-index must be less than " n-slugs))))
      (print "# arguments must be 2 or 4"))))
