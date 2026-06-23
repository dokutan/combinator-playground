(ns combinator-playground.core
  (:gen-class)
  #_{:clj-kondo/ignore [:refer-all :unused-namespace :unused-referred-var]}
  (:require
   [combinator-playground.combinators :refer :all]
   [combinator-playground.reduce :refer :all]
   [combinator-playground.lambda :refer [lambda->SKI* lambda->BCKW*]]
   [combinator-playground.quests :refer :all]
   [combinator-playground.search :refer [search]]
   [clj-async-profiler.core :as prof]))

(defn -main
  "I don't do a whole lot ... yet."
  [& _args]

  (reduce* SKI '(K x ((S I I) (S I I))))
  (println)
  (reduce* BCKW '(W x y))
  (println)
  (reduce* SKI (BCKW->SKI '(C))))
