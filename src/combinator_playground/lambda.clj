(ns combinator-playground.lambda
  (:require
   [combinator-playground.utils :refer [fixpoint unwrap]]))

;;; Conversion of lambda calculus to combinator calculus through bracket abstraction.
;;; References:
;;; - https://en.wikipedia.org/wiki/Combinatory_logic#Conversion_of_a_lambda_term_to_an_equivalent_combinatorial_term
;;; - https://www.cantab.net/users/antoni.diller/brackets/intro.html
;;; - https://www.sciencedirect.com/science/article/pii/S1571066120300116

(defn lambda->combinators-1
  "Perform a single iteration of bracket abstraction.
   `rules` should be a function of `expr` that implements the abstraction rules."
  [rules expr]
  (cond
    ;; atom
    (not (coll? expr))
    expr

    ;; no (nested) lambda terms in expr
    (not-any? vector? (tree-seq coll? identity expr))
    expr

    ;; deepest lambda term
    (and (vector? expr)
         (not-any? vector? (tree-seq coll? identity (rest expr))))
    (if
      ;; x → (...) ==  x → ...
      (and (= 2 (count expr))
           (seq? (second expr)))
      (let [body (unwrap (second expr))]
        (vec ((if (coll? body) cons list) (first expr) body)))

      ;; else
      (rules expr))

    ;; lambda term
    (vector? expr)
    (lambda->combinators-1 rules (vec (cons (first expr) (lambda->combinators-1 rules (rest expr)))))

    ;; application
    (seq? expr)
    (map (partial lambda->combinators-1 rules) expr)))

(defn rules-SKI
  [expr]
  (cond
    ;; x → x == I
    (and (= 2 (count expr))
         (or (apply = expr)
             (= (first expr) (unwrap (second expr)))))
    'I

    ;; x → f x == f; if x ∉ FV(f), η-conversion
    (and (not-any? (partial = (first expr)) (tree-seq coll? identity (butlast (rest expr))))
         (= (first expr) (last expr)))
    (unwrap (butlast (rest expr)))

    ;; x → y == K y; if x ∉ FV(y)
    (not-any? (partial = (first expr)) (tree-seq coll? identity (rest expr)))
    (cons 'K (list (unwrap (rest expr))))

    ;; x → y z == S(x→y)(x→z)
    (> (count expr) 2)
    (list 'S [(first expr) (unwrap (rest (butlast expr)))] [(first expr) (unwrap (last expr))])

    :else
    'ERROR))

(defn rules-BCKW
  [expr]
  (cond
    ;; x → x == WK
    (and (= 2 (count expr))
         (or (apply = expr)
             (= (first expr) (unwrap (second expr)))))
    '(W K)

    ;; x → f x == f; if x ∉ FV(f), η-conversion
    (and (not-any? (partial = (first expr)) (tree-seq coll? identity (butlast (rest expr))))
         (= (first expr) (last expr)))
    (unwrap (butlast (rest expr)))

    ;; x → y == K y; if x ∉ FV(y)
    (not-any? (partial = (first expr)) (tree-seq coll? identity (rest expr)))
    (cons 'K (list (unwrap (rest expr))))

    ;; x → y z == By(x→z); if x ∉ FV(y)
    (and (> (count expr) 2)
         (not-any? (partial = (first expr)) (tree-seq coll? identity (butlast (rest expr)))))
    (list 'B (unwrap (butlast (rest expr))) [(first expr) (unwrap (last expr))])

    ;; x → y z == C(x→y)z; if x ∉ FV(z)
    (and (> (count expr) 2)
         (not-any? (partial = (first expr)) (tree-seq coll? identity (last expr))))
    (list 'C [(first expr) (unwrap (butlast (rest expr)))] (unwrap (last expr)))

    ;; x → y z == W(B(C x→y)(x→z))
    (> (count expr) 2)
    (list 'W (list 'B (list 'C [(first expr) (unwrap (rest (butlast expr)))]) [(first expr) (unwrap (last expr))]))

    :else
    'ERROR))

(def lambda->SKI*
  "Convert a lambda expression expressed as a vector to an SKI expression through bracket abstraction.
   Returns a vector of all intermediate results.
   λx.x = [x x]"
  (partial fixpoint (partial lambda->combinators-1 rules-SKI)))
(alter-meta! #'lambda->SKI* assoc :arglists '([expr]))

(def lambda->BCKW*
  "Convert a lambda expression expressed as a vector to an BCKW expression through bracket abstraction.
   Returns a vector of all intermediate results.
   λx.x = [x x]"
  (partial fixpoint (partial lambda->combinators-1 rules-BCKW)))
(alter-meta! #'lambda->BCKW* assoc :arglists '([expr]))
