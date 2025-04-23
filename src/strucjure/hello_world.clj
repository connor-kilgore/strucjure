(ns strucjure.hello-world
  (:gen-class)
  (:require [c3kit.apron.log :as log]))

(defn -main [& args]
  ;(println "Hello World!")
  (log/info "Hello World!")
  )