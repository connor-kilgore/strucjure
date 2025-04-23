(ns strucjure.build.options)

(def class-dir "target/classes")
(def target (atom "target/output"))
;(def uber-file (atom "target/output.jar"))
(def main-ns (atom nil))

(defn set-uber! [args]
  (cond
    (some? (get args '-t)) (reset! target (str (get args '-t)))
    (some? (get args '--target)) (reset! target (str (get args '--target)))))

(defn -get-ns-sym [args]
  (cond
    (some? (get args '-ns)) (symbol (get args '-ns))
    (some? (get args '--namespace)) (symbol (get args '--namespace))
    :else (throw (Exception. "No main namespace provided"))))

(defn set-main-ns! [args]
  (let [ns-sym (-get-ns-sym args)]
    (reset! main-ns ns-sym)))

(defn set-options! [args]
  (set-uber! args)
  (set-main-ns! args))