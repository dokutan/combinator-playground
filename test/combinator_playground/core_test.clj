(ns combinator-playground.core-test
  (:require
   [clojure.test :refer :all]
   [combinator-playground.combinators :refer [all-combinators all-rules
                                              church->int int->church SKI]]
   [combinator-playground.core :refer :all]
   [combinator-playground.lambda :refer :all]
   [combinator-playground.reduce :refer [reduce* reduce-last]]
   [combinator-playground.utils :refer :all]))

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

(deftest combinators->lambda-test
  (run!
   (fn [[combinator replacement]]
     (let [args (range (arity (all-combinators combinator)))]
       (is (= (reduce-last
               all-combinators
               (concat (list (last (lambda->SKIBC* (combinators->lambda all-combinators replacement)))) args))
              (reduce-last
               all-combinators
               (concat (list replacement) args)))
           (str replacement))))
   (remove #(= 'Y (first %)) all-rules)))

(deftest combinators-arity-test
  (run!
   (fn [f]
     (is (= (:arity (meta f))
            (-> f
                .getClass
                .getDeclaredMethods
                first
                .getParameterCount))
         (str "wrong arity in metadata: " f)))
   (vals all-combinators)))

(deftest rules-test
  ;; combinator = replacement ?
  (run!
   (fn [[combinator replacement]]
     (let [args (range (arity (all-combinators combinator)))]
       (is (= (reduce-last all-combinators (concat (list combinator) args))
              (reduce-last all-combinators (concat (list replacement) args)))
           (str combinator " ≠ " replacement))))
   (remove #(= 'Y (first %)) all-rules))
  ;; combinator = combinator as SKI = combinator as BCKW ?
  (let [->SKI  (rules->replacements all-rules '[S K I])
        ->BCKW (rules->replacements all-rules '[B C K W])]
    (run!
     (fn [[combinator _]]
       (let [args (range (arity (all-combinators combinator)))]
         (when-let [as-SKI (->SKI combinator)]
           (is (= (reduce-last all-combinators (concat (list combinator) args))
                  (reduce-last all-combinators (concat (list as-SKI) args)))
               (str combinator " ≠ " as-SKI)))
         (when-let [as-BCKW (->BCKW combinator)]
           (is (= (reduce-last all-combinators (concat (list combinator) args))
                  (reduce-last all-combinators (concat (list as-BCKW) args)))
               (str combinator " ≠ " as-BCKW)))))
     (remove #(= 'Y (first %)) all-rules))))

(deftest church-int-test
  (is (= 0  (church->int all-combinators (int->church 0))))
  (is (= 1  (church->int all-combinators (int->church 1))))
  (is (= 2  (church->int all-combinators (int->church 2))))
  (is (= 99 (church->int all-combinators (int->church 99))))
  (is (= 99 (church->int all-combinators (int->church 99 :inc '(S B))))))

(deftest complexity-test
  (is (= 1 (complexity 'x)))
  (is (= 1 (complexity '(x))))
  (is (= 2 (complexity '(x x))))
  (is (= 3 (complexity '(x x x))))
  (is (= 4 (complexity '(x (x x))))))
