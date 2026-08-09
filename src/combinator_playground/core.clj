(ns combinator-playground.core
  (:gen-class)
  #_{:clj-kondo/ignore [:refer-all :unused-namespace :unused-referred-var]}
  (:require
   [clj-async-profiler.core :as prof]
   [combinator-playground.combinators :refer :all]
   [combinator-playground.lambda :refer :all]
   [combinator-playground.quests :refer :all]
   [combinator-playground.reduce :refer :all]
   [combinator-playground.riddle :refer :all]
   [combinator-playground.search :refer [all-trees search trees]]
   [combinator-playground.utils  :refer :all]
   [combinator-playground.writer :refer [bind fmap]]
   [combinator-playground.interpreter :refer [interpreter]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (when-some [command (first args)]
    (case command
      "combinators"
      (println (combinators->md all-combinators all-rules))

      "quests"
      (print-quests)

      "interpreter"
      (if-some [file (second args)]
        (with-in-str (slurp file)
          (interpreter))
        (interpreter)))))
