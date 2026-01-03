(ns combinator-playground.reduce
  (:require
   [combinator-playground.utils :refer [arity]]))

(defn reduce-1
  "Reduce `expr` once."
  [combinators expr]
  (if-not (seq? expr)
    expr
    (let [c (combinators (first expr))
          a (arity c)]
      (cond
        ;; apply (C x)
        (and c (= (dec (count expr)) a))
        (apply c (rest expr))

        ;; apply (C x) y
        (and c (> (count expr) a))
        (cons
         (apply c (take a (rest expr)))
         (drop a (rest expr)))

        ;; ((x) y) -> (x y)
        (and (seq? (first expr)) (> (count expr) 1))
        (concat (first expr) (rest expr))

        ;; (x) -> x
        (and (seq? expr) (= 1 (count expr)))
        (first expr)

        :else
        (map (partial reduce-1 combinators) expr)))))

(defn reduce*
  "Reduce `expr` to normal form."
  [combinators expr & [limit]]
  (loop [expr' nil expr expr steps []]
    (if (or (= expr' expr)
            (and limit (= limit (count steps))))
      steps
      (let [expr' (reduce-1 combinators expr)]
        (recur expr expr' (conj steps expr))))))
