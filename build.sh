#!/usr/bin/env bash
# Compiles the game and the tests. No Maven, no Gradle, no downloads -
# a school computer with a JDK installed is all you need.
set -e
cd "$(dirname "$0")"

echo "Compiling the game..."
mkdir -p out
find src -name '*.java' > .sources.txt
javac -d out @.sources.txt
rm -f .sources.txt

echo "Compiling the tests..."
mkdir -p test-out
find test -name '*.java' > .testsources.txt
javac -cp out -d test-out @.testsources.txt
rm -f .testsources.txt

echo "Build finished. Run ./run.sh to play."
