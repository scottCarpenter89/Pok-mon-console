# Unit 4 — Conditionals and the type chart

**Course:** Computer Science I · **Time:** 3–4 class periods
**TEKS strands:** Critical thinking (conditional statements; logical operators;
boolean expressions).

## Objectives

1. Write `if`, `if/else` and `if/else if` chains, and explain why order matters.
2. Use `&&`, `||` and `!` correctly and build a truth table.
3. Use a `switch` statement and say when it beats an `if` chain.
4. Return a boolean expression directly instead of `if (x) return true;`.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 2
# Trainer School -> 2. Making decisions
```

Students pick an attacking and a defending type and watch the if-chain pick the
message. Then ask the killer question: *what happens if we swap the first two
branches?* (Every 0× hit would be announced as "super effective" — because `0.0 > 1.0`
is false but `== 0.0` would never be reached. Order is not a style choice.)

## Read

- `src/pokemon/battle/DamageCalculator.java` — `effectivenessMessage()`.
- `src/pokemon/model/Type.java` — `decode()`, a `switch` with a `default` that throws.
- `src/pokemon/battle/Battle.java` — `canMove()`: a `switch` over an enum where each
  branch has real consequences.
- `src/pokemon/model/Pokemon.java` — `applyStatus()`: a compound condition with `||`.

## Direct instruction

**Order matters.** An `else if` chain is checked top to bottom and stops at the first
`true`. Put the most specific test first.

**Boolean expressions are values.** This:

```java
if (Type.effectiveness(a, d) > 1.0) { return true; } else { return false; }
```

is the same as this:

```java
return Type.effectiveness(a, d) > 1.0;
```

The second one is not just shorter — it is easier to be sure about.

**Short circuit.** In `a || b`, if `a` is true Java never looks at `b`. That is why
`pokemon != null && pokemon.isFainted()` is safe and the other order is not.

## Lab: truth tables and a real condition (25 minutes)

Take the real line from `Pokemon.applyStatus`:

```java
if (isFainted() || status != Status.NONE || newStatus == Status.NONE) return false;
```

1. Build the full truth table (8 rows, 3 conditions).
2. In English, what does this guard prevent? (Poisoning a fainted Pokémon; stacking
   two conditions; "applying" no condition at all.)
3. Rewrite it with `&&` and a positive test. Which version would you rather maintain?

## Exercises

- **Exercise 3** `isSuperEffective` — return the comparison.
- **Exercise 4** `needsHealing` — `||` with two real conditions.

## Errors to expect

- `if (x = 5)` instead of `==`. Java catches this one for booleans — explain why C
  programmers are jealous.
- `&&` where `||` was meant. Have them read it aloud: "fainted AND low health" is
  clearly not what we want.
- Comparing `String`s with `==`. Show it appearing to work for short literals and
  then failing, and teach `.equals()`.

## Exit ticket

1. Rewrite `if (a > b) return true; else return false;` in one line.
2. `false && somethingBroken()` does not crash. Why not?
3. In an `else if` chain that tests `== 0`, `> 1` and `< 1`, which order is correct
   and why?
