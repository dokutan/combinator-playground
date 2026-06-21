(ns combinator-playground.core-test
  (:require
   [clojure.test :refer :all]
   [combinator-playground.combinators :refer [SKI]]
   [combinator-playground.core :refer :all]
   [combinator-playground.reduce :refer [reduce*]]
   [combinator-playground.lambda :refer [lambda->SKI*]]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(deftest SKI-test
  (is (= 'x     (last (reduce* SKI '(I x)))))
  (is (= 'x     (last (reduce* SKI '(K x y)))))
  (is (= '(x x) (last (reduce* SKI '(S I I x))))))

(deftest lambda->SKI*-test
  (is (= 'I       (last (lambda->SKI* '[x x]))))
  (is (= 'K       (last (lambda->SKI* '[x [y x]]))))
  (is (= '(K I)   (last (lambda->SKI* '[x [y y]]))))
  (is (= '(S S I) (last (lambda->SKI* '[x [y x y (x y)]])))))
