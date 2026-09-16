#!/usr/bin/env bash
# Marks your work in src/pokemon/exercises/Exercises.java and MyFirstAI.java.
set -e
cd "$(dirname "$0")"
./build.sh > /dev/null
java -cp out pokemon.exercises.ExerciseRunner
