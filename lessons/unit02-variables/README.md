# Unit 2 — Variables and data types

**Course:** Computer Science I · **Time:** 3–4 class periods
**TEKS strands:** Critical thinking (declare and use variables with appropriate
primitive data types; integer vs floating-point arithmetic).

## Objectives

1. Declare variables of type `int`, `double`, `boolean` and `String`.
2. Choose the right type for a piece of data and justify the choice.
3. Predict the result of integer division and explain why it happens.
4. Read the fields of a class as "the data one object remembers".

## Hook (10 minutes)

Run the in-game lesson:

```bash
./run.sh --fast --seed 3
# Main menu -> Trainer School -> 1. Variables and types
```

It prints a live Pikachu's fields and then shows `current / max` giving `0` while
`(double) current / max` gives `0.775`. Ask for a prediction *before* the second line
appears.

## Direct instruction

Draw the object on the board as a box of labelled slots:

```
  Pikachu object
  +---------------------------------+
  | String nickname  = "Pikachu"    |
  | int    level     = 12           |
  | int    currentHp = 31           |
  | int    maxHp     = 40           |
  | Status status    = NONE         |
  +---------------------------------+
```

Key points:
- The **type** comes first and never changes. Java will refuse to put a `String` in
  an `int` slot — at compile time, before the program ever runs.
- `int` holds whole numbers. `double` holds decimals. `boolean` holds only
  `true`/`false`. `String` holds text.
- **Integer division throws away the remainder.** `7 / 2` is `3`. This is not a bug;
  it is the rule. It is also the single most common mistake in this course.

## Read

- `src/pokemon/model/Pokemon.java` — the field declarations at the top (about 20
  lines). Do not read the methods yet.
- `src/pokemon/model/Stats.java` — six `int`s in a record.

Discussion: why is `level` an `int` and not a `double`? Why is `nickname` a `String`
and not a `char`? Why is `isFainted()` a method rather than a stored `boolean`?
(Because it can be worked out from `currentHp` — storing it twice means it can get out
of step. That idea comes back in Unit 9.)

## Lab (30 minutes)

In `data/species.csv`, design a creature of your own — but only fill in the numeric
columns for now, and write down for each column *why* an `int` is the right type. Then
argue: should `catchRate` be a `double` between 0 and 1 instead? What would break?

## Exercises

- **Exercise 1** `totalBaseStats` — adding six fields.
- **Exercise 2** `healthPercent` — the integer-division trap.

Run `./exercises.sh` until both say `DONE`.

## Errors to expect

- `incompatible types: possible lossy conversion from double to int` — assigning a
  `double` to an `int`. Ask: what would be lost?
- Exercise 2 returning 0 for everything. This is the lesson. Do not just tell them —
  have them print `currentHp / maxHp` and see the `0` for themselves.

## Exit ticket

1. What does `int hp = 7 / 2;` put in `hp`? Why?
2. Give one value in this game that *must* be a `double`, and one that must be an
   `int`. Defend both.
3. `isFainted()` is not stored as a field. Why not?
