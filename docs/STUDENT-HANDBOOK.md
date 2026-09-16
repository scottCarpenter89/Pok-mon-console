# Student handbook

You are about to learn Java by reading and changing a game that already works. That
is how almost all professional programming actually happens — you are handed a
codebase someone else wrote, and you make it do something new.

---

## Getting it running

Open a terminal in the project folder.

| | Mac / Linux | Windows |
|---|---|---|
| Play | `./run.sh` | `run.bat` |
| Check your exercises | `./exercises.sh` | `exercises.bat` |
| Run the tests | `./test.sh` | `test.bat` |

If you want to see what those scripts actually do, here it is, in full:

```bash
javac -d out $(find src -name '*.java')     # compile every .java file into out/
java -cp out pokemon.Main                   # run the class with the main method
```

That is the whole build. No magic.

**Useful flags:**

- `./run.sh --seed 12345` — the same "random" adventure every time. Essential when
  you are hunting a bug: if you cannot reproduce it, you cannot fix it.
- `./run.sh --fast` — no dramatic pauses.

---

## Your first hour

1. **Play it.** Ten minutes. Catch something. Lose a battle on purpose.
2. **Visit the Trainer School** from the main menu. Lesson 1 and Lesson 4.
3. **Change the game without writing Java.** Open `data/species.csv` and find the
   Magikarp row. Change its `hp` from 20 to 200. Save, run, and go find one. You have
   changed a program's behaviour on your first day.
4. **Put it back**, then open `src/pokemon/model/Type.java` and just read it. You are
   not expected to understand all of it yet.
5. **Run `./exercises.sh`.** Everything says `TODO`. That is your map.

---

## How to read code you did not write

You will never read a big program the way you read a book. Do this instead:

**Start from something you saw on screen.** You saw `It's super effective!` in a
battle. Search the project for that text:

```bash
grep -rn "super effective" src/
```

It is in `DamageCalculator.effectivenessMessage()`. Now you have a foothold, and you
can read *outwards* from there: who calls this method? What does it need?

**Follow one thing at a time.** Pick "what happens when I press 1 for FIGHT?" and
follow only that path: `Battle.choosePlayerAction()` → `chooseMove()` → `attack()` →
`dealDamage()` → `DamageCalculator.computeDamage()`. Ignore everything else. You have
just read the most important 200 lines in the project.

**Trust the names, check the comments.** Comments marked `TEACHING NOTE` were written
for you. They say which concept a piece of code demonstrates and why it is written
that way.

**Use the traffic-light trick.** On a printout or on screen, mark each line green (I
get it), yellow (I can guess) or red (no idea). Ask about the red ones. Nobody
understands everything on the first pass — not even the people who wrote it.

---

## The exercise workbook

Open `src/pokemon/exercises/Exercises.java`. Every method has a `TODO`. Fill them in,
then run `./exercises.sh`:

```
  DONE    1.  totalBaseStats      (CS I - expressions)
  TODO    2.  healthPercent       (CS I - integer division)
         Multiply BEFORE you divide: current * 100 / max.
         15 of 20 HP
         expected: 75
         your answer: 0
```

It tells you which check failed, what it expected and what you produced. Use that
loop — write, run, read, fix — rather than staring at the screen hoping.

Exercise 18 is different: it is in `src/pokemon/exercises/MyFirstAI.java` and it asks
you to write a class that the *actual game* can use. When it passes, ask your teacher
how to put your AI on a real trainer.

---

## Five errors you will definitely hit

**`cannot find symbol`** — Java does not know that name. Usually a typo, a missing
import, or a variable declared inside a block you are now outside of. The error names
the line and the symbol; read it.

**`incompatible types: String cannot be converted to int`** — you promised one type
and delivered another. Look at what the method returns versus what you are storing.

**`NullPointerException`** — you called a method on something that is not there. The
message names it: *"Cannot invoke Pokemon.getStatus() because <x> is null"*. The
question is never "what is null" (Java told you) — it is "why was it never set?"

**`ArrayIndexOutOfBoundsException: Index 18 out of bounds for length 18`** — arrays
start at 0, so the last index is `length - 1`. Almost always a `<=` that should be
`<`.

**`StackOverflowError`** — a recursive method never stopped. Your base case is
missing or never true.

**Nothing is wrong, but the answer is 0** — integer division. `15 / 20` is `0` in
Java, not `0.75`. Multiply before you divide, or cast one side to `double`.

---

## Where everything lives

| If you want to… | Open |
|---|---|
| Change a creature's stats, types or evolution | `data/species.csv` |
| Change what a move does | `data/moves.csv` and `src/pokemon/model/MoveEffect.java` |
| Change who learns what | `data/learnsets.csv` |
| Add a route or a town | `data/areas.csv`, `encounters.csv`, `trainers.csv` |
| See how damage is worked out | `src/pokemon/battle/DamageCalculator.java` |
| See how a whole battle runs | `src/pokemon/battle/Battle.java` |
| See how the opponent thinks | `src/pokemon/battle/TypeSmartAI.java` |
| See how a Pokémon levels up | `src/pokemon/model/Pokemon.java` |
| See how files are read | `src/pokemon/data/DataLoader.java` |
| Change what the player sees | `src/pokemon/ui/ConsoleUI.java` |

---

## Glossary of the words your teacher will use

| Word | What it means here |
|---|---|
| **class** | A blueprint. `Pokemon.java` describes what every Pokémon has |
| **object** | One thing built from a blueprint. Your Charmander |
| **field** | A variable that belongs to an object (`level`, `currentHp`) |
| **method** | A named block of code you can call. `takeDamage(10)` |
| **parameter** | Information a method needs, in the brackets |
| **return value** | What a method hands back |
| **static** | Belongs to the class, not to any one object. `DamageCalculator` is all static |
| **encapsulation** | Keeping fields private and controlling access with methods |
| **inheritance** | One class building on another. `Player extends Trainer` |
| **override** | A subclass replacing a method it inherited |
| **polymorphism** | One variable type, several real classes behind it |
| **interface** | A contract: "you must have these methods". `BattleAI` |
| **collection** | An object that holds other objects. `List`, `Map` |
| **exception** | An error object thrown when something goes wrong |
| **recursion** | A method that calls itself, with a base case that stops it |
| **Big-O** | How the work grows as the data grows. O(n) vs O(log n) |
| **unit test** | A small automated check that one piece of code is correct |
| **refactor** | Improving code's structure without changing what it does |

---

## When you are stuck

1. **Read the error message.** All of it. Java's messages are better than their
   reputation and usually name the file and the line.
2. **Print things.** `System.out.println("level is " + level);` before the line that
   breaks. Delete them when you are done.
3. **Make it smaller.** Comment out half. Still broken? The bug is in the other half.
4. **Use `--seed`.** Make the bug happen the same way twice before you try to fix it.
5. **Explain it out loud** to a person, a wall or a rubber duck. You will often solve
   it mid-sentence. This is a real, named technique, and professionals use it daily.
6. **Then ask** — and bring the error message, the line number and what you have
   already tried. That is what a good bug report looks like at work, too.
