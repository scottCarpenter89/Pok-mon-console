# Unit 8 — Classes and objects

**Course:** Computer Science I (bridging to II) · **Time:** 5–6 class periods
**TEKS strands:** Critical thinking (define a class with fields, a constructor and
methods; use objects); Creativity (create an original solution).

## Objectives

1. Explain the difference between a class and an object.
2. Write a class with fields, a constructor, getters and behaviour.
3. Explain why two objects of the same class can hold different values while sharing
   one `Species` object.
4. Use `this` correctly.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 6
# Trainer School -> 6. Classes and objects
```

Two Eevee, same species, different IVs, different stats. Then it takes 99,999 damage
and the HP stops at 0 instead of going negative — the object defends its own rules.

## Direct instruction

Draw this on the board and leave it up for the rest of the course:

```
     Species "Charmander"  (ONE object, in memory once)
        ^          ^          ^
        |          |          |
   your Charmander |     a wild Charmander
              a rival's Charmander
```

Each `Pokemon` object has its own level, HP, nickname and IVs, and a **reference** to
the shared `Species`. This is why changing a species' base stats changes every
Charmander in the game, and why nicknaming yours does not rename anyone else's.

Anatomy of a class, from `Pokemon.java`:
- **fields** — what it remembers
- **constructor** — how it is born (`new Pokemon(species, level, ivs)`)
- **methods** — what it can do
- **`this`** — "the object this method was called on"

Also show the **static factory**: `Pokemon.create(species, level, rng)` exists because
a second constructor with the same parameter types would be impossible, and because
`create` says what it does.

## Read

- `src/pokemon/model/Pokemon.java` — the constructor, `create()`, `takeDamage()`,
  `heal()`.
- `src/pokemon/model/Species.java` — a class that is mostly data.
- `src/pokemon/model/MoveSlot.java` — the clearest example in the project of shared
  data (`Move`) versus per-object data (`currentPp`). Ask why PP is not stored on the
  `Move`. (Every Charmander that knows Ember would share one PP counter.)

## Lab: write a class (2 periods)

Students write `Berry.java` in `src/pokemon/model/`:

```java
public class Berry {
    private final String name;
    private final int healAmount;
    private final Status curesStatus;   // may be null
    private int uses;
    // constructor, getters, and:
    public boolean useOn(Pokemon target) { ... }   // returns false if it did nothing
}
```

Requirements: fields private, a constructor that sets them, `useOn` that heals and/or
cures and decrements `uses`, and Javadoc on every public method. Then write a `main`
that makes a Pokémon, hurts it, and uses a Berry on it.

**Extension for the fast finishers:** make it a real item by adding a `BERRY` kind to
`ItemKind` and a row in `data/items.csv`.

## Exercises

Revisit **Exercise 7** (`strongestAttacker`) now that objects make sense, and start
**Exercise 11** (`moveNames`).

## Errors to expect

- Forgetting `new`, then `variable might not have been initialized`.
- Shadowing: a parameter named the same as a field, assigned `name = name`. Show
  `this.name = name` and explain exactly what `this` is pointing at.
- Expecting `pokemonA = pokemonB` to copy. It copies the *reference*: now two
  variables point at one object, and damaging "one" damages "both". Demonstrate it —
  it is the most valuable five minutes of the unit.

## Exit ticket

1. In one sentence each: what is a class, what is an object?
2. Why does `MoveSlot` store PP instead of `Move`?
3. `Pokemon a = b;` then `a.takeDamage(10);`. What is `b`'s HP now, and why?
