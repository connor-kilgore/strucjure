#!/bin/bash
set -e

# Build native image
clojure -T:build-native build -t $BUILD_TARGET -ns $MAIN_NS

mv $BUILD_TARGET /app/output