# Unit 6 — Arrays and 2D arrays

**Course:** Computer Science I · **Time:** 4 class periods
**TEKS strands:** Critical thinking (create and traverse one- and two-dimensional
arrays; nested loops; flowcharts and pseudocode).

## Objectives

1. Declare, fill and traverse a one-dimensional array.
2. Explain zero-indexing and the cause of `ArrayIndexOutOfBoundsException`.
3. Traverse a two-dimensional array with nested loops and say what each loop controls.
4. Read a real 18×18 lookup table and use it.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 4
# Trainer School -> 4. Arrays and the type chart
```

It prints `Type.values()` as an indexed list, then a 6×6 corner of the effectiveness
chart, then the nested loop that produced it.

## Direct instruction

A 2D array is a table. `chart[attacker][defender]` — **row first, then column**. Say
it out loud every time; mixing them up is the number-one 2D array bug, and in this
game it produces a chart where Water is weak to Fire, which students notice
immediately.

The type chart in `Type.java` is stored as 18 strings of 18 characters:

```java
"211244222224121242", // FIRE
```

and decoded once into a `double[18][18]`. Ask why it is written that way. (A human can
proofread 18 lines of text; nobody can proofread 324 comma-separated doubles. Code is
written for people.)

## Read

- `src/pokemon/model/Type.java` — `CHART_ROWS`, `decodeChart()`, `decode()`,
  `effectiveness()`. This is the single best file in the project for this unit.
- `src/pokemon/battle/Battle.java` — the `int[] stages` array in `Side`, indexed by
  `Stat.ordinal()`. A small, real 1D array.

## Lab: prove the chart is right (30 minutes)

Split the 18 attacking types across the class. Each student verifies their row against
a reference chart and reports. Then:

1. Deliberately corrupt one cell (change a `4` to a `1`).
2. Run `./test.sh`. Does a test catch it? (`TypeChartTest` checks specific matchups
   and that every cell is legal — so some corruptions are caught and some are not.)
3. **Discussion worth having:** what test *would* catch any corruption? What would it
   cost to write? This is where students first meet the idea that testing is a
   judgement call, not a checkbox.

## Extension

Write a method that, given a defending type pair, prints every attacking type that
hits it for 4× damage. This is a nested loop over `Type.values()` with a condition —
and it is genuinely useful when balancing a route.

## Exercises

Revisit **Exercise 5** (`hpBar` — building a String with a loop) and set the
extension above for students who finish early.

## Errors to expect

- `ArrayIndexOutOfBoundsException: Index 18 out of bounds for length 18` — the last
  index is 17. Expect this at least once per student.
- Swapping row and column. The symptom is a chart that is "backwards" for
  non-symmetric matchups.
- `array.length` vs `list.size()` vs `string.length()` — three names for nearly the
  same idea, and Java uses all three.

## Exit ticket

1. A `double[18][18]` holds how many numbers?
2. What is the largest legal index of an array with 18 elements?
3. In `chart[a][b]`, which is the attacker and how do you know?
