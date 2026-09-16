# Unit 1 — Getting the game running

**Course:** Computer Science I · **Time:** 2–3 class periods
**TEKS strands:** Technology operations and concepts (compile and execute a program,
use an editor); Critical thinking (the problem-solving process); Digital citizenship
(intellectual property).

## Objectives

Students will be able to:
1. Compile and run a Java program from the command line.
2. Explain the difference between source code (`.java`) and compiled classes
   (`.class`), and what `javac` and `java` each do.
3. Navigate a multi-file project and predict where a change belongs.
4. Change a program's behaviour by editing a data file, and verify the change.

## Hook (15 minutes)

Play the game on the projector. Get into a battle, catch something, lose on purpose
so they see the blackout. Then say: *"Every word you just saw is in a file on this
computer. By Friday you will have changed some of them."*

## Do it yourself (25 minutes)

Have students run the game — but **once, do it the long way**, so the scripts stop
being magic:

```bash
javac -d out $(find src -name '*.java')
java -cp out pokemon.Main
```

Then show that `./run.sh` does exactly those two lines. Talk about what `out/`
contains now (`ls out/pokemon/model/`) — those `.class` files are what actually runs.

## Read

- `src/pokemon/Main.java` — the whole file. It is short on purpose.
- `README.md` — the project structure section.

Ask: *why is `main` so short?* (Because it delegates. A good `main` reads like a
table of contents.)

## Lab: change the game without writing Java (30 minutes)

1. Open `data/species.csv`. Find the `magikarp` row.
2. Change its `hp` from `20` to `200`. Save.
3. `./run.sh --seed 5`, travel to Route 2, and find one. It is now a wall.
4. Put it back to 20.
5. Now break it on purpose: change `hp` to `twenty`. Run again and **read the error
   message out loud**:
   `data/species.csv line 55: column 'hp' should be a whole number but was "twenty"`
6. Fix it. Discuss: why is that message better than a crash?

## Exercises

None in the workbook yet — but run `./exercises.sh` so students see the 18 `TODO`s
that are coming. Tell them this is the map for the semester.

## Errors to expect

| Error | Cause |
|---|---|
| `javac: command not found` | A Java runtime is installed, not a JDK |
| `Could not find or load main class` | Ran from the wrong folder, or forgot `-cp out` |
| `Could not find the data folder` | Not in the project root |

## Digital citizenship (10 minutes)

Read the trademark note at the bottom of `README.md` together. Why can we use these
names in this classroom but not publish the game? What would we have to change? (This
comes back in Unit 18, when they actually do it.)

## Exit ticket

1. What does `javac` do? What does `java` do?
2. You changed a creature's HP without opening a single `.java` file. How?
3. Name one file you would open to change what the *player sees* on screen, and one
   you would open to change *how much damage a move does*.
