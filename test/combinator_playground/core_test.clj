(ns combinator-playground.core-test
  (:require
   [clojure.test :refer :all]
   [combinator-playground.combinators :refer [SKI]]
   [combinator-playground.core :refer :all]
   [combinator-playground.reduce :refer [reduce*]]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(deftest SKI-test
  (testing "SKI"
    (is (= (last (reduce* SKI '(I x))) 'x))))
