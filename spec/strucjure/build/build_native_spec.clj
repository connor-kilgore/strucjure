(ns strucjure.build.build-native-spec
  (:require [speclj.core :refer :all]
            [clojure.tools.build.api :as b]
            [strucjure.build.build-native :as sut]
            [strucjure.build.options :as opts]))

(defmacro with-out-str-around []
  `(around [it#] (with-out-str (it#))))

(describe "Build Native"
  (with-stubs)
  (with-out-str-around)

  (it "cleans output"
    (with-redefs [b/delete (stub :delete)]
      (sut/clean nil)
      (should-have-invoked :delete {:with [{:path "target"}]})
      (should-have-invoked :delete {:with [{:path ".cpcache"}]})))

  (context "compile classes"
    (redefs-around [b/compile-clj (stub :compile-clj)
                    b/create-basis (stub :create-basis {:return "deps.edn"})
                    b/uber (stub :uber)
                    require (stub :require)
                    sut/clean (stub :clean)])
    (it "converts string to a namespace"
      (sut/uber 'strucjure.build.build-native-spec)
      (should-have-invoked :require {:with ['strucjure.build.build-native-spec]}))

    (it "throws an exception if unknown namespace"
      (should-throw (sut/uber 'blah.main)))

    (it "with ns"
      (sut/uber 'strucjure.build.build-native-spec)
      (should-have-invoked :compile-clj {:with [{:basis      @sut/basis
                                                 :ns-compile ['strucjure.build.build-native-spec]
                                                 :class-dir  opts/class-dir}]})
      (should-have-invoked :uber {:with [{:class-dir opts/class-dir
                                          :uber-file (str @opts/target ".jar")
                                          :basis @sut/basis
                                          :main 'strucjure.build.build-native-spec}]})))
  )