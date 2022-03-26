(ns jisho.main
  (:require [clj-http.lite.client :as client]
            [cheshire.core :as json] 
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

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

(defn missing-required?
  "Ensure command line arguments contains word to process"
  [arguments]
  (= 0 (count arguments)))

(defn parts-of-speech [arguments]
  (let [word (first arguments)
        blob (nth (get-data word) 0 nil)]
    (comma-separate-senses
     (get-query blob 0 "senses" "parts_of_speech"))))

(defn jlpt-level [arguments]
  (let [word (first arguments)
        blob (nth (get-data word) 0 nil)
        jlpt (get-query blob 0 "jlpt" "")]
    (if (nil? jlpt)
      ""
      ;; extract level only
      (last jlpt))))

(defn japanese-reading
  "Return the japanese reading for kanji"
  [arguments]
  (let [word (first arguments)
        blob (nth (get-data word) 0 nil)]
    (get-query blob 0 "japanese" "reading")))

(defn japanese->english
  "Return the english definition"
  [arguments]
  (let [word (first arguments)
        blob (nth (get-data word) 0 nil)]
    (comma-separate-senses
     (get-query blob 0 "senses" "english_definitions"))))

(defn english->japanese
  "Return the japanese word"
  [arguments]
  (let [word (first arguments)
        blob (nth (get-data word) 0 nil)
        japanese (get-query blob 0 "japanese" "word")
        show-kana? true]
    (if show-kana?
      (str japanese "  [" (get-query blob 0 "japanese" "reading") "]")
      japanese)))

(defonce cli-options
  [["-p" "--part-of-speech" "Part of Speech" :default true]
   ["-l" "--jlpt-level" "JLPT Level" :default false]
   ["-r" "--japanese-reading" "Japanese Reading" :default false]
   ["-j" "--english->japanese" "English -> Japanese" :default false]
   ["-e" "--japanese->english" "Japanese -> English" :default false]
   ["-h" "--help" "word <options>" :default false]])

(defn -main
  "Extract the command line arguments and process the request"
  [& args]
  (let [{:keys [options arguments summary _errors]} (parse-opts args cli-options)]
    (cond
      (or (missing-required? arguments)
          (:help options))              (println summary)
      (:jlpt-level options)             (print (jlpt-level arguments))
      (:japanese-reading options)       (print (japanese-reading arguments))
      (:english->japanese options)      (print (english->japanese arguments))
      (:japanese->english options)      (print (japanese->english arguments))
      ;; default
      (:part-of-speech options)         (print (parts-of-speech arguments))
      )))

(comment
  (-main)      
  (-main "-h") 
  (-main "漢字" "-p") 
  (-main "漢字" "-l")
  (-main "漢" "-l")
  (-main "漢" "-r")
  (-main "漢字" "-r")
  (-main "kanji" "-j")
  (-main "kanji" "-j" "-k")
  (-main "漢字" "-e")
,)
