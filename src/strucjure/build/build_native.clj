(ns strucjure.build.build-native
  (:require [clojure.tools.build.api :as b]
            [strucjure.build.options :as opts]
            [clojure.java.shell :as sh]))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path ".cpcache"}))

(defn uber [main-ns]
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir opts/class-dir})

  (println "Creating uber jar...")

  (b/compile-clj {:basis      @basis
                  :ns-compile [main-ns]
                  :class-dir  opts/class-dir})
  (b/uber {:class-dir opts/class-dir
           :uber-file (str @opts/target ".jar")
           :basis     @basis
           :main      main-ns})

  (println (str "Uber jar '" @opts/target ".jar' created.")))

(defn native []
  ; TODO: babashka
  (println (str "Building native executable"))
  (sh/sh "native-image" "--report-unsupported-elements-at-runtime"
         "--initialize-at-build-time" "--no-server" "-jar"
         (str @opts/target ".jar")
         (str "-H:Name=" @opts/target)))

(defn build [args]
  (opts/set-options! args)
  (clean nil)
  (uber @opts/main-ns)
  (native))