# Unit 14 — Searching, sorting and Big-O

**Course:** Computer Science II → III · **Time:** 5 class periods
**TEKS strands:** Critical thinking (implement linear and binary search; implement and
trace a sorting algorithm; compare algorithm efficiency).

## Objectives

1. Implement linear search and binary search.
2. Explain why binary search requires sorted data.
3. Implement and trace one O(n²) sort.
4. Describe the growth of O(n), O(log n) and O(n²) and predict which matters when.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 12
# Trainer School -> 9. Searching and sorting
```

It searches the Pokédex both ways and prints the comparison counts — typically ~36
versus 5 on 55 species. Then it shows a selection sort pass by pass.

## The number that sells it

| n | linear O(n) | binary O(log n) |
|---|---|---|
| 50 | 50 | 6 |
| 1,000 | 1,000 | 10 |
| 1,000,000 | 1,000,000 | 20 |
| 1,000,000,000 | 1,000,000,000 | 30 |

Same computer. Better algorithm. This table is the single most important idea in the
back half of a CS sequence, and it is worth putting on the wall.

## Direct instruction

**Binary search** works only on sorted data, and that is the trade: sort once (slow),
search many times (fast). `GameData` does something equivalent with a `HashMap` —
build the index once at start-up, then every lookup is effectively instant.

**Selection sort:** for each position, find the smallest of what is left and swap it
in. n(n−1)/2 comparisons. Trace it on six cards at the front of the room before
anybody writes code.

**Big-O in one sentence:** it describes how the work grows as the data grows, ignoring
constants. O(n²) beats O(n log n) at n = 5 and loses catastrophically at n = 100,000.

## Read

- `src/pokemon/school/SearchSortLesson.java` — `linearSearch`, `binarySearch`,
  `selectionSortWithLog`, all instrumented with comparison counters.

## Lab: measure it yourself (2 periods)

1. Implement Exercise 14 (`binarySearch`) and Exercise 15 (`sortByStatDescending`).
2. Add a counter to each and print the number of comparisons.
3. Sort lists of 10, 100 and 1,000 randomly generated Pokémon and record the counts.
4. Plot the results. Does selection sort's curve look like n²? Does binary search's
   look like log n?
5. Compare your sort against `Collections.sort()` on the same data using
   `System.nanoTime()`. Report the ratio.

**Writing task:** three paragraphs — what you measured, what you expected, and which
algorithm you would ship. That is a lab report, and it is exactly the research- and
communication-strand evidence an administrator wants to see.

## Exercises

- **Exercise 14** `binarySearch`
- **Exercise 15** `sortByStatDescending`

## Errors to expect

- `while (low < high)` misses the final element. Show it failing on a one-element
  list.
- Forgetting `+ 1` / `− 1` when moving `low` or `high`, giving an infinite loop.
- Running binary search on unsorted data and getting *sometimes* correct answers —
  the most dangerous kind of bug, and a great discussion.
- Losing an element in the swap by overwriting before saving the temporary.

## Exit ticket

1. Why does binary search need sorted data?
2. n = 1,000,000: how many comparisons for linear? For binary?
3. Your program searches a 200-item list once at start-up. Should you sort it first?
   Defend your answer.
