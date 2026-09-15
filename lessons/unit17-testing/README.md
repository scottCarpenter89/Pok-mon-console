# Unit 17 — Testing, debugging and version control

**Course:** Computer Science III · **Time:** 5 class periods
**TEKS strands:** Design, write and run unit tests; defensive programming and input
validation; debug systematically; use version control in a team project.

## Objectives

1. Write a unit test that fails for the right reason before it passes.
2. Explain what a test framework does, having read one.
3. Test boundary values and error paths, not just the happy path.
4. Debug systematically instead of by guessing.
5. Use branches, commits and a merge (if your lab allows Git).

## Hook (10 minutes)

```bash
./test.sh
```

177 checks in about a second. Then break something on purpose — change
`STAB_MULTIPLIER` to `2.0` — and run it again. A test fails and *names the constant*.
Ask how long it would take to find that change by playing the game.

## Direct instruction

**Read the framework.** `test/pokemon/MiniTest.java` is 60 lines: two counters, three
assertion methods, a summary. JUnit does more, but it does not do anything
*mysterious*. Students who have read this one are no longer intimidated by the real
thing.

| MiniTest | JUnit |
|---|---|
| `check(what, condition)` | `assertTrue` |
| `checkEquals(what, expected, actual)` | `assertEquals` |
| `checkThrows(what, type, action)` | `assertThrows` |
| `summary()` | the green/red bar |

**What makes code testable.** `DamageCalculator.computeDamage` is a pure function, so
a test can assert `28`. `Battle.runTurn()` needs a player, an opponent, a UI and a
random source — much harder. Ask: which parts of this project are hard to test, and
what would you change? That question is the heart of CS III.

**Test the boundaries.** `PokemonTest` checks 0 HP, healing past the maximum,
negative damage, level 100, exactly-enough experience and a two-level jump. The happy
path was never where the bugs were.

**Test your data too.** `DataIntegrityTest` checks that every area is reachable, every
trainer can battle, and every wild Pokémon has a move. Most classroom breakage is a
spreadsheet typo, not a Java bug.

## Read

- `test/pokemon/MiniTest.java` — all of it.
- `test/pokemon/DamageCalculatorTest.java` — exact-value assertions.
- `test/pokemon/DataIntegrityTest.java` — testing data rather than code.

## Lab 1: red, green (one period)

Test-driven, in the strict order:

1. Add a check to `PokemonTest` for behaviour that does not exist yet — say, that a
   Pokémon at level 100 gains no experience.
2. Run `./test.sh`. **It must fail.** A test that passes before you write the code is
   testing nothing.
3. Make it pass.
4. Repeat for: a Pokémon cannot learn the same move twice; `Bag.remove` with a
   negative count; a burn on a Fire type fails.

## Lab 2: systematic debugging (one period)

Hand out a build with one seeded bug (suggestions: swap `low`/`high` in a search, use
`<=` in `Type.decodeChart`, remove the `Math.max(0, ...)` in `takeDamage`). Students
must find it using only:

1. `./test.sh` — which test fails, and what does its name tell you?
2. `--seed` — reproduce it identically every time.
3. `System.out.println` — narrow it down, then delete the prints.

**Then have them write the bug report:** what you did, what you expected, what
happened, the seed. That is a professional artefact and it is worth grading.

## Version control (one period, if your lab allows it)

```bash
git switch -c add-flinch-effect
# edit, then:
./test.sh                # never commit red
git add -A
git commit -m "Add FLINCH move effect and a test for it"
git switch main
git merge add-flinch-effect
```

Three rules: one feature per branch, never commit failing tests, and write messages
that finish "this commit will…". Then **create a merge conflict on purpose** — two
students edit the same line of `data/moves.csv` — and resolve it together. The first
conflict should happen with a teacher in the room.

## Exit ticket

1. Why must a new test fail before it passes?
2. Name a boundary value worth testing in this game and say what could go wrong at it.
3. Why is `computeDamage` easier to test than `runTurn`?
