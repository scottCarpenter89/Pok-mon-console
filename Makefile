# Plain make targets, for teachers who prefer them to the shell scripts.
#   make        - compile everything
#   make play   - start the game
#   make test   - run the test suite
#   make work   - mark the exercise workbook
#   make clean  - delete the compiled classes

JAVAC = javac
JAVA  = java
SRC   = $(shell find src -name '*.java')
TESTS = $(shell find test -name '*.java')

all: out/.built test-out/.built

out/.built: $(SRC)
	@mkdir -p out
	$(JAVAC) -d out $(SRC)
	@touch out/.built

test-out/.built: out/.built $(TESTS)
	@mkdir -p test-out
	$(JAVAC) -cp out -d test-out $(TESTS)
	@touch test-out/.built

play: out/.built
	$(JAVA) -cp out pokemon.Main

test: all
	$(JAVA) -cp out:test-out pokemon.TestRunner

work: out/.built
	$(JAVA) -cp out pokemon.exercises.ExerciseRunner

clean:
	rm -rf out test-out savegame.txt

.PHONY: all play test work clean
