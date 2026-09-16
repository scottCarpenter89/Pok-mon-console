# Unit 7 — Methods and decomposition

**Course:** Computer Science I · **Time:** 4 class periods
**TEKS strands:** Critical thinking (write methods with parameters and return values;
decompose a problem); Technology operations (naming conventions, internal
documentation).

## Objectives

1. Write a method with parameters and a return value.
2. Explain the difference between a `static` method and an instance method.
3. Decompose a long process into named steps.
4. Write a Javadoc comment that says what a method does, not how.

## Hook (10 minutes)

Show `Battle.runTurn()` on the projector — about 30 lines — and ask what it does.
Students can answer *immediately*, because the method names say it: choose an action,
decide who goes first, attack, end of turn. Then show them that the full logic behind
those four steps is several hundred lines. That is decomposition: you read the level
you need.

## Direct instruction

Anatomy:

```java
public static int computeDamage(int level, int power, ...) {
//     ^ static  ^ returns  ^ name         ^ parameters
```

- **Parameters** are the information the method needs. They are copies — changing a
  parameter inside a method does not change the caller's variable. (Show this with an
  `int`. Objects are the subtle case; flag it for Unit 8.)
- **Return** hands one value back. A method that returns nothing is `void`.
- **`static`** means the method belongs to the class, not to an object.
  `DamageCalculator` is entirely static because it needs no state — it is a
  calculator, not a thing.

**Pure functions.** `computeDamage` has no side effects: same inputs, same output,
forever. That is why `DamageCalculatorTest` can assert an exact number. Keeping
calculation separate from action is the habit to build here.

## Read

- `src/pokemon/battle/DamageCalculator.java` — every method. Note how small each one
  is, and that each has one job.
- `src/pokemon/util/Text.java` — four tiny static helpers used everywhere.
- `src/pokemon/battle/Battle.java` — `runTurn()` and the methods it calls.

## Lab: extract a method (30 minutes)

Give students this deliberately ugly version of a real piece of logic:

```java
// Ugly on purpose. Refactor it.
if (p.getStatus() == Status.POISON) {
    int d = p.getMaxHp() / 8; if (d < 1) d = 1; p.takeDamage(d);
    System.out.println(p.getNickname() + " is hurt by poison! (-" + d + ")");
} else if (p.getStatus() == Status.BURN) {
    int d = p.getMaxHp() / 16; if (d < 1) d = 1; p.takeDamage(d);
    System.out.println(p.getNickname() + " is hurt by its burn! (-" + d + ")");
}
```

Ask for: a method `damageFromStatus(Pokemon, Status)` that **returns** the damage and
prints nothing, and a caller that does the printing. Then compare with the real
`Battle.endOfTurn()`. Discuss why separating "work out the number" from "tell the
player" makes the first part testable.

## Exercises

All of **Exercises 1–9** should be `DONE` by the end of this unit. Every one of them
is a method with parameters and a return value.

## Errors to expect

- `missing return statement` — a path through the method that returns nothing.
- Calling an instance method from a static one (`non-static method cannot be
  referenced from a static context`). This confuses everyone once; explain it with
  "whose HP would it take away?"
- Methods that print *and* return. Usually a sign the method is doing two jobs.

## Exit ticket

1. Why is `DamageCalculator.computeDamage` `static` but `pokemon.takeDamage` is not?
2. What makes a method easy to test?
3. Split `hpBar(current, max, width)` into two methods, and say what each is called
   and what it returns.
