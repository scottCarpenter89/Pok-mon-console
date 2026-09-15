#!/usr/bin/env bash
# TEACHER TOOL. Builds the project with solutions/ in place of the student stubs
# and marks it, without touching anything in src/. Use it to confirm the answer
# key still passes after you edit an exercise.
set -e
cd "$(dirname "$0")"

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

cp -r src "$WORK/src"
cp solutions/pokemon/exercises/*.java "$WORK/src/pokemon/exercises/"
find "$WORK/src" -name '*.java' > "$WORK/sources.txt"
javac -d "$WORK/out" @"$WORK/sources.txt"

echo "Marking the answer key (nothing in src/ was changed):"
java -cp "$WORK/out" pokemon.exercises.ExerciseRunner
