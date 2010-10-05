# clj-conv-path

clj-conv-path is a Clojure utility which converts absolute path into relative path.

## Usage

    (use 'hozumi.conv-path)

    (path->relative "/Users/fatrow/Documents")
    => "../../Documents"

    ;;You can specify base directory.	
    (path->relative "/Users/fatrow/Documents" ".")
    => "../../Documents"
    (path->relative "/Users/fatrow/Documents" "..")
    => "../Documents"
    

## Installation
Leiningen
    [org.clojars.hozumi/clj-conv-path "1.0.0-SNAPSHOT"]
