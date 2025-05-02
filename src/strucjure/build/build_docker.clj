(ns strucjure.build.build-docker
  (:require [clojure.tools.build.api :as b]))

(defn write-dockerfile! [target]
  (spit "Dockerfile.aarch64"
    (apply str
      (interpose "\n"
        ["FROM arm64v8/debian:bullseye"
         "WORKDIR /app"
         "COPY src /app/src"
         "COPY deps.edn /app/"
         "RUN apt-get update && apt-get install -y curl zip unzip git build-essential libz-dev zlib1g-dev gnupg ca-certificates"
         "ENV SDKMAN_DIR=\"/root/.sdkman\""
         "ENV PATH=\"${SDKMAN_DIR}/candidates/java/current/bin:$PATH\""
         "RUN curl -s \"https://get.sdkman.io\" | bash && \\"
         "bash -c \"source $SDKMAN_DIR/bin/sdkman-init.sh && sdk install java 24-graal\""
         "RUN curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh && \\"
         "chmod +x linux-install.sh && \\"
         "./linux-install.sh && \\"
         "rm linux-install.sh"
         "CMD clojure -M:build-native && \\"
         (str "mv " target " /app/target/")]))))

(defn docker [target]
  (let [cwd (System/getProperty "user.dir")]
    (write-dockerfile! target)
    ; TODO: parameterize the Dockerfile to choose which platform to build native image on
    (println "Building Docker image")
    (b/process {:command-args
                ["docker" "build"
                 "-f" "Dockerfile.aarch64"
                 "-t" "strucjure-builder" "."]})
    (println "Running Docker container")
    (b/process {:command-args
                ["docker" "run"
                 "-v" (str cwd "/target:/app/target")
                 "strucjure-builder"]})))

(defn -main [target]
  (docker target))