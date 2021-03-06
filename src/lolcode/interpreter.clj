;
; Copyright © 2013 Peter Monks (pmonks@gmail.com)
;
; This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
; International License. To view a copy of this license, visit
; http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative
; Commons, PO Box 1866, Mountain View, CA 94042, USA.
;

(ns lolcode.interpreter
  (:require [instaparse.core       :as insta]
            [clojure.tools.logging :as log]
            [lolcode.runtime       :as rt]
            [lolcode.translator    :as lt]))

; The interpreter itself
(defn interpret
  "Evaluates (interprets) a LOLCODE program."
  [source]
  (eval (lt/translate source)))
