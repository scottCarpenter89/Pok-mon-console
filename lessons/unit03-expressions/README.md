# Unit 3 — Expressions and the damage formula

**Course:** Computer Science I · **Time:** 3 class periods
**TEKS strands:** Critical thinking (arithmetic, relational and logical operators
including precedence; trace program execution by hand).

## Objectives

1. Evaluate arithmetic expressions with correct operator precedence.
2. Translate a written formula into a Java expression.
3. Hand-trace an expression and verify the answer against the running program.
4. Explain how casting changes the result of a division.

## Hook (15 minutes)

```bash
./run.sh --fast --seed 11
# Trainer School -> 5. Methods and the damage formula
```

It prints the damage calculation step by step with real numbers. Stop after the
`base = ...` line and have students finish the arithmetic on paper before the program
shows the answer.

## The formula

```
base   = ((2 * level / 5 + 2) * power * attack / defense) / 50 + 2
damage = base * STAB * typeMultiplier * critical * random
```

Work through it with Charmander's Ember on Bulbasaur at level 12. Insist on writing
the order of operations out explicitly:

```
2 * 12      = 24
24 / 5      = 4        <-- integer division, not 4.8
4 + 2       = 6
6 * 40      = 240      <-- move power
240 * 22    = 5280     <-- attacker's Sp. Attack
5280 / 23   = 229      <-- defender's Sp. Defense, integer division again
229 / 50    = 4
4 + 2       = 6        <-- base damage
6 * 1.5 * 2.0 = 18     <-- STAB, then super effective
```

Then run it and confirm. When the numbers match, the class has just proved the
computer is doing exactly what they did — no magic.

## Read

- `src/pokemon/battle/DamageCalculator.java` — `computeDamage()`, all of it.
- `src/pokemon/model/Pokemon.java` — `getMaxHp()` and `getStat()`.

Ask: where does integer division happen, and does it help or hurt the player? (It
rounds damage *down*, so it slightly favours the defender. Game designers choose
these things.)

## Lab: change the balance (30 minutes)

1. In `DamageCalculator`, change `STAB_MULTIPLIER` from `1.5` to `3.0`. Rebuild, play
   a battle, and describe what happened to the game.
2. Change `CRITICAL_CHANCE_PERCENT` to `100`. Play again.
3. Put both back and run `./test.sh` to prove you restored them. While either value is
   wrong a test fails and names the constant — `DamageCalculatorTest` deliberately pins
   the balance numbers, so that tuning the game is something you do on purpose rather
   than by accident. Point out that the test caught it, not their memory.

## Exercises

- **Exercise 5** `hpBar` — expressions plus a loop.
- **Exercise 9** `experienceReward` — translating a written formula exactly.

## Errors to expect

- Doing the multiplication and division in the wrong order and getting 0.
- Writing `* 1.5` in a method that returns `int` and being surprised by the error.
  The fix — `* 3 / 2` — is worth teaching as a technique.
- Forgetting that `Math.round` returns a `long`, so it needs a cast to `int`.

## Exit ticket

1. Evaluate `(2 * 15 / 5 + 2) * 60 / 50 + 2` by hand, showing each step.
2. Why does `experienceReward` use `* 3 / 2` rather than `* 1.5`?
3. A student writes `damage = power * attack / defense / 50 + 2` and gets 2 every
   time. What is happening?
