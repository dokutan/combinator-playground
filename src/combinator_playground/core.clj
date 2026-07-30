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
   [combinator-playground.utils  :refer :all]))

(defn -main
  "I don't do a whole lot ... yet."
  [& _args]

  (reduce* SKI '(K x ((S I I) (S I I))))
  (println)
  (reduce* BCKW '(W x y))
  (println)
  (reduce* SKI (BCKW->SKI '(C))))
