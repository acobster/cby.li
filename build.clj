;; Build with:
;; clojure -T:build uberjar
(ns build
  (:require
    [clojure.tools.build.api :as build]))

(defn clean [_]
  (println "Deleting ./target...")
  (build/delete {:path "target"}))

(defn uberjar [_]
  (clean nil)
  (let [class-dir "target/classes"
        version (format "0.1.%s" (build/git-count-revs nil))
        jar-file (format "target/cby.li-%s.jar" version)
        basis (build/create-basis {:project "deps.edn"})]
    (build/write-pom {:class-dir class-dir
                      :lib 'acobster/cby.li
                      :version version
                      :basis basis
                      :src-dirs ["src"]})
    (build/copy-dir {:src-dirs ["src" "resources"]
                     :target-dir class-dir})
    (build/compile-clj {:basis basis
                        :src-dirc ["src"]
                        :class-dir class-dir})
    (build/uber {:class-dir class-dir
                 :uber-file jar-file
                 :basis basis
                 :main 'li.cby.app})
    (println "Created" jar-file)))
