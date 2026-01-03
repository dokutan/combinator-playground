(ns combinator-playground.lambda
  (:require
   [combinator-playground.utils :refer [fixpoint unwrap]]))

;;; Conversion of lambda calculus to combinator calculus.

(defn lambda->SKI-1
  "Convert a lambda expression expressed as a vector to an SKI expression.
  λx.x = [x x]"
  [expr]
  (cond
    (not (coll? expr))
    expr

    ;; no (nested) lambda terms in expr
    (not-any? vector? (tree-seq coll? identity expr))
    expr

    ;; found deepest lambda term
    (and
     (vector? expr)
     (not-any? vector? (tree-seq coll? identity (rest expr))))
    (cond
      ;; x → (...) =  x → ...
      (and (= 2 (count expr))
           (seq? (second expr)))
      (lambda->SKI-1 (vec (cons (first expr) (unwrap (second expr)))))

      ;; x → x == I
      (and (= 2 (count expr))
           (or (apply = expr)
               (= (first expr) (unwrap (second expr)))))
      'I

      ;; x → y; x∉y == K y
      (not-any? #(= % (first expr)) (tree-seq coll? identity (rest expr)))
      (cons 'K (list (unwrap (rest expr))))

      ;; x → f x; x∉f == f
      (and (not-any? #(= % (first expr)) (tree-seq coll? identity (butlast (rest expr))))
           (= (first expr) (last expr)))
      (unwrap (butlast (rest expr)))

      ;; x → y z == S(x→y)(x→z)
      (> (count expr) 2)
      (list 'S [(first expr) (unwrap (rest (butlast expr)))] [(first expr) (unwrap (last expr))])

      :else
      'ERROR)

    (vector? expr)
    (lambda->SKI-1 (vec (cons (first expr) (lambda->SKI-1 (rest expr)))))

    (seq? expr)
    (map lambda->SKI-1 expr)))

(def lambda->SKI* (partial fixpoint lambda->SKI-1))
