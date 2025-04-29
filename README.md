# Strucjure

Strucjure is a library meant to be used for embedded Clojure projects. Some important namespaces include:
```
strucjure.build.build-native
```

## Requirements
In order to fully use **Strucjure** the following requirements are necessary
1. [Clojure](https://clojure.org/guides/install_clojure)
2. [Docker](https://docs.docker.com/engine/install/)
3. [GraalVM](https://www.graalvm.org/downloads/)

## Building native images
First create an alias in any Clojure deps project for building. Until a release is made, be sure to use the most recent git sha for strucjure.
```clojure
:build-native
{:exec-fn     strucjure.build.build-native/build
 :ns-default  strucjure.build.build-native
 :extra-deps  {strucjure/strucjure           {:git/url "https://github.com/connor-kilgore/strucjure.git"
                                              :sha     "0eeeea6ecc05c636ecac95e2b15edda519cc9842"}
               io.github.clojure/tools.build {:git/tag "v0.10.3" :git/sha "15ead66"}}
 :extra-paths ["src"]}
```

To utilize the alias, the target for the executable, and the main namespace must be provided:
```
clj -T:build-native -t target/hello-world -ns strucjure.hello-world
```