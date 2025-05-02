(ns strucjure.build.build-native
  (:require [clj.native-image :as ni]))

(def pre-args
  ["--initialize-at-build-time"
   "-march=compatibility"])

(defn -main [main-ns target]
  (let [args (concat [main-ns] pre-args [(str "-H:Name=" target)])]
    (apply ni/-main args)))