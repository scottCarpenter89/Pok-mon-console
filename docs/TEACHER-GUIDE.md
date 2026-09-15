# Teacher's guide

Everything you need to run this project as the spine of a Computer Science I, II or
III course — or to drop single units into a course you already have.

---

## 1. Before day one (30 minutes)

**Check the lab.** On one machine, open a terminal and run:

```bash
java -version     # needs 17 or newer
javac -version    # must exist - a JRE alone is not enough
git clone <your copy of this repo>
cd Pok-mon-console
./test.sh         # should end with "ALL TESTS PASSED"
./run.sh          # play for two minutes
```

If `javac` is missing, students have a Java *runtime* but not a *developer kit*.
Install a JDK (Temurin and Microsoft Build of OpenJDK are both free and quiet
installers for school images).

**Decide how students get the code.** In order of preference:

1. **Git**, if your network allows it — and then Unit 17 teaches version control for
   free.
2. **A shared network folder** students copy from. Works everywhere.
3. **A zip on the LMS.** Fine. Remind them to unzip *before* opening it.

**Play the game yourself for fifteen minutes**, with `./run.sh --seed 1`. You will be
asked about it constantly, and knowing that Brock's Onix is a wall is worth more than
any slide deck.

---

## 2. What each course looks like

Units are in `lessons/`. Each has objectives, a demo, files to read, exercises and an
exit ticket.

### Computer Science I (Units 1–8)

| Weeks | Units | Arc |
|---|---|---|
| 1–2 | 1 | Play it, run it, compile it, change one line of a CSV file |
| 3–4 | 2–3 | Variables, types, integer division, the damage formula by hand |
| 5–6 | 4 | Conditionals and the type chart |
| 7–9 | 5–6 | Loops, then arrays and 2D arrays |
| 10–12 | 7 | Methods: parameters, return values, decomposition |
| 13–16 | 8 | Classes and objects; students write their own class |
| 17–18 | — | Project: design and add a creature line, with a rationale |

### Computer Science II (Units 9–14)

| Weeks | Units | Arc |
|---|---|---|
| 1–3 | 9 | Encapsulation, static vs instance, Javadoc |
| 4–6 | 10 | Inheritance, interfaces, polymorphism — students write an AI |
| 7–8 | 11 | Collections: List, Map, and choosing between them |
| 9–10 | 12 | File I/O and exceptions — the corrupt-CSV lab |
| 11–12 | 13 | Recursion |
| 13–16 | 14 | Searching, sorting, and a first taste of Big-O |

### Computer Science III (Units 15–18)

| Weeks | Units | Arc |
|---|---|---|
| 1–3 | 15 | Stacks, queues, hash maps, graphs, BFS |
| 4–6 | 16 | Design patterns, refactoring, design critique |
| 7–8 | 17 | Testing, debugging, defensive programming, version control |
| 9–14 | 18 | Team capstone: retheme or extend, with the full life cycle |

**Using single units instead?** The three highest-value standalone lessons are
Unit 6 (2D arrays — the type chart sells itself), Unit 10 (interfaces — students
change how the game *plays* by writing one class) and Unit 15 (graphs — BFS on a map
they have walked through).

---

## 3. How to teach from a codebase students did not write

This is the part most teachers have not done before. Three protocols that work:

**Read-aloud with a highlighter.** Project one method. Students read it line by line
and mark: *green* = I know what this does, *yellow* = I can guess, *red* = no idea.
Then you teach only the red. It takes five minutes and it is ruthlessly efficient at
finding the real gap.

**Predict, then run.** Before showing output, ask for a prediction on paper. Use
`--seed` so the "random" result is the one you rehearsed. Students who predict wrong
remember the right answer far longer than students who were just told.

**Break it on purpose.** The best single activity in this project: change one
character, compile, and watch the failure. Examples that fail in an instructive way:

| Change | What happens | The lesson |
|---|---|---|
| In `Pokemon.gainExperience`, `while` → `if` | Big wins only grant one level | `while` vs `if` |
| In `Type.decodeChart`, `defender < count` → `<=` | An index-out-of-bounds exception at start-up | Off-by-one, zero-indexing |
| In `Pokemon.getMaxHp`, delete `+ level + 10` | Everything has tiny HP and one-shot kills | Reading a formula |
| In `Bag.remove`, delete the `held < count` check | Negative item counts | Guard clauses |
| Delete **every** level-1 row for one species in `learnsets.csv` | `./test.sh` fails: that species could enter a battle with no moves | Testing your data |
| In `Battle.Side`, replace the field with `trainer.getActivePokemon()` | The original NullPointerException comes back | Derived vs remembered state |

Have students revert with `git checkout .` (or from their copy) — which is a free
argument for version control.

---

## 4. Assessment

**Daily / formative.** `./exercises.sh` output. It names each exercise, its course
level and its concept. A screenshot at the end of a period is a complete record of
what a student got working.

**Unit quizzes.** Each `lessons/unitNN/README.md` ends with an exit ticket you can
use as a bell-ringer the next day.

**Practical assessments that are hard to fake** — these are the ones worth the
gradebook space:

