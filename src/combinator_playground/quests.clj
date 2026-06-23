(ns combinator-playground.quests
  (:require
   [clojure.pprint :as pprint]
   [combinator-playground.combinators :refer [all-combinators BCKW->MTAB
                                              BCKW->SKI I->SK SKI->BCKW SKI->X]]
   [combinator-playground.lambda :refer [lambda->BCKW* lambda->SKI*]]
   [combinator-playground.reduce :refer [reduce-last]]
   [combinator-playground.search :refer [search]]
   [combinator-playground.utils :refer [fixpoint replace*]]))

;;; Solutions for https://dallaylaen.github.io/ski-interpreter/quest.html

(defn lambda->X* [expr]
  (let [ski (lambda->SKI* expr)]
    (concat
     (vec ski)
     (SKI->X (last ski)))))

(defn quests []
  ["1.1 x→y→y"
   (lambda->SKI* '[x [y y]])

   "1.2 x→f(g(x))"
   (lambda->SKI* '[x f (g x)])

   "1.3 x→h(f(g(x)))"
   (lambda->SKI* '[x h (f (g x))])

   "1.4 f→f x"
   (lambda->SKI* '[f f x])

   "1.5 a→b→c→d→d"
   (lambda->SKI* '[a [b [c [d d]]]])

   "1.6 y→f y x"
   (lambda->SKI* '[y f y x])

   "1.7 M x = x x"
   (lambda->SKI* '[x x x])

   "1.8 M_3 x = x x x"
   (lambda->SKI* '[x x x x])

   "1.9 O x y = y (x y)"
   (lambda->SKI* '[x [y y (x y)]])

   "1.10 I => SK"
   [(I->SK 'I)]

   "2.1 g∘f; B f g x = f(g x)"
   (lambda->SKI* '[f [g [x f (g x)]]])

   "2.2 B_1 a b c d = a (b c d)"
   (lambda->SKI* '[a [b [c [d a (b c d)]]]])

   "2.3 T x y = y x"
   (lambda->SKI* '[x [y y x]])

   "2.4 W x y = x y y"
   (lambda->SKI* '[x [y x y y]])

   "2.5 x→y→x x"
   ['(C (K M))
    "="
    (BCKW->SKI '(C (K M)))]

   "2.6 R x y z = y z x"
   (lambda->SKI* '[x [y [z y z x]]])

   "2.7 C x y z = x z y"
   (lambda->SKI* '[x [y [z x z y]]])

   "2.8 V x y z = z x y"
   (lambda->SKI* '[x [y [z z x y]]])

   "2.9 v3 a b c d = d a b c"
   (lambda->SKI* '[a [b [c [d d a b c]]]])

   "3.1 I x = x [BCKW]"
   [(SKI->BCKW 'I)]

   "3.2 T x y = y x [BCKW]"
   (lambda->BCKW* '[x [y y x]])

   "3.3 M x = x x [BCKW]"
   (lambda->BCKW* '[x x x])

   "3.4 D x y z t = x y (z t) [BCKW]"
   (lambda->BCKW* '[x [y [z [t x y (z t)]]]])

   "3.5 R x y z = y z x [BCKW]"
   (lambda->BCKW* '[x [y [z y z x]]])

   "3.6 L x y = x (y y) [BCKW]"
   (lambda->BCKW* '[x [y x (y y)]])

   "3.7 U a b = b (a a b) [BCKW]"
   (lambda->BCKW* '[a [b b (a a b)]])

   "3.8 a d a c [BCKW]"
   (lambda->BCKW* '[a [b [c [d a d a c]]]])

   "3.9 a (b c) c [BCKW]"
   (lambda->BCKW* '[a [b [c a (b c) c]]])

   "3.10 S [BCKW]"
   [(SKI->BCKW 'S)]

   "4.1 X = x → x(S)(K)"
   (lambda->SKI* '[x x S K])

   "4.2 I [X]"
   (lambda->X* '[x x])

   "4.3 x→y→y [X]"
   (lambda->X* '[x [y y]])

   "4.4 K [X]"
   (lambda->X* '[x [y x]])

   "4.5 S [X]"
   (lambda->X* '[x [y [z x z (y z)]]])

   "5.1 I [MTAB]"
   ['(A M)
    '(A T)
    '(A A)
    '(A B)]

   "5.2 C [MTAB]"
   [(BCKW->MTAB 'C)]

   "5.3 K [MTAB]"
   [(BCKW->MTAB 'K)]

   "5.4 W [MTAB]"
   [(BCKW->MTAB 'W)]

   "6.1 C* a b c d = a b d c"
   (search (select-keys all-combinators '[B C I R T V]) 2 '[a b c d] (partial = '(a b d c)) 5)

   "6.2 Q a b c = b (a c)"
   (search (select-keys all-combinators '[B C I R T V]) 2 '[a b c] (partial = '(b (a c))) 5)

   "6.3 Q₁ a b c = b (c a)"
   (search (select-keys all-combinators '[B C I R T V]) 3 '[a b c] (partial = '(a (c b))) 10)

   "6.4 Q₂ a b c = a (c b)"
   (search (select-keys all-combinators '[B C I R T V]) 4 '[a b c] (partial = '(b (c a))) 10)

   "6.5 Q₃ a b c = c (a b)"
   (search (select-keys all-combinators '[B C I R T V]) 2 '[a b c] (partial = '(c (a b))) 5)

   "6.5 Q₄ a b c = c (b a)"
   (search (select-keys all-combinators '[B C I R T V]) 3 '[a b c] (partial = '(c (b a))) 10)

   "6.7 R₄ a b c d = b c d a"
   (search (select-keys all-combinators '[B C I R T V]) 4 '[a b c d] (partial = '(b c d a)) 20)

   "7.1 J a b c d = a b (a d c)"
   (lambda->SKI* '[a [b [c [d a b (a d c)]]]])

   "7.2 T x y = y x [IJ]"
   '[(J I I)]

   "7.3 Q₁ x y z = x (z y) [IJ]"
   '[(J I)]

   "7.4 B x y z = x (y z) [IJ]"
   ['(search (select-keys all-combinators '[J I T Q₁]) 7 '[x y z] (partial = '(x (y z))) 100)
    "→"
    '(J T (Q₁ T) (J (J T)))
    "→"
    (replace* {'T '(J I I) 'Q₁ '(J I)} '(J T (Q₁ T) (J (J T))))]

   "7.5 C x y z = x z y [IJ]"
   ['(search (select-keys all-combinators '[J I T Q₁]) 6 '[x y z] (partial = '(x z y)) 100)
    "→"
    '(J T (J T) (J T))
    "→"
    (replace* {'T '(J I I)} '(J T (J T) (J T)))]

   "7.6 W x y = x y y"
   ["Taken from Rosser 1935: works but is not efficient enough"
    (reduce-last
     all-combinators
     (last
      (fixpoint
       (partial replace*
                {'T '(J I I)
                 'C '((J T) (J T) (J T))
                 'B '(C (J I C) (J I))})
       '(C (C (B C (C (B J T) T)) T)))))

    "`search` for a better solution"
    '(search (select-keys all-combinators '[J I T Q₁ B C]) 7 '[x y] (partial = '(x y y)) 2000)
    '([(C (J (Q₁ (T T))) (B J T) x y) (x y y)]
      [(C (J (C (C J I)) T C) x y) (x y y)]
      [(C (J (C (C J T)) T I) x y) (x y y)])
    "→"
    '(C (J (Q₁ (T T))) (B J T))

    "→"
    (reduce-last
     all-combinators
     (replace*
      {'Q₁ '(J I)
       'T  '(J I I)
       'C  '(J (J I I) (J (J I I)) (J (J I I)))
       'B  '(J (J I I) ((J I) (J I I)) (J (J (J I I))))}
      '(C (J (Q₁ (T T))) (B J T))))]

   "7.7 I = AA; K = JAA"
   (search (select-keys all-combinators '[J A]) 3 '[x y] (partial = 'x) 5)])

(defn print-quests []
  (pprint/cl-format true "~{~a\n~{ ~a\n~}\n\n~}" (quests)))
