(ns hozumi.conv-path
  (:require [clojure.java.io :as io :only [file]]))

(defn abs->relative
  ([abs-path] (abs->relative abs-path "."))
  ([abs-path base]  ;;"/a/b/c/d"  "."
     (let [base-path  (.getCanonicalPath (io/file base)) ;;"." -> "/a/b/e"
	   abs-parts  (filter #(not= "" %) (.split abs-path "/")) ;;("a" "b" "c" "d")
	   base-parts (filter #(not= "" %) (.split base-path "/"));;("a" "b" "e")
	   base-n  (count base-parts) ;; 3
	   common (take-while (fn [[a b]] (= a b))   ;; ("a" "b")
			      (map vector abs-parts base-parts))
	   common-n (count common) ;; 2
	   abs-dif  (drop common-n abs-parts) ;; ("c" "d")
	   climb  (repeat (- base-n common-n) "..") ;; ("..")
	   r-parts  (concat climb abs-dif) ;; (".." "c" "d")
	   relative (butlast (interleave r-parts (repeat "/")))]
       (apply str relative))))
