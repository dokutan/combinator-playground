(ns combinator-playground.quests
  (:require
   [clojure.pprint :as pprint]
   [combinator-playground.combinators :refer [BCKW->MTAB BCKW->SKI I->SK
                                              SKI->BCKW SKI->X]]
   [combinator-playground.lambda :refer [lambda->SKI* lambda->BCKW*]]
   [combinator-playground.utils :refer [replace*]]))

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

   "6.1 J a b c d = a b (a d c)"
   (lambda->SKI* '[a [b [c [d a b (a d c)]]]])

   "6.2 T x y = y x [IJ]"
   '[(J I I)]

   "6.3 Q1 x y z = x (z y) [IJ]"
   '[(J I)]

   "6.4 B x y z = x (y z) [IJ]"
   ['(search
      {'J (fn [a b c d] (list a b (list a d c)))
       'I (fn [x] x)
       'T (fn [x y] (list y x))
       'Q1 (fn [x y z] (list x (list z y)))}
      7
      '[x y z]
      (partial = '(x (y z)))
      100)
    "→"
    '(J T (Q1 T) (J (J T)))
    "→"
    (replace* {'T '(J I I) 'Q1 '(J I)} '(J T (Q1 T) (J (J T))))]

   "6.5 C x y z = x z y [IJ]"
   ['(search
      {'J (fn [a b c d] (list a b (list a d c)))
       'I (fn [x] x)
       'T (fn [x y] (list y x))
       'Q1 (fn [x y z] (list x (list z y)))}
      6
      '[x y z]
      (partial = '(x z y))
      100)
    "→"
    '(J T (J T) (J T))
    "→"
    (replace* {'T '(J I I)} '(J T (J T) (J T)))]])

(defn print-quests []
  (pprint/cl-format true "~{~a\n~{ ~a\n~}\n\n~}" (quests)))
