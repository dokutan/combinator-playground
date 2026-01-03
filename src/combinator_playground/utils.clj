(ns combinator-playground.utils)

(defn arity
  "Get the arity of function `f`, checks metadata first."
  [f]
  (when (fn? f)
    (or
     (:arity (meta f))
     (-> f
         .getClass
         .getDeclaredMethods
         first
         .getParameterCount))))

(defn unwrap
  "Unwrap a nested list `x` until a single element."
  [x]
  (if (and (coll? x) (= 1 (count x)))
    (unwrap (first x))
    x))

(defn fixpoint [f x]
  (reductions #(if (= %1 %2) (reduced %1) %2) (iterate f x)))

(defn replace*
  "Nested replace in `expr`."
  [replacements expr]
  (if-not (seq? expr)
    (replacements expr expr)
    (map
     (fn [term]
       (cond
         (replacements term)
         (replacements term)

         (seq? term)
         (replace* replacements term)

         :else
         term))
     expr)))
