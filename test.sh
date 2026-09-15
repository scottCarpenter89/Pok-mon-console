#!/usr/bin/env bash
# Runs the whole test suite. Exit code 0 means everything passed.
set -e
cd "$(dirname "$0")"
./build.sh > /dev/null
java -cp out:test-out pokemon.TestRunner
