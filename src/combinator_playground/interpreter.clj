(ns combinator-playground.interpreter 
  (:require
    [clojure.string :as str]
    [combinator-playground.combinators :refer [all-combinators church->int
                                               int->church]]
    [combinator-playground.lambda :refer [lambda->SKI* lambda->SKIBC*]]
    [combinator-playground.reduce :refer [reduce-last]]
    [combinator-playground.tree :refer [tree-node triage-node]]
    [combinator-playground.utils :refer [fixpoint replace*]]))

;;; A primitive interpreter built on top of `reduce*`.
;;; Reads from *in*, writes to *out*, use `with-in-str`/`with-out-str` to use strings instead.
;;; Each line read gets reduced and printed, lines starting with ';' are ignored.
;;;
;;; If started without any arguments, `combinators/all-combinators` and
;;; additional functions from `interpreter-stdlib` are available.
;;;
;;; The following commands are available:
;;; #env              show the interpreter environment
;;; #q                quit the interpreter
;;; #limit num        set the reduction limit to num
;;; #limit            disable the reduction limit
;;; #def name expr    define a variable with name and value expr
;;; #tree             load △/node from tree calculus
;;; #triage           load △/node from triage calculus

(def interpreter-stdlib
  "A map of functions, usable like combinators in the interpreter."
  {'->SKI       (fn [expr] (last (lambda->SKI* expr)))
   '->SKIBC     (fn [expr] (last (lambda->SKIBC* expr)))
   'int->church (fn [expr] (int->church expr))
   'church->int (fn [expr] (church->int (into all-combinators interpreter-stdlib) expr))})

(defn interpreter
  "Start an (interactive) interpreter, reads from *in*, writes to *out*."
  ([]
   (interpreter (into all-combinators interpreter-stdlib) {}))
  ([combinators]
   (interpreter combinators {}))
  ([combinators variables]
   (loop [combinators combinators
          variables variables
          limit nil]
     (when-some [line (some-> (read-line) str/trim)]
       (cond
         ;; show env
         (str/starts-with? line "#env")
         (do
           (println "reduction limit:" limit)
           (println "combinators:" (str/join " " (sort (keys combinators))))
           (println "variables:"       (str/join " " (sort (keys variables))))
           (recur combinators variables limit))

         ;; quit interpreter
         (str/starts-with? line "#q")
         nil

         ;; load tree calculus
         (str/starts-with? line "#tree")
         (recur (into combinators tree-node) variables limit)

         ;; load triage calculus
         (str/starts-with? line "#triage")
         (recur (into combinators triage-node) variables limit)

         ;; set reduction limit
         (str/starts-with? line "#limit")
         (recur
          combinators
          variables
          (if (re-matches #"#limit +[0-9]+" line)
            (parse-long (second (str/split line #" +")))
            nil))

         ;; define a variable
         (re-matches #"^#def +[^ ]+ +[^ ].*" line)
         (let [[_ a b]  (str/split line #" +" 3)
               parsed   (read-string (str "(" b ")"))
               resolved (last (fixpoint (partial replace* variables) parsed))
               reduced  (reduce-last combinators resolved limit)]
           (if (#{"true" "false" "nil"} a) ; conflicts with the clojure values
             (do
               (println "invalid variable name:" a)
               (recur combinators variables limit))
             (recur
              combinators
              (into variables {(symbol a) reduced})
              limit)))

         ;; comment ?
         (or (empty? line)
             (str/starts-with? line ";")
             (str/starts-with? line "#"))
         (recur combinators variables limit)

         :else
         (do
           (let [parsed   (read-string (str "(" line ")"))
                 resolved (last (fixpoint (partial replace* variables) parsed))
                 reduced  (reduce-last combinators resolved limit)]
             (println reduced))
           (recur combinators variables limit)))))))
