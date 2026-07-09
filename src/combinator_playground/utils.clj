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

(defn over
  "A variadic version of the Ψ combinator.
   `((over f g) x y z …) = (f (g x) (g y) (g z) …)`"
  [f g]
  (fn [& args] (apply f (map g args))))

(defn fork
  "A variadic version of the Φ combinator.
   `((fork f g h …) x y …) = (f (g x y …) (h x y …) …)`"
  [f & fns]
  (fn [& x] (apply f (map #(apply % x) fns))))

(def and-fn
  "`and` as a function."
  (fn [& xs]
    (reduce #(and %1 %2) true xs)))

(def or-fn
  "`or` as a function."
  (fn [& xs]
    (reduce #(or %1 %2) nil xs)))

(def symbols
  "Get a set of symbols in `expr`"
  (comp set flatten list))

(defn complexity
  "Count symbols and parentheses in `expr`"
  [expr]
  (if-not (coll? expr)
    1
    (reduce +
            (count expr)
            (map complexity (filter coll? expr)))))

(defn rules->replacements
  "Given a list of `rules` and a `basis`,
   construct a map of replacements for all combinators that can be expressed in `basis`"
  [rules basis]
  (loop [replacements (reduce into {} (map (fn [sym] {sym sym}) basis))]
    (let [new-replacements
          (->> rules
               (map (fn [[sym expr]]
                      ;; all symbols in `expr` in replacements ?
                      (if (empty? (remove (set (keys replacements)) (symbols expr)))
                        {sym (replace* replacements expr)}
                        {})))
               (reduce into {}))
          new-replacements
          (merge-with #(if ((over < complexity) %1 %2) %1 %2) new-replacements replacements)]
      (if ((over = keys) new-replacements replacements)
        new-replacements
        (recur new-replacements)))))
