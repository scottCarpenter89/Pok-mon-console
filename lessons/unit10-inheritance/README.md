# Unit 10 — Inheritance, interfaces and polymorphism

**Course:** Computer Science II · **Time:** 5–6 class periods
**TEKS strands:** Critical thinking (inheritance and overriding; polymorphism;
interfaces and abstract classes; composition vs inheritance).

## Objectives

1. Extend a class and override a method, using `super` where appropriate.
2. Explain why an abstract class cannot be instantiated and when to use one.
3. Implement an interface and explain the contract it represents.
4. Explain polymorphism using code from this project, not a Shape/Circle example.
5. Choose between "is-a" and "has-a".

## Hook (15 minutes)

```bash
./run.sh --fast --seed 7
# Trainer School -> 7. Inheritance and polymorphism
```

It prints the family tree, calls `battleTitle()` on a `Player` and an `NpcTrainer`
stored in the same `List<Trainer>`, then asks three different AI classes to choose a
move for the same Onix against the same Pikachu. They give different answers. The
battle engine contains no `if` about which AI is playing.

## Direct instruction

**Inheritance (is-a).**

```
        Trainer  (abstract: name + party + healing + switching)
        /      \
   Player       NpcTrainer
```

`Player IS-A Trainer`. It inherits everything and adds money, a bag and a Pokédex.
`Trainer` is `abstract` because "trainer" is a category — there is no such thing as a
generic trainer in this game, and `new Trainer("Bob")` is a compile error.

**Overriding.** `NpcTrainer.battleTitle()` replaces the inherited version. Java picks
the right one at *run time* based on the actual object, not the variable's declared
type. That is polymorphism, and it is why `for (Trainer t : everyone)` works.

**`super`.** `CleverAI.scoreMove()` calls `super.scoreMove()` and then adjusts the
result. Improve `TypeSmartAI` and `CleverAI` gets better for free. Show the
alternative — copy and paste — and what happens when you fix a bug in one copy.

**Interfaces (can-do).** `BattleAI` says: whoever you are, you must be able to
`chooseMove`. No shared code, no family tree, just a contract. `NpcTrainer` **has-a**
`BattleAI` rather than **being** one, which is why difficulty is data
(`data/trainers.csv` column `ai`) instead of code.

## Read

- `src/pokemon/model/Trainer.java`, `Player.java`, `NpcTrainer.java`.
- `src/pokemon/battle/BattleAI.java`, `RandomAI.java`, `TypeSmartAI.java`,
  `CleverAI.java` — all four are short; read them in that order.
- `src/pokemon/school/Lesson.java` — a second interface, used for a completely
  different purpose. Two examples make the idea generalise.

## Lab: write an opponent that plays the game (2–3 periods)

This is the best assignment in the course.

1. Complete **Exercise 18** in `src/pokemon/exercises/MyFirstAI.java` until
   `./exercises.sh` says `DONE`.
2. Now put it in the real game:
   - add a case to `BattleAI.byName()` returning `new MyFirstAI()`
   - in `data/trainers.csv`, change one trainer's `ai` column to your keyword
   - `./run.sh`, go and fight them
3. Then make it *yours*. Ideas: heal when below a third; switch strategy when the
   foe is faster; prefer status moves on the first turn; play it safe when it is the
   last Pokémon standing.
4. Write three sentences: what does your AI value, and when does it lose?

**Assessment:** they changed how the game plays without editing `Battle.java`. Ask
them to say why that was possible. That answer is the learning objective.

## Errors to expect

- Forgetting `@Override` (optional but it catches typos — insist on it).
- Calling an abstract class's constructor.
- Overriding with a different parameter list (that is overloading, and the original
  is still being called — a genuinely confusing bug worth showing).
- Returning a move with no PP. The exercise checks this; the real game would
  Struggle forever.

## Exit ticket

1. Why is `Trainer` abstract?
2. Give an "is-a" and a "has-a" from this project and justify each.
3. The battle engine has no `if` about AI type. What makes that possible, and what
   would the code look like without it?
