(ns combinator-playground.riddle
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [combinator-playground.utils :refer [replace*]]))

;;; A solution for https://blog.happyfellow.dev/a-riddle/

(defn read-riddle []
  (->> "riddle.txt"
       io/resource
       slurp
       str/upper-case
       (map (partial str " "))
       str/join
       (format "(%s)")
       read-string))

(defn riddle []
  (let [;; replace SK terms with other combinators
        r1 (replace* {;; Church numeral 0
                      '(S K) 'A
                      ;; Increment a Church numeral
                      '(S ((S (K S)) K)) '+}
                     (read-riddle))
        ;; cheat to extract all Church numerals: +(+(+ … (+(+ A)) … ))
        r2 (rest (clojure.string/split (clojure.string/join (flatten r1)) #"[^+]+"))
        ;; convert Church numerals to ASCII characters
        r3 (map (comp char count) r2)]
    (clojure.string/join r3)))
