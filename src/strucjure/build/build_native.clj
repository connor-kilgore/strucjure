(ns strucjure.build.build-native
  (:require [clojure.tools.build.api :as b]
            [strucjure.build.options :as opts]))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path ".cpcache"}))

(defn uber [main-ns]
  (require main-ns)
  (when-not (find-ns main-ns)
    (throw (ex-info (str "Namespace " main-ns " not found") {})))

  (println "Creating uber jar...")

  (b/compile-clj {:basis      @basis
                  :ns-compile [main-ns]
                  :class-dir  opts/class-dir})
  (b/uber {:class-dir opts/class-dir
           :uber-file @opts/uber-file
           :basis     @basis
           :main      main-ns})

  (println (str "Uber jar '" @opts/uber-file "' created.")))

(defn build [args]
  (opts/set-options! args)
  (clean nil)
  (uber @opts/main-ns)
  )