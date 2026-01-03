(ns combinator-playground.quests
  (:require
   [clojure.pprint :as pprint]
   [combinator-playground.combinators :refer [BCKW I->SK SKI->BCKW SKI->X]]
   [combinator-playground.lambda :refer [lambda->SKI*]]
   [combinator-playground.reduce :refer [reduce*]]))

;;; Solutions for https://dallaylaen.github.io/ski-interpreter/quest.html

(defn lambda->BCKW* [expr]
  (let [ski (lambda->SKI* expr)]
    (concat
     (vec ski)
     (reduce* BCKW (SKI->BCKW (last ski))))))

(defn lambda->X* [expr]
  (let [ski (lambda->SKI* expr)]
    (concat
     (vec ski)
     (SKI->X (last ski)))))

(def quests
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

   "2.5 x→y→x x TODO!!!"
   (lambda->SKI* '[x [y M x]])

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

   "3.7 U a b = a (b b a) [BCKW]"
   (lambda->BCKW* '[a [b a (b b a)]])

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
   (lambda->X* '[x [y [z x z (y z)]]])])

(defn print-quests []
  (pprint/cl-format true "~{~a\n~{ ~a\n~}\n\n~}" quests))
