(ns strucjure.build
  (:require [clojure.java.io :as io]
            [clojure.tools.build.api :as b]))


;(def jarfile (str "simple-player.jar"))
;(def basis (b/create-basis {:project "deps.edn"}))
;(def src-dirs ["src/clj" "resources"])
;(def java-dirs ["src/java"])
;(def class-dir "target/classes")

;(defn clean [_]
;  (b/delete {:path "target"})
;  (b/delete {:path ".cpcache"}))
;
;(defn- build-pom [dir]
;  (b/write-pom {:target   dir
;                :lib      'droneup/themis
;                :version  "1.0"
;                :basis    (b/create-basis {:project "deps.edn" :aliases [:test]})
;                :src-dirs src-dirs}))
;
;(defn pom [_] (build-pom "."))
;
;(defn fail! [reason]
;  (if (instance? Exception reason)
;    (prn reason)
;    (println reason))
;  (System/exit -1))
;
;(defn verify-pom [_]
;  (try
;    (when-not (.exists (io/file "pom.xml"))
;      (fail! "pom.xml is missing"))
;    (build-pom "tmp")
;    (when-not (= (slurp "pom.xml") (slurp "tmp/pom.xml"))
;      (fail! "pom.xml is not current with deps.edn"))
;    (println "pom.xml is up to date")
;    (catch Exception e
;      (fail! e))))

(defn javac [_]
  (println "Compiling Java code...")
  (b/aot {:namespace 'clojure.pprint
          :class-dir "target/classes"
          :basis basis})
  (b/javac {:src-dirs   java-dirs
            :class-dir  class-dir
            :basis      basis
            :javac-opts ["--release" "17"]}))

;(defn uber [_]
;  (println "Building Uber Jar: " jarfile)
;  (b/copy-dir {:src-dirs   src-dirs
;               :target-dir class-dir})
;  (javac _)
;  (b/uber {:class-dir class-dir
;           :uber-file jarfile
;           :basis     basis
;           :exclude   ["META-INF/license/LICENSE.*.txt"]
;           :main      'simple-player.DesktopMain})
;  (println "uber jar created: " jarfile))

;(defn package [_]
;  (clean nil)
;  (uber nil))