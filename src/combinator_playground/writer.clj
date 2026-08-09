(ns combinator-playground.writer)

;;; A minimal implementation of the writer monad.
;;; Usable with all functions that return a vector of intermediate results (e.g. `reduce*`).
;;; The monadic value is a vector, where the last element is the actual value.

(defn bind
  "Apply a function `f` that returns a vector of intermediate results to `m` and `args`."
  [m f & args]
  (vec (concat m (apply f (last m) args))))

(defn fmap
  "Apply a function `f` that returns a single result to `m` and `args`."
  [m f & args]
  (conj m (apply f (cons (last m) args))))
