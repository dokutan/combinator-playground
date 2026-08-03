(ns combinator-playground.interpreter 
  (:require
    [clojure.string :as str]
    [combinator-playground.combinators :refer [all-combinators]]
    [combinator-playground.reduce :refer [reduce-last]]
    [combinator-playground.utils :refer [fixpoint replace*]]))

;;; A primitive interpreter built on top of `reduce*`.
;;; Reads from *in*, writes to *out*, use `with-in-str`/`with-out-str` to use strings instead.
;;; Each line read gets reduced and printed.
;;;
;;; The following commands are available:
;;; #env              show the interpreter environment
;;; #q                quit the interpreter
;;; #limit num        set the reduction limit to num
;;; #limit            disable the reduction limit
;;; #def name expr    define a variable with name and value expr


(defn interpreter
  "Start an (interactive) interpreter, reads from *in*, writes to *out*."
  ([]
   (interpreter all-combinators {}))
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
           (println "combinators:"     (str/join " " (sort (keys combinators))))
           (println "variables:"       (str/join " " (sort (keys variables))))
           (recur combinators variables limit))

         ;; quit interpreter
         (str/starts-with? line "#q")
         nil

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
         (recur
          combinators
          (let [[_ a b] (str/split line #" +" 3)]
            (into variables {(symbol a) (read-string (str "(" b ")"))}))
          limit)

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
