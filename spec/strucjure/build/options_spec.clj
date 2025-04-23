(ns strucjure.build.options-spec
  (:require [speclj.core :refer :all]
            [strucjure.build.options :as sut]))

(describe "Options"

  (context "set-uber"
    (before
      (reset! sut/target "target/output"))

    (it "with -t"
      (sut/set-uber! {'-t "my-uber"})
      (should= "my-uber" @sut/target))

    (it "with --target"
      (sut/set-uber! {'--target "target/my-uber"})
      (should= "target/my-uber" @sut/target))

    (it "no supplied args"
      (sut/set-uber! {'-blah "target"})
      (should= "target/output" @sut/target)))

  (context "set-main-ns"
    (before
      (reset! sut/main-ns nil))

    (it "with -ns"
      (sut/set-main-ns! {'-ns "blah.core"})
      (should= 'blah.core @sut/main-ns))

    (it "with --namespace"
      (sut/set-main-ns! {'--namespace "blah.core"})
      (should= 'blah.core @sut/main-ns))

    (it "no supplied args"
      (should-throw (sut/set-main-ns! {'-blah "blah.core"})))
    )

  )
