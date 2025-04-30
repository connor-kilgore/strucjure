(ns strucjure.build.options)

(def class-dir "target/classes")
(def target (atom "target/output"))
;(def uber-file (atom "target/output.jar"))
(def main-ns (atom nil))

(defn set-uber! [args add-target?]
  (cond
    (some? (get args '-t)) (reset! target (str (when add-target? "target/") (get args '-t)))
    (some? (get args '--target)) (reset! target (str (when add-target? "target/") (get args '--target)))))

(defn -get-ns-sym [args]
  (cond
    (some? (get args '-ns)) (symbol (get args '-ns))
    (some? (get args '--namespace)) (symbol (get args '--namespace))
    :else (throw (Exception. "No main namespace provided"))))

(defn set-main-ns! [args]
  (let [ns-sym (-get-ns-sym args)]
    (reset! main-ns ns-sym)))

(defn set-options! [args set-target?]
  (set-uber! args set-target?)
  (set-main-ns! args))