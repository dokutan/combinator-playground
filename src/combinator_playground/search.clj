(ns combinator-playground.search
  (:require
   [combinator-playground.reduce :refer [reduce*]]))

(defn partitions
  "Generate all integer partitions of `n`."
  [n]
  (if (zero? n)
    [[]]
    (for [i (range 1 (inc n))
          rest (partitions (- n i))]
      (cons i rest))))

(defn cartesian-product
  "Returns a lazy sequence of vectors, one from each seq in `colls`."
  [& colls]
  (letfn [(step [colls]
            (if (empty? colls)
              (list [])
              (for [x (first colls)
                    more (step (rest colls))]
                (cons x more))))]
    (map vec (step colls))))

(defn trees
  "Generate all trees of exact size `n` with possible `node-values`."
  [node-values n]
  (if (= n 1)
    ;; Base case: single node
    (for [v node-values] v)
    ;; Recursive case
    (let [child-counts (partitions (dec n))]
      (for [v node-values
            counts child-counts
            children (apply cartesian-product
                            (map #(trees node-values %) counts))]
        (apply list v children)))))

(defn all-trees
  "Generate all trees of size <= `n` with possible `node-values`."
  [node-values n]
  (mapcat #(trees node-values %)
          (range 1 (inc n))))

(defn search
  "Perform a brute force search for all terms built from `combinators` with size = `n`,
   that match `pred` when reduced with `parms` up to `limit` steps.

   Example; I from BCKW: (search BCKW 3 ['x] (partial = 'x) 10)"
  [combinators n parms pred limit]
  (->> (trees (keys combinators) n)
       (map #(concat % parms))
       (pmap #(reduce* combinators % limit))
       (map (juxt first last))
       (filter #(pred (second %)))))
