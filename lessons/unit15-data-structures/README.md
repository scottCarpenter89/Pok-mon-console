# Unit 15 — Data structures and graphs

**Course:** Computer Science III · **Time:** 6 class periods
**TEKS strands:** Implement and use stacks, queues and hash-based structures;
represent and traverse a graph; justify a choice of data structure.

## Objectives

1. Use a stack (LIFO) and a queue (FIFO) and choose correctly between them.
2. Explain why a hash map lookup does not slow down as the map grows.
3. Represent a graph and traverse it with breadth-first search.
4. Justify a data-structure choice in terms of the operations you need.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 13
# Trainer School -> 10. Data structures
```

One lesson, four structures: a stack of battle actions, a queue at the Pokémon
Center, a hash map of species, and a breadth-first search across the world map that
prints the actual route from Pallet Town to Cerulean City.

## Direct instruction

**Stack (LIFO)** — `push`, `pop`. Undo histories, the call stack from Unit 13, the
back button.

**Queue (FIFO)** — `add`, `poll`. Anything fair: print jobs, a line at a counter, and
**breadth-first search**.

**Hash map** — key in, value out, in roughly constant time regardless of size. Teach
the intuition (the key is turned into a number that says which bucket to look in) and
the consequence (you need a good `hashCode` and `equals`).

**Graph** — nodes and edges. `data/areas.csv` *is* an adjacency list, and students
have walked it:

```
pallet_town -> route1
route1      -> pallet_town, viridian_city
...
```

**BFS, in words:** put the start in a queue. Repeatedly take the front, look at its
neighbours, and queue any you have not seen — remembering where each was reached
from. Because you explore everything one step away before anything two steps away, the
first route you find is the shortest.

## Read

- `src/pokemon/world/WorldMap.java` — `shortestPath()`. Twenty lines. Trace it on the
  board for pallet_town → pewter_city with the queue and the `cameFrom` map drawn as
  two columns.
- `src/pokemon/school/DataStructuresLesson.java` — all four demos.
- `src/pokemon/data/GameData.java` — the hash-map registries.

## Lab (2–3 periods)

1. **Exercise 16** — `reachableAreas`: BFS that returns every area you can get to.
2. **Exercise 17** — `nearestPokemonCenter`: search over routes rather than numbers.
3. **Build it into the game:** add a "Fly to the nearest Pokémon Center" option to the
   overworld menu in `Game.java`, using your Unit 15 code. Suddenly the algorithm is a
   feature.
4. **Extend the map:** add three areas to `data/areas.csv` including a dead end and a
   loop. Does your BFS still work? Does `./test.sh` still pass? (`DataIntegrityTest`
   checks every area is reachable — which is itself a graph algorithm.)

## Design discussion (one period)

Ask, and make them defend the answers:

- Why is the party a `List` and the bag a `Map`?
- Why does `WorldMap` use `ArrayDeque` rather than `ArrayList` for the BFS queue?
  (`remove(0)` on an `ArrayList` is O(n); `poll()` on a deque is O(1).)
- The BFS `cameFrom` map does two jobs — remembering the path *and* marking nodes as
  visited. Is that clever or confusing?
- What would change if the map had one-way routes? (Nothing in the algorithm. The
  data already allows it. Prove it.)

## Errors to expect

- Using a stack instead of a queue for BFS and getting *a* path that is not the
  shortest. Excellent teachable moment — it still "works", which is why the bug
  survives.
- Forgetting the visited check, producing an infinite loop on a map with a cycle.
- Using `list.contains()` inside a loop and quietly making BFS O(n²). Fine at this
  scale; name it anyway.

## Exit ticket

1. Stack or queue: an undo feature? A print queue? BFS?
2. Why is a `HashMap` lookup not slower with a million entries?
3. In one sentence, why does BFS find the *shortest* path?
