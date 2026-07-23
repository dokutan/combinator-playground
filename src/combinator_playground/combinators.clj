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
        M2  ^{:arity 2} (fn [x y]       (list x y (list x y)))
        Q1  ^{:arity 3} (fn [x y z]     (list x (list z y)))
        Q2  ^{:arity 3} (fn [x y z]     (list y (list z x)))
        Q3  ^{:arity 3} (fn [x y z]     (list z (list x y)))
        Q4  ^{:arity 3} (fn [x y z]     (list z (list y x)))
        W1  ^{:arity 2} (fn [x y]       (list y x x))
        PHI ^{:arity 4} (fn [f g h x]   (list f (list g x) (list h x)))
        PSI ^{:arity 4} (fn [f g x y]   (list f (list g x) (list g y)))
        iota ^{:arity 1} (fn [x]        (list x 'S 'K))]
    {'A    ^{:arity 2} (fn [_ y]     y)
     'B    ^{:arity 3} (fn [x y z]     (list x (list y z)))
     'B1   B1
     'B₁   B1
     'B2   B2
     'B₂   B2
     'B3   B3
     'B₃   B3
     'C    ^{:arity 3} (fn [x y z]     (list x z y))
     'C*   ^{:arity 4} (fn [a b c d]   (list a b d c))
     'C**  ^{:arity 5} (fn [a b c d e] (list a b c e d))
     'D    D
     'D1   D1
     'D₁   D1
     'D2   D2
     'D₂   D2
     'E    ^{:arity 5} (fn [a b c d e] (list a b (list c d e)))
     'Ê    ^{:arity 7} (fn [a b c d e f g] (list a (list b c d) (list e f g)))
     'F    ^{:arity 3} (fn [a b c]     (list c b a))
     'F*   ^{:arity 4} (fn [a b c d]   (list a d c b))
     'F**  ^{:arity 5} (fn [a b c d e] (list a b e d c))
     'G    ^{:arity 4} (fn [a b c d]   (list a d (list b c)))
     'H    ^{:arity 3} (fn [a b c]     (list a b c b))
     'I    ^{:arity 1} (fn [x]         x)
     'I*   ^{:arity 2} (fn [x y]       (list x y))
     'J    ^{:arity 4} (fn [a b c d]   (list a b (list a d c)))
     'K    ^{:arity 2} (fn [x _]       x)
     'L    ^{:arity 2} (fn [x y]       (list x (list y y)))
     'M    ^{:arity 1} (fn [x]         (list x x))
     'M2   M2
     'M₂   M2
     'N    ^{:arity 4} (fn [a b c d]   (list a b (list c b d)))
     'O    ^{:arity 2} (fn [a b]       (list b (list a b)))
     'Q    ^{:arity 3} (fn [x y z]     (list y (list x z)))
     'Q1   Q1
     'Q₁   Q1
     'Q2   Q2
     'Q₂   Q2
     'Q3   Q3
     'Q₃   Q3
     'Q4   Q4
     'Q₄   Q4
     'R    ^{:arity 3} (fn [x y z]     (list y z x))
     'R*   ^{:arity 4} (fn [a b c d]   (list a c d b))
     'R**  ^{:arity 5} (fn [a b c d e] (list a b d e c))
     'S    ^{:arity 3} (fn [x y z]     (list x z (list y z)))
     'T    ^{:arity 2} (fn [x y]       (list y x))
     'U    ^{:arity 2} (fn [x y]       (list y (list x x y)))
     'V    ^{:arity 3} (fn [x y z]     (list z x y))
     'V*   ^{:arity 4} (fn [a b c d]   (list a c b d))
     'V**  ^{:arity 5} (fn [a b c d e] (list a b e c d))
     'W    ^{:arity 2} (fn [x y]       (list x y y))
     'W1   W1
     'W¹   W1
     'W*   ^{:arity 3} (fn [x y z]     (list x y z z))
     'W**  ^{:arity 4} (fn [a b c d]   (list a b c d d))
     'ι    iota
     'iota iota
     'Φ    PHI
     'PHI  PHI
     'Ψ    PSI
     'PSI  PSI
     'B'   D
     'C'   ^{:arity 4} (fn [a b c d]   (list a (list b d) c))
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
   ;; SKI->iota
   ['S '(ι K)]
   ['K '(ι A)]
   ['A '(ι I)]
   ['I '(ι ι)]
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
   ['B1 '(D B)]
   ['B₁ 'B1]
   ['B1 'B₁]
   ['B2 '(D B₁)]
   ['B₂ 'B2]
   ['B2 'B₂]
   ['B3 '(B D B)]
   ['B₃ 'B3]
   ['B3 'B₃]
   ['B' 'D]
   ['C* '(B C)]
   ['C** '(B C*)]
   ['D '(B B)]
   ['D 'B']
   ['D1 '(B D)]
   ['D₁ 'D1]
   ['D1 'D₁]
   ['D2 '(D D)]
   ['D₂ 'D2]
   ['D2 'D₂]
   ['E '(B B₁)]
   ['Ê '(E E)]
   ['F '(E T T E T)]
   ['F* '(B C* R*)]
   ['F** '(B F*)]
   ['G '(D C)]
   ['H '(B W (B C))]
   ['I* '(S (S K))]
   ['L '(C B M)]
   ['M '(S I I)]
   ['O '(S I)]
   ['Q '(C B)]
   ['Q1 '(B C B)]
   ['Q1 'Q₁]
   ['Q₁ 'Q1]
   ['Q2 '(C Q₁)]
   ['Q2 'Q₂]
   ['Q₂ 'Q2]
   ['Q3 '(B T)]
   ['Q3 'Q₃]
   ['Q₃ 'Q3]
   ['Q4 '(F* B)]
   ['Q4 'Q₄]
   ['Q₄ 'Q4]
   ['R '(D T)]
   ['R* '(C* C*)]
   ['R** '(B R*)]
   ['T '(C I)]
   ['U '(L O)]
   ['V '(B C T)]
   ['W¹ '(C W)]
   ['W¹ 'W1]
   ['W1 'W¹]
   ['W* '(B W)]
   ['W** '(B W*)]
   ['ι 'iota]
   ['iota 'ι]
   ['Φ '(B₁ S B)]
   ['Φ 'PHI]
   ['PHI 'Φ]
   ['Φ 'S']
   ['S' 'Φ]
   ['Ψ '(B (S Φ C B) B)]
   ['Ψ 'PSI]
   ['PSI 'Ψ]])

(def SKI
  (select-keys all-combinators '[S K I]))

(def BCKW
  (select-keys all-combinators '[B C K W]))

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

(def SKI->iota
  (partial
   replace*
   {'S '(ι (ι (ι (ι ι))))
    'K '(ι (ι (ι ι)))
    'I '(ι ι)}))

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
