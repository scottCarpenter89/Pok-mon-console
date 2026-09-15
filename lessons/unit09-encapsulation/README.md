# Unit 9 — Encapsulation, static vs instance, documentation

**Course:** Computer Science II · **Time:** 4 class periods
**TEKS strands:** Critical thinking (design classes with appropriate encapsulation;
distinguish class from instance members); Technology operations (documentation).

## Objectives

1. Explain what `private` buys you, with a concrete example of a bug it prevents.
2. Decide whether a member should be `static` or per-object.
3. Write getters and controlled mutators instead of public fields.
4. Document a class and its public methods with Javadoc.

## Hook (10 minutes)

Make `currentHp` public in `Pokemon.java`. Then, in `Game.java`, write
`player.getParty().get(0).currentHp = -500;` and run. The HP bar renders nonsense and
the "fainted" check silently breaks. Put it back. **That** is what `private` is for —
not a rule from a textbook.

## Direct instruction

**Encapsulation is about invariants.** An invariant is a statement that must always be
true. In `Pokemon` the invariants are:

- `0 <= currentHp <= getMaxHp()`
- a fainted Pokémon has no status condition
- a Pokémon knows at most four moves

Every one of those is enforced inside the class. There is no way to break them from
outside, which means there is exactly one place to look when one breaks.

**Static vs instance, decided out loud:**

| Member | Which | Why |
|---|---|---|
| `Pokemon.currentHp` | instance | Every Pokémon has its own |
| `Pokemon.MAX_MOVES` | static | It is 4 for everyone, forever |
| `Pokemon.experienceNeededForLevel(n)` | static | Depends only on `n` |
| `DamageCalculator.computeDamage(...)` | static | Needs no object at all |
| `Species` | one object, many references | "Charmander" exists once |

## Read

- `src/pokemon/model/Pokemon.java` — `takeDamage`, `heal`, `applyStatus`,
  `learnMove`. Every one is a guarded mutator.
- `src/pokemon/model/Move.java` — everything `final`, no setters at all. Ask why a
  shared object must be immutable.
- `src/pokemon/model/Bag.java` — `remove()` refuses to go negative.

## Lab: find the leak (30 minutes)

`Pokemon.getMoves()` returns the real internal list, so outside code *could* clear it.
`Trainer.getParty()` does the same. Have students:

1. Prove the leak: write code that empties a Pokémon's moves from outside.
2. Argue both sides. Returning a copy is safer; but the battle engine legitimately
   needs to spend PP on the real slots, and copying every party every frame is
   wasteful.
3. Propose a fix and say what it costs.

There is no single right answer, and that is the point — this is the first design
trade-off discussion of CS II. (The project's own answer: it is a known, documented
compromise for a single-threaded teaching codebase.)

## Exercises

Set **Exercises 10 and 11** (Map and List), which need objects to be understood first.

## Errors to expect

- Writing a getter *and* a setter for everything by reflex. Ask each time: should the
  outside world be allowed to change this at all?
- `private` on a method that another class needs, then making the whole class public
  to fix it. Discuss the middle ground.

## Exit ticket

1. Name an invariant of `Pokemon` and the method that protects it.
2. Should `MAX_PARTY` be static? Defend it.
3. Why does `Move` have no setters?
