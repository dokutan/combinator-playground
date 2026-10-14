(ns combinator-playground.tree 
  (:require
    [combinator-playground.utils :refer [normalize replace*]]))

;;; Tree calculus and triage calculus

;; Both share the △ (node) operator but have different reduction rules.
;; The (original) tree calculus is described in Reflective Programs in Tree Calculus by Barry Jay: https://github.com/barry-jay-personal/tree-calculus/blob/master/tree_book.pdf
;; Triage calculus was suggested by Johannes Bader: https://treecalcul.us

(defn node? [sym]
  (and (symbol? sym)
       (or (= 'node sym)
           (= '△   sym))))

(def tree-node
  "The △/node operator from tree calculus. Not a combinator, but usable like `all-combinators`."
  (let [node ^{:arity 3}
        (fn [x y z]
          (cond
            ;; △△yz → y
            (node? x)
            y

            ;; △(△x)yz → yz(xz)
            (and (seq?  x)
                 (node? (first x))
                 (= 2   (count x)))
            (list y z (list (second x) z))

            ;; △(△wx)yz → zwx
            (and (seq?  x)
                 (node? (first x))
                 (= 3   (count x)))
            (list z (second x) (nth x 2))

            :else
            (normalize (list '△ x y z))))]
    {'node node
     '△   node}))

(def triage-node
  "The △/node operator from triage calculus. Not a combinator, but usable like `all-combinators`."
  (let [node ^{:arity 3}
        (fn [x y z]
          (cond
            ;; △△yz → y
            (node? x)
            y

            ;; △(△x)yz → xz(yz)
            (and (seq?  x)
                 (= 2   (count x))
                 (node? (first x)))
            (list (second x) z (list y z))

            ;; △(△wx)y△ → w
            (and (seq?  x)
                 (= 3   (count x))
                 (node? (first x))
                 (node? z))
            (second x)

            ;; △(△wx)y(△u) → xu
            (and (seq?  x)
                 (= 3   (count x))
                 (node? (first x))
                 (seq? z)
                 (= 2   (count z))
                 (node? (first z)))
            (list (nth x 2) (second z))

            ;; △(△wx)y(△uv) → yuv
            (and (seq?  x)
                 (= 3   (count x))
                 (node? (first x))
                 (seq? z)
                 (= 3   (count z))
                 (node? (first z)))
            (list y (second z) (nth z 2))

            :else
            (normalize (list '△ x y z))))]
    {'node node
     '△   node}))

;; compare to `all-rules`
(def tree-rules
  [['node '△]
   ['△ 'node]
   ['S '(△ (△ (△ △ (△ (△ △) (△ △ △)))) (△ (△ (△ △)) (△ △ (△ (△ △) (△ △ △)))))]
   ['K '(△ △)]
   ['I '(△ (△ △) (△ △))]])

(def SKI->tree
  (partial
   replace*
   {'S '(△ (△ (△ △ (△ (△ △) (△ △ △)))) (△ (△ (△ △)) (△ △ (△ (△ △) (△ △ △)))))
    'K '(△ △)
    'I '(△ (△ △) (△ △))}))
