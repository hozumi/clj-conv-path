(ns hozumi.conv-path
  (:require [clojure.java.io :as io :only [file]])
  (:import [java.net URL]))

(defn path->relative
  ([path] (path->relative path "."))
  ([path base]  ;;"/a/b/c/d"  "."
     (let [abs-path   (.getCanonicalPath (io/file path)) ;;"/a/b/c/d" -> "/a/b/c/d" for confirmation
	   base-path  (.getCanonicalPath (io/file base)) ;;"." -> "/a/b/e"
	   abs-parts  (filter #(not= "" %) (.split abs-path "/")) ;;("a" "b" "c" "d")
	   base-parts (filter #(not= "" %) (.split base-path "/"));;("a" "b" "e")
	   base-n     (count base-parts) ;; 3
	   common     (take-while (fn [[a b]] (= a b))   ;; ("a" "b")
				  (map vector abs-parts base-parts))
	   common-n   (count common) ;; 2
	   abs-dif    (drop common-n abs-parts) ;; ("c" "d")
	   climb      (repeat (- base-n common-n) "..") ;; ("..")
	   r-parts    (concat climb abs-dif) ;; (".." "c" "d")
	   relative   (butlast (interleave r-parts (repeat "/")))]
       (apply str relative))))

(defn when-pos [v]
  (when (and v (pos? v)) v))

(defn parse-url [url]
  (let [url-parsed (URL. url)]
    [(.getProtocol url-parsed)
     (.getHost url-parsed)
     (when-pos (.getPort url-parsed))
     (.getPath url-parsed)
     (.getUserInfo url-parsed)
     (.getQuery url-parsed)]))

(defn absolute-https-url? [^String url]
  (.startsWith url "https://"))

(defn absolute-http-url? [^String url]
  (.startsWith url "http://"))

(defn absolute-url? [url]
  (or (absolute-http-url? url)
      (absolute-https-url? url)))

(defn port-with-colon-or-nil [port]
  (when port (str ":" port)))

(defn absolute-url
  ([^String href
    ^String url]
     (let [[scheme domain port uri] (parse-url url)]
       (absolute-url href scheme domain port uri)))
  ([^String href
    ^String scheme
    ^String domain
    ^String port
    ^String current-uri]
     (cond
      (absolute-url? href)
      href
      ;;protocol relative path
      (re-find #"^//[^/]+\.[^/]+" href)
      href

      (.startsWith href "/")
      (str scheme "://" domain (port-with-colon-or-nil port) href)

      (.startsWith href "data:")
      href

      (.startsWith href "./")
      (let [[_ tail] (re-find #"^\./(.*)" href)
            is-current-dir? (.endsWith current-uri "/")]
        (str scheme "://" domain (port-with-colon-or-nil port)
             (if is-current-dir?
               current-uri
               (apply str "/" (interleave
                               (butlast (drop 1 (.split current-uri "/")))
                               (repeat "/"))))
             tail))

      (.startsWith href "..")
      (let [[_ _ tail] (re-find #"^(\.\./)+(.*)" href)
            ;;    "../aaa" => ["../aaa"    "../" "aaa"]
            ;; "../../aaa" => ["../../aaa" "../" "aaa"]

            current-uri* (if (.endsWith current-uri "/")
                           (str current-uri "hohei")
                           current-uri)
            base-url-list (drop 1 (.split current-uri* "/"))

            ;;         "/" => ("hohei")
            ;;      "/aaa" => ("aaa")
            ;;     "/aaa/" => ("aaa" "hohei")
            ;;  "/aaa/bbb" => ("aaa" "bbb")
            ;; "/aaa/bbb/" => ("aaa" "bbb" "hohei")
            up-n (count (re-seq #"\.\./" href))
            ;;    "../" => 1
            ;; "../../" => 2
            target-dir (apply str "/" (interleave
                                       (take (- (count base-url-list) (inc up-n))
                                             base-url-list)
                                       (repeat "/")))]
        ;;     "/aaa/" 1 =>     "/" (0)
        ;; "/aaa/bbb/" 1 => "/aaa/" (1)
        ;;  "/aaa/bbb" 1 =>     "/" (0)
        ;; "/aaa/bbb/" 2 =>     "/" (0)
        (str scheme "://" domain (port-with-colon-or-nil port) target-dir tail))

      (.endsWith current-uri "/")
      (str scheme "://" domain (port-with-colon-or-nil port) current-uri href)

      :else
      (let [target-dir (apply str "/" (interleave
                                       (butlast (drop 1 (.split current-uri "/")))
                                       (repeat "/")))]
        (str scheme "://" domain (port-with-colon-or-nil port) target-dir href)))))
