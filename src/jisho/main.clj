(ns jisho.main
  (:require [clj-http.lite.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn get-data
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

(defn get-query [blob i section query]
  (let [item (nth (get blob section) i nil)]
     (get item query item)))

(defmulti jisho-query
  (fn [_blob [query _]] query))

(defmethod jisho-query
  "-e" [blob [_ i]]
  (comma-separate-senses
   (get-query blob i "senses" "english_definitions")))

(defmethod jisho-query
  "-d" [blob [_ _]]
  (let [senses (get blob "senses")]
    (reduce (fn [s sense]
              (str s (comma-separate-senses
                       (get sense "english_definitions")) "\n"))
            ""
            senses)))

(defmethod jisho-query
  "-j" [blob [_ i]]
  (get-query blob i "japanese" "word"))

(defmethod jisho-query
  "-r" [blob [_ i]]
  (get-query blob i "japanese" "reading"))

(defmethod jisho-query
  "-p" [blob [_ i]]
  (comma-separate-senses
   (get-query blob i "senses" "parts_of_speech")))

(defmethod jisho-query
  "-l" [blob [_ _]]
  (get-query blob 0 "jlpt" ""))

(defmethod jisho-query
  :default [_ [_ _]] "Usage: bb -m jisho.main <word> < -e || -j || -r || -p || -l >")

(defn -main
  ([word query]
   (-main word "1" query "1"))
  ([word data-i query]
   (-main word data-i query "1"))
  ([word data-i query senses-i]
   (if-let [blob (nth (get-data word) (dec (Integer/parseInt data-i)) nil)]
     (print (jisho-query blob [query (dec (Integer/parseInt senses-i))]))
     (print "Error: data index out of range"))))

(comment
(-main "漢字" "-e")
;; error
  (-main "漢字" "-1" "-e" "1")
;; three arguments
  (-main "漢字" "1" "-e")
;; four arguments
  (-main "漢字" "1" "-e" "1")
  (-main "漢字" "-d")
  (-main "circle" "1" "-e" "2")
  (-main "circle" "1" "-e")
  (println)
)
