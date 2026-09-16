#!/usr/bin/env bash
# Builds if needed, then starts the game.
#   ./run.sh                 normal game
#   ./run.sh --seed 12345    the same adventure every time (great for demos)
#   ./run.sh --fast          no dramatic pauses
set -e
cd "$(dirname "$0")"
./build.sh > /dev/null
java -cp out pokemon.Main "$@"
