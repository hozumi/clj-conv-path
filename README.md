# clj-conv-path

clj-conv-path is a Clojure utility that converts absolute path into relative path.

## Usage

    (use 'hozumi.conv-path)

    (abs->relative "/Users/fatrow/Documents")
    => "../../Documents"

    ;;You can specify base directory.	
    (abs->relative "/Users/fatrow/Documents" ".")
    => "../../Documents"
    (abs->relative "/Users/fatrow/Documents" "..")
    => "../Documents"
    

## Installation
Leiningen
    [org.clojars.hozumi/clj-conv-path "1.0.0-SNAPSHOT"]
