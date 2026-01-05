(ns combinator-playground.combinators
  (:require
   [combinator-playground.utils :refer [replace*]]))

;;; Different combinator bases and conversions between them.

;; The arity of the combinator functions is optionally stored in the metadata to avoid reflection.

(def SKI
  {'I ^{:arity 1} (fn [x] x)
   'K ^{:arity 2} (fn [x _] x)
   'S ^{:arity 3} (fn [x y z] (list x z (list y z)))})

(def BCKW
  {'B ^{:arity 3} (fn [x y z] (list x (list y z)))
   'C ^{:arity 3} (fn [x y z] (list x z y))
   'K ^{:arity 2} (fn [x _] x)
   'W ^{:arity 2} (fn [x y] (list x y y))})

(def XSKI
  (into SKI {'X ^{:arity 1} (fn [x] (list x 'S 'K))}))

(def MTAB
  {'M ^{:arity 1} (fn [x] (list x x))
   'T ^{:arity 2} (fn [x y] (list y x))
   'A ^{:arity 2} (fn [_ y] y)
   'B ^{:arity 3} (fn [x y z] (list x (list y z)))})

(def BCKW->SKI
  (partial
   replace*
   {'B '(S (K S) K)
    'C '(S (S (K S) (S (K K) S)) (K K))
    'W '(S S (K I))}))

(def SKI->BCKW
  (partial
   replace*
   {'S '(B (B W) (B B C))
    'I '(W K)}))

(def I->SK
  (partial
   replace*
   {'I '(S K K)}))

(def SKI->X
  (partial
   replace*
   {'S '(X (X (X (X X))))
    'K '(X (X (X X)))
    'I '(X X)}))

(def MTAB->BCKW
  (partial
   replace*
   {'T '(C (C K C))
    'A '(K (C K C))
    'M '(W (C K C))}))

(def BCKW->MTAB
  (partial
   replace*
   {'C '((B B T) (B B T) (B B T))
    'K '(B (T A) (B B T))
    'W '((B B T) (B B T) (B B T) (B M (B B T)))}))
