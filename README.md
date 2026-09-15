# Pokémon Console RPG — a Java course in a game

A complete, playable, turn-based RPG for the terminal, written in plain Java 17+
with **no libraries, no build tools and no downloads** — and designed from the first
line to be *read* by high school students learning computer science.

The game is the textbook. Every concept in a Computer Science I–III scope and
sequence appears here doing real work: the type chart is a 2D array, the battle is a
state machine, the trainers are an inheritance hierarchy, the opponents are a
Strategy pattern, the map is a graph, and the save file is serialization.

```
$ ./run.sh

================================================================
                             BATTLE
================================================================
  A wild PIDGEY appeared! (Lv.3)
  Go! BULBASAUR!

+--------------------------------------------------------------+
| FOE  Pidgey       Lv.3   [##########] 15/15   Normal/Flying   |
|                                                              |
| YOU  Bulbasaur    Lv.5   [########..] 16/20   Grass/Poison    |
+--------------------------------------------------------------+

What will BULBASAUR do?
  1) FIGHT      2) BAG      3) POKEMON      4) RUN
```

---

## Quick start

You need a JDK (17 or newer; developed against 21). Nothing else.

| | Mac / Linux | Windows |
|---|---|---|
| Play the game | `./run.sh` | `run.bat` |
| Run the tests | `./test.sh` | `test.bat` |
| Mark the exercise workbook | `./exercises.sh` | `exercises.bat` |

Prefer `make`? `make play`, `make test`, `make work`, `make clean`.

Useful flags for the classroom: `./run.sh --seed 12345` replays the exact same
"random" adventure every time (perfect for a demo you have rehearsed), and
`./run.sh --fast` removes the dramatic pauses.

---

## What is in the box

| | |
|---|---|
| **A real game** | Catching, evolution, 18 types, status conditions, stat stages, critical hits, trainers, a shop, Pokémon Centers, a Pokédex, saving and loading. |
| **10 in-game CS lessons** | The "Trainer School" menu runs interactive lessons on variables, conditionals, loops, arrays, methods, objects, inheritance, recursion, searching/sorting and data structures — each one demonstrated with the game's live data. |
| **18 self-marking exercises** | Students fill in `TODO`s and run `./exercises.sh`, which tells them exactly which check failed and gives a hint. No marking time for you. |
| **177 unit tests** | Written with a 60-line test framework students can read, including tests that check the **data files** so a typo in a spreadsheet is caught immediately. |
| **18 lesson plans** | `lessons/unit01` … `unit18`, each with objectives, a demo to run, the files to read, exercises and an exit ticket. |
| **A TEKS alignment table** | `docs/TEKS-ALIGNMENT.md` maps every unit to Computer Science I, II and III. |

---

## The shape of the project

```
data/            The whole game as CSV spreadsheets - no content is hard-coded
  species.csv      55 species with base stats, types and evolutions
  moves.csv        76 moves with power, accuracy, PP and effects
  learnsets.csv    which species learns what, and when
  items.csv  areas.csv  encounters.csv  shops.csv  trainers.csv

src/pokemon/
  Main.java        where the program starts
  Game.java        the overworld: menus, travel, shops, saving
  model/           Pokemon, Species, Move, Item, Bag, Trainer, Player, Type
  battle/          the battle engine, damage/catch formulas, three AI classes
  world/           areas, wild encounters, the map as a graph
  data/            CSV loading, the game registry, save files
  ui/              every println and every keyboard read, in one place
  school/          the ten in-game computer science lessons
  exercises/       the student workbook (TODOs) and its marking runner
  util/            random-number abstraction and string helpers

test/pokemon/    MiniTest framework + 177 checks
solutions/       the completed workbook (teacher's copy)
lessons/         18 units of lesson plans
docs/            TEKS alignment, teacher guide, student handbook
```

---

## The three-course path through the code

**Computer Science I — the first semester.** Students play the game before they read
any of it. Then they read `Type.java` (enums, a 2D array), `DamageCalculator.java`
(one formula, one method, pure arithmetic) and `Pokemon.java` (fields, constructors,
`if`, `while`). They finish by adding a species to `species.csv` and battling it —
which is a real change to a real program on week two.

**Computer Science II — objects in earnest.** `Trainer` → `Player` / `NpcTrainer` is
inheritance with a purpose. `BattleAI` is an interface with three implementations
that visibly change how the game plays. `DataLoader` is file I/O and exception
handling. `Species.getEvolvesInto()` makes recursion obvious. Students write their
own AI class and drop it into `data/trainers.csv`.

**Computer Science III — thinking like an engineer.** `WorldMap.shortestPath` is a
breadth-first search on data students can see. `Bag` is a Map; the Pokémon Center
queue and the undo stack are in the data-structures lesson. `MiniTest` shows what a
testing framework actually is. The capstone is a feature of their own design, with
tests, delivered against a deadline.

`docs/TEACHER-GUIDE.md` has the pacing, the assessments and the "what will go wrong"
notes. `docs/TEKS-ALIGNMENT.md` has the standards mapping.

---

## Design decisions made for teaching, not for elegance

These are deliberate, and they are worth explaining to students at some point:

- **No frameworks.** The project must run on a locked-down lab machine with nothing
  but a JDK. It also means every line on screen is a line a student could have
  written.
- **The data is in CSV files, not in Java.** A student can change the game on day one
  without understanding the engine, and a teacher can retheme the whole thing with a
  spreadsheet.
- **Randomness goes through an interface.** `RandomSource` lets tests replace chance
  with certainty. This is the reason the damage formula is testable at all.
- **The UI is quarantined.** Nothing in `model/`, `battle/` or `world/` prints
  anything. That is what makes the test suite possible, and it is the cleanest
  example of separation of concerns in the project.
- **Some comments explain *why*, not *what*.** Comments marked `TEACHING NOTE` are
  written for a student reader and name the concept and the course level.
- **One bug was left in the commit history on purpose.** `Battle.Side.active` carries
  a comment about the NullPointerException that led to it. Real debugging stories
  land better than invented ones.

---

## Making it your own

The fastest classroom win is to retheme it. Every name the player sees comes out of
`data/`, so a class can replace the creatures with school mascots, historical
figures, cell organelles or their own drawings, and the engine does not change by one
character. `lessons/unit18` is built around exactly that project.

To add a new creature: one row in `species.csv`, a few rows in `learnsets.csv`, one
row in `encounters.csv`. Then run `./test.sh` — `DataIntegrityTest` will tell you if
you forgot something.

---

## A note on the Pokémon name

This is an unofficial, non-commercial teaching project with no connection to
Nintendo, Creatures Inc. or GAME FREAK, who own the Pokémon trademarks and
characters. It exists to teach Java in a classroom. If you plan to publish, share
outside your school, or enter it in a competition, retheme the `data/` folder first —
the engine is entirely generic and the swap takes an afternoon (see
`lessons/unit18`).

## Licence

The code is released under the licence in `LICENSE`. The creature names and
characters referenced in `data/` are the property of their respective owners.
