;
; Copyright © 2013 Peter Monks (pmonks@gmail.com)
;
; This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
; International License. To view a copy of this license, visit
; http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative
; Commons, PO Box 1866, Mountain View, CA 94042, USA.
;

(ns lolcode.test-helper
  (:use clojure.pprint
        clojure.java.io
        lolcode.parser)
  (:require [instaparse.core :as insta]))

(defn print-ast
  ([source]      (print-ast source :Program))
  ([source rule] (pprint (raw-parses source rule))))

(defn can-parse?
  ([source]                    (can-parse? source :Program))
  ([source rule]               (can-parse? source rule false))
  ([source rule print-failure]
    (let [asts   (raw-parses source rule)
          result (not (insta/failure? asts))]
      (if (and print-failure (not result))
        (do
          (pprint "Parse failure:")
          (pprint (insta/get-failure asts))))
      result)))

(defn can-parse-file?
  ([file]      (can-parse-file? file :Program))
  ([file rule] (can-parse-file? file rule false))
  ([file rule print-failure]
    (let [source         (slurp file)
          parsed         (can-parse? source rule print-failure)
          number-of-asts (number-of-asts source rule)]

      (if (and print-failure (> number-of-asts 1))
        (println (str "Multiple ASTs (" number-of-asts ")!")))
      (and parsed (= number-of-asts 1))
    )))
