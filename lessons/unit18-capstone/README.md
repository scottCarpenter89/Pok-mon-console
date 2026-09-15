# Unit 18 — Capstone: retheme and extend

**Course:** Computer Science III · **Time:** 4–6 weeks
**TEKS strands:** Apply the software development life cycle; project management;
teamwork; create original solutions; present work to an audience; intellectual
property.

## The brief

> In teams of two or three, take this game and make it yours. Replace the world with
> a theme of your own, add at least one feature that required changing Java (not just
> data), test it, and present it.

The retheme is not decoration. It is the project's own answer to the trademark
problem raised in Unit 1: the engine is generic, the content is data, and swapping the
content makes the work publishable. Students discover that a good architecture is what
makes that swap a weekend instead of a rewrite.

## Milestones

| Week | Deliverable | What you are grading |
|---|---|---|
| 1 | **Design document** (2 pages) | Theme, 12+ creatures with a type scheme, the map, the feature, who does what |
| 2 | **Data complete** | All CSVs rethemed; `./test.sh` green; game playable end to end |
| 3 | **Feature built** | The Java change works; new tests pass |
| 4 | **Polish + rehearsal** | Balance, bug fixes, a five-minute demo rehearsed |
| 5 | **Demo day** | Present to the class (or to another class — better) |

## Feature ideas, by difficulty

**Approachable**
- A new move effect (FLINCH, CONFUSE, a two-turn charge) — `MoveEffect` + `Battle`
- Held items that trigger in battle
- A day/night cycle that changes encounter tables
- Difficulty levels that pick different AIs

**Solid**
- A new `BattleAI` that switches Pokémon when it is at a type disadvantage
- Double battles (two active per side) — a real change to `Battle`
- A storage-box interface with sorting and searching (Units 11 and 14 pay off)
- An achievements system, saved to the save file (Unit 12 pays off)

**Ambitious**
- Branching evolution (Eevee-style) — data model *and* engine
- A type-coverage analyser that tells the player which types their team cannot hit
- A "Fly" menu using BFS over the map (Unit 15 pays off)
- A GUI front end that replaces `ConsoleUI` and nothing else

## Requirements

1. All the tests still pass: `./test.sh` is green at every milestone.
2. At least **three new unit tests** covering the new feature, including one
   boundary or error case.
3. No game rules in `ui/`. No `System.out.println` in `model/`. The design review in
   Unit 16 told you why.
4. Every public method you write has a Javadoc comment.
5. The README is updated so a stranger can run your version.
6. If you used Git: one branch per feature and at least one merge.

## The demo (5 minutes per team)

1. Play it for 90 seconds. Show the theme.
2. Show the feature working.
3. Show **one design decision** and explain the alternative you rejected.
4. Show **one bug** you fixed and how you found it.
5. Take two questions.

Points 3 and 4 are where the computer science is. A team that demos a pretty game and
cannot explain a decision has not met the standard; a team with a plain game who
explains a genuine trade-off has.

## Rubric (100 points)

| Criterion | Pts | Evidence |
|---|---|---|
| It works | 25 | Compiles, runs, does what the design document promised |
| Tests | 20 | New tests exist, pass, and would catch a real regression |
| Design | 20 | Change is in the right layer; data stays in `data/` |
| Code quality | 15 | Naming, decomposition, Javadoc, no copy-paste |
| Process | 10 | Design document, milestones met, commit history |
| Presentation | 10 | Clear demo; explains a decision and a bug |

## Individual accountability in a team

Require a one-page individual reflection: what you built, what you struggled with,
what you would do differently, and an honest account of your teammates'
contributions. Cross-reference it with the commit history if you used Git. This is
also the artefact that lets you defend a differentiated grade to a parent.

## Where this goes next

Students who enjoyed this should hear, specifically, what comes after:
AP Computer Science A covers the same Java with more rigour; a community-college data
structures course covers Units 13–15 properly; and the skills that got them through
this project — reading someone else's code, changing it safely, and proving it still
works — are the actual day-to-day work of a software engineer.
