(ns combinator-playground.combinators
  (:require
   [combinator-playground.utils :refer [replace*]]))

;;; Different combinator bases and conversions between them.

;; The arity of the combinator functions is optionally stored in the metadata to avoid reflection in `utils/arity`.

(def all-combinators
  "A selection of standard combinators."
  (let [B1  ^{:arity 4} (fn [a b c d]   (list a (list b c d)))
        B2  ^{:arity 5} (fn [a b c d e] (list a (list b c d e)))
        B3  ^{:arity 4} (fn [a b c d]   (list a (list b (list c d))))
        D   ^{:arity 4} (fn [a b c d]   (list a b (list c d)))
        D1  ^{:arity 5} (fn [a b c d e] (list a b c (list d e)))
        D2  ^{:arity 5} (fn [a b c d e] (list a (list b c) (list d e)))
        Q1  ^{:arity 3} (fn [x y z]     (list x (list z y)))
        Q2  ^{:arity 3} (fn [x y z]     (list y (list z x)))
        Q3  ^{:arity 3} (fn [x y z]     (list z (list x y)))
        Q4  ^{:arity 3} (fn [x y z]     (list z (list y x)))
        PHI ^{:arity 4} (fn [f g h x]   (list f (list g x) (list h x)))
        PSI ^{:arity 4} (fn [f g x y]   (list f (list g x) (list g y)))]
    {'A    ^{:arity 2} (fn [_ y]     y)
     'B    ^{:arity 3} (fn [x y z]   (list x (list y z)))
     'B1   B1
     'B₁   B1
     'B2   B2
     'B₂   B2
     'B3   B3
     'B₃   B3
     'C    ^{:arity 3} (fn [x y z]   (list x z y))
     'D    D
     'D1   D1
     'D₁   D1
     'D2   D2
     'D₂   D2
     'E    ^{:arity 5} (fn [a b c d e] (list a b (list c d e)))
     'F    ^{:arity 3} (fn [a b c]   (list c b a))
     'G    ^{:arity 4} (fn [a b c d] (list a d (list b c)))
     'H    ^{:arity 3} (fn [a b c]   (list a b c b))
     'I    ^{:arity 1} (fn [x]       x)
     'J    ^{:arity 4} (fn [a b c d] (list a b (list a d c)))
     'K    ^{:arity 2} (fn [x _]     x)
     'L    ^{:arity 2} (fn [x y]     (list x (list y y)))
     'M    ^{:arity 1} (fn [x]       (list x x))
     'N    ^{:arity 4} (fn [a b c d] (list a b (list c b d)))
     'O    ^{:arity 2} (fn [a b]     (list b (list a b)))
     'Q    ^{:arity 3} (fn [x y z]   (list y (list x z)))
     'Q1   Q1
     'Q₁   Q1
     'Q2   Q2
     'Q₂   Q2
     'Q3   Q3
     'Q₃   Q3
     'Q4   Q4
     'Q₄   Q4
     'R    ^{:arity 3} (fn [x y z]   (list y z x))
     'S    ^{:arity 3} (fn [x y z]   (list x z (list y z)))
     'T    ^{:arity 2} (fn [x y]     (list y x))
     'V    ^{:arity 3} (fn [x y z]   (list z x y))
     'W    ^{:arity 2} (fn [x y]     (list x y y))
     'X    ^{:arity 1} (fn [x]       (list x 'S 'K))
     'Φ    PHI
     'PHI  PHI
     'Ψ    PSI
     'PSI  PSI
     'B'   D
     'C'   ^{:arity 4} (fn [a b c d] (list a (list b d) c))
     'S'   PHI}))

(def all-rules
  "A list of common combinator definitions. Useful with `utils/rules->replacements`."
  [['I '(S K K)]
   ;; BCKW->SKI
   ['B '(S (K S) K)]
   ['C '(S (S (K S) (S (K K) S)) (K K))]
   ['W '(S S (K I))]
   ;; SKI->BCKW
   ['S '(B (B W) (B B C))]
   ['I '(W K)]
   ;; SKI->X
   ['S '(X (X (X (X X))))]
   ['K '(X (X (X X)))]
   ['I '(X X)]
   ;; MTAB->BCKW
   ['T '(C (C K C))]
   ['A '(K (C K C))]
   ['M '(W (C K C))]
   ;; BCKW->MTAB
   ['C '((B B T) (B B T) (B B T))]
   ['K '(B (T A) (B B T))]
   ['W '((B B T) (B B T) (B B T) (B M (B B T)))]
   ;; JA
   ['K '(J A A)]
   ['I '(A A)]
   ;; JI, see Rosser 1935
   ['T '(J I I)]
   ['C '(J T (J T) (J T))]
   ['B '(C (J I C) (J I))]
   ['W '(C (C (B C (C (B J T) T)) T))]
   ;; other combinator definitions
   ['D '(B B)]
   ['D1 '(B D)]
   ['D₁ 'D1]
   ['D1 'D₁]
   ['D2 '(D D)]
   ['D₂ 'D2]
   ['D2 'D₂]
   ['E '(B (B B B))]
   ['F '(E T T E T)]
   ['G '(B B C)]
   ['H '(B W (B C))]
   ['L '(C B M)]
   ['M '(S I I)]
   ['O '(S I)]
   ['Q '(C B)]
   ['V '(B C T)]])

(def SKI
  (select-keys all-combinators '[S K I]))

(def BCKW
  (select-keys all-combinators '[B C K W]))

(def XSKI
  (select-keys all-combinators '[X S K I]))

(def MTAB
  (select-keys all-combinators '[M T A B]))

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
