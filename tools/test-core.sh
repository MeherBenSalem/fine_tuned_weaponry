#!/usr/bin/env bash
# Compiles core with Java 17 semantics and runs the dependency-free core test suite.
# Usage (from anywhere): tools/test-core.sh   Needs a JDK 17+ on PATH or in JAVA_HOME.
set -euo pipefail
cd "$(dirname "$0")/.."

JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"
GSON_VERSION=2.11.0
GSON="${GSON_JAR:-}"
if [[ -z "$GSON" ]]; then
  GSON=$(find "$HOME/.gradle/caches/modules-2" -name "gson-2.1*.jar" ! -name "*sources*" 2>/dev/null | sort | tail -1 || true)
fi
if [[ -z "$GSON" ]]; then
  GSON="build/test-core/gson-$GSON_VERSION.jar"
  mkdir -p build/test-core
  [[ -f "$GSON" ]] || curl -fsSL -o "$GSON" \
    "https://repo1.maven.org/maven2/com/google/code/gson/gson/$GSON_VERSION/gson-$GSON_VERSION.jar"
fi

OUT=build/test-core/classes
rm -rf "$OUT"
mkdir -p "$OUT"
find core/src -name '*.java' > build/test-core/sources.txt
"${JAVA_BIN}javac" --release 17 -encoding UTF-8 -Xlint:-options -cp "$GSON" -d "$OUT" @build/test-core/sources.txt
"${JAVA_BIN}java" -cp "$OUT:$GSON" io.nightbeam.runeborn.core.CoreTests
