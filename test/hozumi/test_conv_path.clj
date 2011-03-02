(ns hozumi.test-conv-path
  (:use [hozumi.conv-path] :reload)
  (:use [clojure.test]))

(deftest test-path->relative
  (is "../../Documents" (path->relative "/Users/fatrow/Documents"))
  (is "../../Documents" (path->relative "/Users/fatrow/Documents" "."))
  (is "../Documents" (path->relative "/Users/fatrow/Documents" "..")))

(deftest test-absolute-url
  (is (= "http://example.com/" (absolute-url "http://example.com/" "http://example.com/")))
  (is (= "https://example.com/" (absolute-url "https://example.com/" "https://example.com/")))

  (is (= "http://example.com/a" (absolute-url "/a" "http://example.com/")))
  (is (= "http://example.com:8080/a" (absolute-url "/a" "http://example.com:8080/")))
  (is (= "http://example.com/a" (absolute-url "a" "http://example.com/")))
  (is (= "http://example.com/a" (absolute-url "./a" "http://example.com/")))
  (is (= "http://example.com/a/b" (absolute-url "/a/b" "http://example.com/")))
  (is (= "http://example.com/a/b" (absolute-url "a/b" "http://example.com/")))
  (is (= "http://example.com/a/b" (absolute-url "./a/b" "http://example.com/")))

  (is (= "http://example.com/a" (absolute-url "/a" "http://example.com/index")))
  (is (= "http://example.com/a" (absolute-url "a" "http://example.com/index")))
  (is (= "http://example.com/a" (absolute-url "./a" "http://example.com/index")))
  (is (= "http://example.com/a/b" (absolute-url "/a/b" "http://example.com/index")))
  (is (= "http://example.com/a/b" (absolute-url "a/b" "http://example.com/index")))
  (is (= "http://example.com/a/b" (absolute-url "./a/b" "http://example.com/index")))

  (is (= "http://example.com/" (absolute-url "../" "http://example.com/a/")))
  (is (= "http://example.com/" (absolute-url "../" "http://example.com/a/b")))
  (is (= "http://example.com/a/" (absolute-url "../" "http://example.com/a/b/")))
  (is (= "http://example.com/c" (absolute-url "../c" "http://example.com/a/b")))
  (is (= "http://example.com/a/c" (absolute-url "../c" "http://example.com/a/b/")))
  (is (= "http://example.com/x" (absolute-url "../../x" "http://example.com/a/b")))
  (is (= "http://example.com/x/y" (absolute-url "../../x/y" "http://example.com/a/b"))))
