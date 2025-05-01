(ns strucjure.build.build-native
  (:require [clojure.tools.build.api :as b]
            [strucjure.build.options :as opts]
            [clojure.java.shell :as sh]))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path ".cpcache"}))

(defn aot-compile [main-ns]
  (b/copy-dir {:src-dirs   ["src" "resources"]
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
  (b/process
    {:command-args
     ["native-image"
      "--verbose"
      "-march=compatibility"
      "--initialize-at-build-time"
      "--no-server"
      "--initialize-at-run-time=io.helins.linux.io.LinuxIO"
      ;"-H:+UnlockExperimentalVMOptions"
      "--jni"
      "-jar" (str @opts/target ".jar")
      (str "-H:Name=" @opts/target)]
     :inherit true}))

(defn write-dockerfile! [_]
  (spit "Dockerfile.aarch64"
    (apply str
      (interpose "\n"
        ["FROM arm64v8/debian:bullseye"
         "WORKDIR /app"
         "COPY src /app/src"
         "COPY deps.edn /app/"
         "RUN apt-get update && apt-get install -y curl zip unzip git build-essential libz-dev zlib1g-dev gnupg ca-certificates"
         "ARG BUILD_TARGET=output"
         "ENV BUILD_TARGET=$BUILD_TARGET"
         "ARG MAIN_NS=main"
         "ENV MAIN_NS=$MAIN_NS"
         "ENV SDKMAN_DIR=\"/root/.sdkman\""
         "ENV PATH=\"${SDKMAN_DIR}/candidates/java/current/bin:$PATH\""
         "RUN curl -s \"https://get.sdkman.io\" | bash && \\"
         "bash -c \"source $SDKMAN_DIR/bin/sdkman-init.sh && sdk install java 24-graal\""
         "RUN curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh && \\"
         "chmod +x linux-install.sh && \\"
         "./linux-install.sh && \\"
         "rm linux-install.sh"
         "CMD clojure -T:build-native build -t $BUILD_TARGET -ns $MAIN_NS && \\"
         "mv $BUILD_TARGET /app/output/"]))))

(defn docker []
  (let [cwd (System/getProperty "user.dir")]
    (write-dockerfile! nil)
    ; TODO: parameterize the Dockerfile to choose which platform to build native image on
    (println "Building Docker image")
    (b/process {:command-args
                ["docker" "build"
                 "--build-arg" (str "BUILD_TARGET=" @opts/target)
                 "--build-arg" (str "MAIN_NS=" @opts/main-ns)
                 "-f" "Dockerfile.aarch64"
                 "-t" "strucjure-builder" "."]})
    (println "Running Docker container")
    (b/process {:command-args
                ["docker" "run"
                 "-v" (str cwd "/output:/app/output")
                 "strucjure-builder"]}))
  )

(defn build [args]
  (opts/set-options! args false)
  (clean nil)
  (aot-compile @opts/main-ns)
  (native))

(defn build-aarch64 [args]
  (opts/set-options! args true)
  (clean nil)
  (aot-compile @opts/main-ns)
  (docker))