# Unit 16 — Design patterns and refactoring

**Course:** Computer Science III · **Time:** 5 class periods
**TEKS strands:** Apply software design patterns; refactor for readability and
maintainability; evaluate and critique the design of an existing system.

## Objectives

1. Name and recognise the Strategy and Factory patterns in working code.
2. Explain separation of concerns using this project's layers.
3. Refactor a method without changing its behaviour, using the tests as a safety net.
4. Critique a design, including its weaknesses, and propose a change with costs.

## Hook (10 minutes)

Ask: *"What would it take to give this game a graphical interface?"*

Answer: rewrite `ui/ConsoleUI.java`, and nothing else — because nothing in `model/`,
`battle/`, `world/` or `data/` prints a single character or reads the keyboard. Then
ask what it would take in a program where `System.out.println` is scattered
everywhere. That is separation of concerns, and it is worth real money.

## The patterns in this project

**Strategy** — `BattleAI`. The algorithm is an object you can swap. The battle engine
holds a `BattleAI` and never asks which one it is. Difficulty becomes a column in a
CSV file.

**Factory** — `TrainerTemplate.build()`. A template is a *recipe*, not a trainer.
Every battle builds a fresh, fully healed team. Without it you would have to remember
to heal a stored trainer after each fight — and `BattleFlowTest` has a test proving
two builds are independent, because that bug is so easy to write.

**Layers (MVC-ish)** —

| Layer | Package | Rule |
|---|---|---|
| Model | `model/`, `world/` | Knows the rules. No output at all |
| Logic | `battle/`, `data/` | Coordinates the model. Narrates through a UI it was handed, never `System.out` |
| View | `ui/` | Prints and reads. Knows no rules |
| Entry | `Main`, `Game` | Wires it all together |

**Abstraction for testability** — `RandomSource`. Real play uses `SeededRandom`; tests
use `FixedRandom` and get certainty. The damage formula is testable *because of an
interface*, which is the most concrete answer to "why do interfaces matter?" in the
whole course.

## Read

- `src/pokemon/battle/BattleAI.java` + the three implementations (Strategy).
- `src/pokemon/world/TrainerTemplate.java` (Factory).
- `src/pokemon/util/RandomSource.java`, `SeededRandom.java`, `FixedRandom.java`.
- `src/pokemon/ui/ConsoleUI.java` — notice what it does *not* contain.

## Lab 1: refactor with a safety net (one period)

Give students this working but unpleasant method and have them improve it:

```java
public static String describe(Pokemon p) {
    String s = "";
    s = s + p.getNickname() + " Lv" + p.getLevel() + " ";
    if (p.getCurrentHp() * 100 / p.getMaxHp() > 50) { s = s + "healthy"; }
    else if (p.getCurrentHp() * 100 / p.getMaxHp() > 20) { s = s + "hurt"; }
    else if (p.getCurrentHp() > 0) { s = s + "critical"; }
    else { s = s + "fainted"; }
    if (p.getStatus() != Status.NONE) { s = s + " (" + p.getStatus() + ")"; }
    return s;
}
```

Rules: behaviour must not change, and `./test.sh` must stay green. Targets: extract
the percentage calculation (it is computed twice), name the thresholds as constants,
use a `StringBuilder`, and consider whether the status-to-text mapping belongs on the
`Status` enum.

**The point:** refactoring is only safe because tests exist. Say that out loud.

## Lab 2: design critique (one period)

Split into teams. Each team critiques one part of this project and presents a
five-minute case. Real weaknesses to find — they are genuinely here:

- `Battle.java` is long. Should it be split? Into what? What breaks if you do?
- `Pokemon.getMoves()` returns the live internal list (Unit 9's leak).
- `Game.java` mixes menu rendering with game logic in places.
- `Species` and `Pokemon` both know about levels — is that duplication?
- The save format is plain text with no checksum. Should it be?
- `Text.pad()` silently truncates, which caused a real bug in the lesson menu.

Each team must state: the problem, the cost of leaving it, the cost of fixing it, and
a recommendation. "Leave it alone" is an acceptable recommendation *if they argue it*.

## Errors to expect

- Refactoring and behaviour-changing at the same time, then not knowing which change
  broke the tests. Teach: one thing at a time, tests after each.
- Inventing abstractions nobody needs (an interface with one implementation and no
  prospect of a second). Over-design is a real failure mode and this is the unit to
  name it.

## Exit ticket

1. Name the pattern that lets a CSV column choose an algorithm.
2. Why can this game get a GUI without touching the battle engine?
3. Give one design weakness of this project and say whether you would fix it.