| Task | Course | What it proves |
|---|---|---|
| Add a creature line (3 stages) with balanced stats and a learnset | CS I | Data literacy, following a spec, testing |
| Add a new move effect (e.g. FLINCH or a two-turn move) | CS II | Reading an enum, editing a switch, testing an effect |
| Write a new `BattleAI` and give it to a trainer | CS II | Interfaces and polymorphism, genuinely |
| Add a route with encounters, a trainer and a shop | CS II | Data modelling across four files |
| Add a feature with unit tests that pass | CS III | The whole life cycle |
| Retheme the game and present it | CS III | Design, teamwork, communication |

**Capstone rubric (Unit 18)** — out of 100:

| Criterion | Points | Looking for |
|---|---|---|
| It works | 25 | Compiles, runs, does what the design document promised |
| Tests | 20 | New tests exist, pass, and actually test the new behaviour |
| Design | 20 | Change is in the right layer; no game rules in `ui/`; no `println` in `model/` |
| Code quality | 15 | Naming, decomposition, Javadoc on public methods |
| Process | 10 | Design document, milestones met, commits (if using Git) |
| Presentation | 10 | Five-minute demo; explains one design decision and one bug they fixed |

**A note on `solutions/`.** It is a real answer key. Decide your policy on day one
and say it out loud. What works well: tell students the key exists, that you will
share it after a unit closes, and that the exercise runner already gives hints — so
copying costs them the hint loop and gains them nothing on the practical
assessments, which are not in the key.

---

## 5. What will go wrong (and what to say)

| Symptom | Cause | Fix |
|---|---|---|
| `javac: command not found` | JRE installed, not a JDK | Install a JDK; check `javac -version` |
| `Could not find the data folder` | Running from the wrong directory | `cd` to the project root, or pass `--data path/to/data` |
| `Error: Could not find or load main class pokemon.Main` | Ran `java Main` or forgot `-cp out` | `java -cp out pokemon.Main`, from the project root |
| `data/species.csv line 14: column 'hp' should be a whole number` | A typo in a CSV edit | The message names the file, line and column — read it to them, do not fix it for them |
| Game exits immediately with "Input ended" | Input was piped or redirected | Run it interactively |
| A student's exercise loops forever | Missing base case or a `while` that never advances | `Ctrl-C`, then trace the loop variable on paper |
| Everything broke and nobody knows why | Uncommitted experimenting | This is the Git lesson. Until then: keep a pristine copy |

**The single most common conceptual error** in CS I here is integer division
(`current / max * 100` is always 0). It is Exercise 2 for a reason. Expect to teach
it three times.

---

## 6. Careers and ethics discussions that fit this project

Short, concrete prompts that arise naturally from the code:

- **Roles on a real game team.** This repo has an *engine* (`battle/`), *content*
  (`data/`), *tools* (`school/`, the runner), *QA* (`test/`) and *UX* (`ui/`). Ask
  students which they would want, and which one they think is hardest. Then tell them
  what those jobs pay and what they require.
- **Trademarks.** Why does the README say to retheme before publishing? What is the
  difference between using a name in a classroom and putting it on an app store? This
  is a real decision made in this repo, not a hypothetical.
- **Whose fun is it?** The catch formula is deliberately generous. Discuss how a
  studio would tune it for engagement, and where "engaging" becomes "manipulative"
  (loot boxes, streaks, energy timers).
- **Determinism and fairness.** `--seed` makes the game reproducible. Casinos and
  competitive games must prove their randomness is fair. How would you prove it?
- **Testing and responsibility.** `DataIntegrityTest` catches a broken route before a
  player finds it. Ask what the equivalent is when the software flies a plane.

---

## 7. Optional: version control (Unit 17)

If your network allows Git, the workflow that works with teenagers is small and
rigid:

```bash
git switch -c my-feature     # one branch per feature, named after the feature
# ...edit, then:
./test.sh                    # never commit red
git add -A
git commit -m "Add Rock Tomb to Geodude's learnset"
git switch main
git merge my-feature
```

Rules worth enforcing: never commit failing tests, one feature per branch, and write
commit messages that finish the sentence "this commit will…". The capstone requires
one merge, because merge conflicts are a skill and the first one should happen with
you in the room.

---

## 8. Retheming (and why you might want to)

Everything the player sees comes from `data/`. To replace the creatures entirely:

1. Edit `species.csv` — names, types, stats, evolutions, dex entries.
2. Edit `moves.csv` — names and flavour text. Keep the effect keywords.
3. Edit `learnsets.csv`, `encounters.csv`, `trainers.csv`, `areas.csv` to match.
4. Run `./test.sh`. `DataIntegrityTest` will find what you missed.

Themes that have worked in other classrooms: school clubs, elements of the periodic
table, cell organelles, historical figures, local landmarks, the teachers. The engine
does not change. That is the lesson — and it is also how you make the project safe to
publish.

---

## 9. Fastest path if you have one week

Day 1: play the game, then Trainer School lesson 1 and 4.
Day 2: read `Type.java` together; Exercises 1–3.
Day 3: read `DamageCalculator.java`; hand-trace it; Exercises 5 and 9.
Day 4: add a species to `species.csv`, give it a learnset, battle it.
Day 5: demo day — everyone shows their creature and explains one stat choice.

That week alone covers variables, types, operators, conditionals, arrays, methods,
testing and data modelling, and every student leaves having changed a real program.
