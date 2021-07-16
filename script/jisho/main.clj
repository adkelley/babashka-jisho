(ns jisho.main
  (:require [clj-http.lite.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn get-blob
  [word i]
  (let [url "https://jisho.org/api/v1/search/words"]
    (-> (client/get url {:query-params {"keyword" word}, :accept :json})
        (get :body)
        json/parse-string
        (get-in ["data"])
        (nth i))))

(defn comma-separated [coll]
  (str/join ", " coll))

(defmulti jisho-query
  (fn [_blob action] (first action)))

(defmethod jisho-query
  "-e" [blob [_ index]]
  (-> blob
      (get "senses")
      (nth index)
      (get "english_definitions")
      (comma-separated)))


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
  (-> blob
      (get "senses")
      (nth index)
      (get "parts_of_speech")
      (comma-separated)))

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
      2 (-> (get-blob (first args) 0)
            (jisho-query [(fnext args) 0])
            print)
      4 (-> (get-blob (first args) (Integer/parseInt (fnext args)))
            (jisho-query [(nth args 2) (Integer/parseInt (nth args 3))])
            print)
      (print "# arguments must be 2 or 4"))))

;(-main "post" ":j")
