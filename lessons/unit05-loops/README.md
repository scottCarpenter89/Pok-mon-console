# Unit 5 — Loops

**Course:** Computer Science I · **Time:** 4 class periods
**TEKS strands:** Critical thinking (iterative statements; nested loops; trace
execution by hand).

## Objectives

1. Write `for`, enhanced `for` and `while` loops.
2. Choose between `for` and `while` and justify the choice.
3. Use the accumulator and counter patterns.
4. Identify and fix an infinite loop and an off-by-one error.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 8
# Trainer School -> 3. Loops that do the work
```

It levels a Charmander from 5 to 12 with a `while` loop and prints every level-up,
then adds six base stats with an accumulator. The lesson ends by asking which loop
you need if a Pokémon gains three levels at once.

## Direct instruction

**The rule of thumb:**

| Use | When |
|---|---|
| `for (int i = 0; i < n; i++)` | You know how many times, and you need the index |
| `for (Thing t : list)` | You know how many times, and you do not need the index |
| `while (condition)` | You repeat until something becomes true |

**The accumulator pattern** — declare the total *outside* the loop:

```java
int total = 0;                       // outside!
for (Stat stat : Stat.values()) {
    total += species.getBaseStats().get(stat);
}
```

Declaring `total` inside is the classic bug: it resets every pass and you get the last
value instead of the sum.

**The real `while` in this game** (`Pokemon.gainExperience`):

```java
while (level < MAX_LEVEL && experience >= experienceNeededForLevel(level + 1)) {
    level++;
    ...
}
```

An `if` here would look right and be wrong — a big battle would grant one level and
silently discard the rest. This exact bug is in the teacher's break-it-on-purpose list
because it is so convincing.

## Read

- `src/pokemon/model/Pokemon.java` — `gainExperience()`.
- `src/pokemon/model/Type.java` — `decodeChart()`, a nested loop (preview of Unit 6).
- `src/pokemon/world/Area.java` — `rollEncounter()`, a loop that subtracts until it
  crosses zero. Act this out with index cards: 40 tickets for Pidgey, 40 for Rattata,
  10 for Caterpie, 2 for Eevee.

## Lab: break it, then fix it (30 minutes)

1. In `Pokemon.gainExperience`, change `while` to `if`. Rebuild.
2. Run `./test.sh`. Which test fails, and does its name tell you what broke?
3. Now play: win a big battle at a low level and watch the experience vanish.
4. Restore the `while` and confirm the test passes.

This is the first time many students see a test suite *catch them*. Let that land.

## Exercises

- **Exercise 6** `countFainted` — the counting pattern.
- **Exercise 7** `strongestAttacker` — find the maximum.
- **Exercise 8** `usableMoveCount` — loop with a condition inside.

## Errors to expect

- Infinite loop from forgetting `i++` or from a condition that never changes.
  `Ctrl-C` stops it; then trace the variable on paper.
- `best` initialised to `0` in a "find the maximum" over objects (it should start as
  `null` and be checked).
- Off-by-one: `i <= list.size()`.

## Exit ticket

1. Give one situation from this game that needs `while` and one that needs `for`.
2. What is printed if `int total = 0;` is moved inside the loop?
3. A loop over a party of 6 throws `IndexOutOfBoundsException: Index 6`. What is the
   bug?
