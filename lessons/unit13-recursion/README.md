# Unit 13 — Recursion

**Course:** Computer Science II · **Time:** 4 class periods
**TEKS strands:** Critical thinking (implement recursive methods with a correct base
case; compare recursive and iterative solutions).

## Objectives

1. Identify the base case and the recursive case of a problem.
2. Write a recursive method that terminates.
3. Trace a recursive call stack on paper.
4. Convert between a recursive and an iterative solution, and discuss the trade-off.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 9
# Trainer School -> 8. Recursion
```

It prints evolution chains, built by a four-line recursive method, and then a
countdown. Evolution is the ideal first example because the structure is visibly
recursive: *the chain for Charmander is Charmander followed by the chain for
Charmeleon.*

## Direct instruction

Every recursive method needs exactly two things. Write them on the board and refer
back every single time:

1. **A base case** that returns *without* calling itself.
2. **A recursive case** on a *smaller* problem.

```java
private int stagesLeft(Species species) {
    if (species.getEvolvesInto() == null) {
        return 0;                                 // BASE CASE
    }
    return 1 + stagesLeft(species.getEvolvesInto());   // SMALLER PROBLEM
}
```

**Trace it on the board** for Bulbasaur, drawing the stack:

```
stagesLeft(Bulbasaur)  = 1 + stagesLeft(Ivysaur)
                                 = 1 + stagesLeft(Venusaur)
                                         = 0
                       = 1 + (1 + 0) = 2
```

Every call is a promise to come back. `StackOverflowError` is what happens when there
are too many unfinished promises — demonstrate it by deleting a base case.

## Read

- `src/pokemon/school/RecursionLesson.java` — `buildChain()`, `stagesLeft()`,
  `countdown()`.
- `src/pokemon/school/SearchSortLesson.java` — `binarySearch()` is written
  *iteratively*; ask students to rewrite it recursively and compare.

## Lab: both ways (30 minutes)

Write `sumTo(n)` recursively (Exercise 13) and then iteratively with a loop. Then:

1. Which is shorter? Which is clearer?
2. Run both with `n = 100000`. One of them crashes. Why?
3. When is recursion worth the risk? (When the *data* is recursive: trees, chains,
   nested structures — not for counting.)

This is an honest treatment of recursion: powerful for the right shape of problem,
and the wrong tool for a countdown.

## Exercises

- **Exercise 12** `evolutionsRemaining` — recursion over real game data.
- **Exercise 13** `sumTo` — the classic.

## Errors to expect

- `StackOverflowError`: no base case, or the recursive call is not smaller. The
  exercise runner catches this specifically and says so.
- Base case after the recursive call (unreachable).
- Forgetting to *return* the recursive result: `stagesLeft(next);` instead of
  `return 1 + stagesLeft(next);`.

## Extension

Add a `printFamilyTree` that handles branching evolution (an Eevee-style species with
several possible evolutions). The data model supports only one evolution today — so
this is a genuine design change, and a good one to plan on paper first.

## Exit ticket

1. What two parts must every recursive method have?
2. Trace `sumTo(4)` and show every call and every return.
3. Give one problem in this game where recursion is the natural fit, and one where a
   loop is better.
