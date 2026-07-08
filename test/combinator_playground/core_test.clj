(ns combinator-playground.core-test
  (:require
   [clojure.test :refer :all]
   [combinator-playground.combinators :refer [SKI]]
   [combinator-playground.core :refer :all]
   [combinator-playground.lambda :refer [lambda->BCKW* lambda->SKI*
                                         lambda->SKIBC* lambda->SKIBC'*]]
   [combinator-playground.reduce :refer [reduce*]]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(deftest SKI-test
  (is (= 'x     (last (reduce* SKI '(I x)))))
  (is (= 'x     (last (reduce* SKI '(K x y)))))
  (is (= '(x x) (last (reduce* SKI '(S I I x))))))

(deftest lambda->combinators-test
  (testing "lambda->SKI*"
    (is (= 'I                               (last (lambda->SKI* '[x x]))))
    (is (= 'K                               (last (lambda->SKI* '[x [y x]]))))
    (is (= '(K I)                           (last (lambda->SKI* '[x [y y]]))))
    (is (= '(S S I)                         (last (lambda->SKI* '[x [y x y (x y)]])))))
  (testing "lambda->BCKW*"
    (is (= '(W K)                           (last (lambda->BCKW* '[x x]))))
    (is (= 'K                               (last (lambda->BCKW* '[x [y x]]))))
    (is (= '(K (W K))                       (last (lambda->BCKW* '[x [y y]]))))
    (is (= '(B W (W (B (C (B B C)) (W K)))) (last (lambda->BCKW* '[x [y x y (x y)]])))))
  (testing "lambda->SKIBC*"
    (is (= 'I                               (last (lambda->SKIBC* '[x x]))))
    (is (= 'K                               (last (lambda->SKIBC* '[x [y x]]))))
    (is (= '(K I)                           (last (lambda->SKIBC* '[x [y y]]))))
    (is (= '(S S I)                         (last (lambda->SKIBC* '[x [y x y (x y)]]))))
    (is (= '(B (S I) (C I))                 (last (lambda->SKIBC* '[x [y y (y x)]])))))
  (testing "lambda->SKIBC'*"
    (is (= 'I                               (last (lambda->SKIBC'* '[x x]))))
    (is (= 'K                               (last (lambda->SKIBC'* '[x [y x]]))))
    (is (= '(K I)                           (last (lambda->SKIBC'* '[x [y y]]))))
    (is (= '(S (C' S' I I) I)               (last (lambda->SKIBC'* '[x [y x y (x y)]]))))
    (is (= '(B' S I (C I))                  (last (lambda->SKIBC'* '[x [y y (y x)]]))))))
