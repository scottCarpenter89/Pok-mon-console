# Unit 11 — Collections: List and Map

**Course:** Computer Science II · **Time:** 4 class periods
**TEKS strands:** Critical thinking (use `ArrayList` and other collections; use a Map
of key–value pairs); Research fluency (use API documentation).

## Objectives

1. Use `ArrayList`: add, get, remove, size, iterate.
2. Use `Map`: put, get, containsKey, getOrDefault, merge, iterate entries.
3. Choose between a List and a Map for a given job and justify it.
4. Find and use an unfamiliar method from the Java API documentation.

## Hook (10 minutes)

Ask: the player's bag holds items and counts. How would you store it with arrays?
Let them design it (`String[] names` and `int[] counts`), then ask what happens when
an item runs out in the middle. Then show `Bag.java`: one `LinkedHashMap`, and
`remove()` is six lines.

## Direct instruction

**Arrays vs `ArrayList`:** arrays are fixed-size; `ArrayList` grows. That is the whole
sales pitch, and it is enough.

**`Map` is a lookup table.** Key in, value out. In this project:

| Map | Key | Value | Why a Map |
|---|---|---|---|
| `Bag.contents` | `Item` | how many you own | Look up by item, not by position |
| `GameData.species` | `"charmander"` | the `Species` object | Instant lookup by id from a CSV file |
| `WorldMap.areas` | `"route1"` | the `Area` object | Same |
| `WorldMap.cameFrom` (in BFS) | area id | the area you came from | Remembering a path |

**Which implementation?** `HashMap` is fast but the order of its keys is arbitrary.
`LinkedHashMap` keeps insertion order, which is why the bag menu does not shuffle
itself between turns. Choosing between them is a real decision with a visible
consequence — show it by swapping `Bag` to a `HashMap` and watching the menu jump
around.

## Read

- `src/pokemon/model/Bag.java` — the whole file.
- `src/pokemon/data/GameData.java` — four registries and the lookup methods that
  throw a helpful exception on a bad key.
- `src/pokemon/model/Trainer.java` — `party` as a `List`, with `MAX_PARTY` enforced.

## API documentation activity (25 minutes)

Do not teach `merge`. Instead, give them the problem — "count how many species have
each type" — and send them to the `java.util.Map` documentation to find a method that
helps. They will find `merge` or `getOrDefault`; both are correct. Then have two
students who chose differently present their versions.

This is the research-fluency standard, done properly: the skill is *finding* the
method, not memorising it.

## Exercises

- **Exercise 10** `countByPrimaryType` — building a Map.
- **Exercise 11** `moveNames` — building a List.

## Errors to expect

- `ConcurrentModificationException` from removing during a for-each loop. Show it,
  then show the iterator or the "collect then remove" fix.
- Using `==` on `String` keys. Works by accident with literals, then fails.
- Forgetting a `Map` returns `null` for a missing key, then a
  `NullPointerException` one line later. `getOrDefault` exists for this.
- `HashMap` iteration order surprising them. That is not a bug; it is the contract.

## Exit ticket

1. Bag contents: List or Map? Party: List or Map? Defend both.
2. What does `getOrDefault(key, 0)` do that `get(key)` does not?
3. Why does `Bag` use `LinkedHashMap` instead of `HashMap`?
