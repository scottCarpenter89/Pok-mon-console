# TEKS alignment — Computer Science I, II and III

This document maps the Pokémon Console RPG project onto a Texas Computer Science
scope and sequence for **levels 1–3** (Computer Science I, II and III, 19 TAC
Chapter 126, Subchapter C).

---

## Read this first: about the citation codes

The strand names and topic coverage below are stable across the Computer Science
I–III TEKS, but **the sub-expectation letters differ between adoptions** (the 2012
adoption and the revised Technology Applications TEKS adopted in 2022 and phased in
for high school). Districts also publish their own scope-and-sequence numbering on
top of the state codes.

So this document does the part that requires professional judgement — *"this
expectation is met, here, by this evidence"* — and names each expectation **in
words**, with an empty column for you to paste your district's code into.

**To finish the alignment (about 20 minutes):** open your district's scope and
sequence, or the current TEKS text for CS I/II/III on the TEA site, and fill in the
`Code` column. Every row already tells you what to look for. Do this before
submitting the document to a curriculum office — do not submit the blank column, and
do not assume a code from another campus's document matches yours.

**Coverage key:** ✅ taught and assessed here · 🟡 partly here, needs a classroom
activity · ⬜ not covered by this project (listed honestly in the gaps section).

---

## At a glance

| Course | What the project gives you | Weeks of material |
|---|---|---|
| **Computer Science I** | Units 1–8: from running your first program to writing classes, with a playable game as the context the whole way | ~14–18 weeks |
| **Computer Science II** | Units 9–14: inheritance, interfaces, collections, file I/O, exceptions, recursion, searching and sorting | ~12–16 weeks |
| **Computer Science III** | Units 15–18: data structures, graphs, design patterns, testing, the software life cycle and a capstone | ~10–14 weeks |

The units are in `lessons/unit01` … `lessons/unit18`. Each one names its TEKS
strands, the files to read, a demo to run, exercises and an exit ticket.

---

## Computer Science I

### Strand: Creativity and innovation

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Create original solutions to problems using a programming language | Students add species, moves and routes to `data/`, then a feature of their own | 8, 18 | ✅ |
| | Use a systematic design process (plan → code → test → refine) | Every exercise follows write → `./exercises.sh` → fix; Unit 8 adds design-before-code | 1, 8 | ✅ |
| | Publish or present work for an audience | Unit 18 ends with a demo day; the retheme project is presented | 18 | 🟡 needs class time |

### Strand: Communication and collaboration

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Work in teams to solve a programming problem | Pair-programming protocol in `docs/TEACHER-GUIDE.md`; Unit 18 team capstone | 18 | 🟡 you run the teaming |
| | Communicate technical information to peers | Code-reading protocol (Unit 1) and the demo day (Unit 18) | 1, 18 | 🟡 |
| | Use appropriate digital tools to collaborate | Git workflow appendix in the teacher guide | 17 | 🟡 optional |

### Strand: Research and information fluency

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Use documentation and reference material to solve problems | Javadoc throughout; Unit 7 sends students to the Java API docs for `Math`, `String` | 7, 11 | ✅ |
| | Evaluate the accuracy of information from technical sources | Unit 14 compares a claimed Big-O against a measured comparison count | 14 | 🟡 |

### Strand: Critical thinking, problem solving, and decision making

This is the big one for CS I, and it is where most of the project lives.

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Use the problem-solving process: identify, plan, implement, test, refine | The exercise workbook's write–run–fix loop | all | ✅ |
| | Use flowcharts / pseudocode to plan a program | Unit 6 has students flowchart `Battle.runTurn()` before reading it | 6 | ✅ |
| | Declare and use variables with appropriate primitive data types | `Pokemon.java` fields; Trainer School lesson 1; Exercise 1 | 2 | ✅ |
| | Demonstrate an understanding of integer vs floating-point arithmetic | The stat formulas are integer division on purpose; Exercise 2 | 2, 3 | ✅ |
| | Use arithmetic, relational and logical operators correctly, including precedence | `DamageCalculator.computeDamage()`; Exercises 2–4 | 3, 4 | ✅ |
| | Use conditional statements (`if`, `if/else`, `switch`) | `DamageCalculator.effectivenessMessage()`, `Type.decode()`, `Battle.canMove()` | 4 | ✅ |
| | Use iterative statements (`for`, `while`, nested loops) | `Pokemon.gainExperience()` (while), `Type.decodeChart()` (nested for) | 5 | ✅ |
| | Create and traverse one-dimensional arrays | `Type.values()`; Exercise 6 | 6 | ✅ |
| | Create and traverse two-dimensional arrays | The 18×18 type chart in `Type.java` | 6 | ✅ |
| | Write methods with parameters and return values | `DamageCalculator`; Exercises 1–9 | 7 | ✅ |
| | Use string manipulation methods | `util/Text.java`; Exercise 5 builds the HP bar | 5, 7 | ✅ |
| | Trace program execution by hand | Unit 3 hand-traces the damage formula and checks against `--seed` output | 3, 5 | ✅ |
| | Identify and correct syntax, run-time and logic errors | Unit 8's deliberate-bug lab; `Battle.Side.active` carries a real bug story | 8 | ✅ |
| | Test a program with a set of test cases, including boundary values | `./exercises.sh` output; `PokemonTest` tests 0 HP and over-healing | 8, 17 | ✅ |
| | Use objects and call their methods | Every lesson from Unit 8 on | 8 | ✅ |
| | Define a class with fields, a constructor and methods | Unit 8: students write an `Item`-like class of their own | 8 | ✅ |

### Strand: Digital citizenship

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Discuss intellectual property, trademarks and fair use | Built in: the README's note on the Pokémon trademark is a real, live example — Unit 18 makes retheming the answer | 1, 18 | ✅ |
| | Follow acceptable-use and academic-integrity expectations | `solutions/` is the teacher's copy; the honour-code discussion in the teacher guide | 1 | 🟡 |
| | Discuss the social impact of computing and careers in the field | Teacher guide has a careers discussion tied to the roles in this project (engine, data, QA, design) | 17 | 🟡 |

### Strand: Technology operations and concepts

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Compile and execute a program from the command line | `build.sh` / `run.bat`; Unit 1 deliberately uses raw `javac` and `java` once | 1 | ✅ |
| | Use an IDE or editor effectively | Setup notes in `docs/STUDENT-HANDBOOK.md` | 1 | 🟡 |
| | Apply naming conventions, indentation and internal documentation | The whole codebase is the model; Unit 7 has a style-audit activity | 7 | ✅ |
| | Demonstrate an understanding of data representation (binary, character encoding) | ⬜ — see gaps | — | ⬜ |

---

## Computer Science II

### Strand: Critical thinking, problem solving, and decision making

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Design and implement classes with appropriate encapsulation | `Pokemon`, `Move`, `MoveSlot` — private fields, controlled mutators | 9 | ✅ |
| | Distinguish class variables from instance variables, and static from instance methods | `DamageCalculator` (all static) vs `Pokemon` (all instance); `Species` shared by many `Pokemon` | 9 | ✅ |
| | Implement inheritance and method overriding | `Trainer` → `Player` / `NpcTrainer`; `battleTitle()` is overridden | 10 | ✅ |
| | Use polymorphism to write code that works with several subclasses | `List<Trainer>` in the Trainer School lesson; the battle engine never type-checks | 10 | ✅ |
| | Implement and use interfaces / abstract classes | `BattleAI` with three implementations; `abstract class Trainer`; `Lesson` | 10 | ✅ |
| | Use composition ("has-a") and explain when to prefer it to inheritance | `NpcTrainer` HAS-A `BattleAI`; discussed explicitly in Unit 10 | 10 | ✅ |
| | Use `ArrayList` and other collections | Party lists, `Bag` (`LinkedHashMap`), `GameData` registries | 11 | ✅ |
| | Use a Map to store key–value pairs | `Bag`, `GameData`; Exercise 10 | 11 | ✅ |
| | Read from and write to external files | `DataLoader` (CSV in), `SaveManager` (save out and back in) | 12 | ✅ |
| | Parse and validate external input | `CsvRow.split()` is a hand-written state machine; `ConsoleUI.promptInt()` | 12 | ✅ |
| | Use exception handling, including try/catch/finally and try-with-resources | `DataLoader.readRows()`; `DataException`; `ExitGameException` | 12 | ✅ |
| | Create and use user-defined exception types | `DataException`, `ExitGameException` | 12 | ✅ |
| | Implement recursive methods with a correct base case | `RecursionLesson`; Exercises 12–13 | 13 | ✅ |
| | Compare recursive and iterative solutions to the same problem | Unit 13 writes `sumTo` both ways and times them | 13 | ✅ |
| | Implement linear and binary search | `SearchSortLesson`; Exercise 14 | 14 | ✅ |
| | Implement at least one sorting algorithm and trace it | Selection sort printed pass-by-pass in the lesson; Exercise 15 | 14 | ✅ |
| | Compare algorithm efficiency informally (Big-O) | Comparison-count tables in the lesson; Unit 14 measures them | 14 | ✅ |
| | Debug systematically using output, assertions or a debugger | Unit 12's corrupt-CSV lab; the deliberate bug in `Battle.Side` | 12, 17 | ✅ |
| | Document code with Javadoc-style comments | The whole codebase; Unit 9 requires Javadoc on student code | 9 | ✅ |
| | Develop a program of significant length as part of a team | Unit 18 capstone | 18 | 🟡 |

### Other CS II strands

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| Creativity | Extend an existing program with new functionality | Adding an AI class, a move effect, a route (Units 10, 12, 16) | 10–16 | ✅ |
| Collaboration | Use version control or a shared repository | Git appendix in the teacher guide | 17 | 🟡 optional |
| Research fluency | Use API documentation to find and apply an unfamiliar class | Unit 11 sends students to the `Map`, `Deque` and `List` docs | 11 | ✅ |
| Digital citizenship | Discuss licensing and attribution of source code | `LICENSE`, the trademark note, the retheme project | 18 | ✅ |
| Technology operations | Use a build process and understand the compile/run cycle | `build.sh`, `Makefile`; Unit 17 explains what `javac -d out` actually does | 1, 17 | ✅ |

---

## Computer Science III

| Code | Student expectation (in words) | Where it lives | Unit | Coverage |
|---|---|---|---|---|
| | Implement and use stacks and queues | `DataStructuresLesson`; `ArrayDeque` in `WorldMap` | 15 | ✅ |
| | Implement and use hash-based structures, and explain O(1) lookup | `GameData` registries; `Bag`; the lesson's HashMap demo | 15 | ✅ |
| | Represent and traverse a graph | `WorldMap` is a graph; `shortestPath` is breadth-first search | 15 | ✅ |
| | Implement a search over a non-linear structure | Exercises 16–17 (BFS and nearest-Center) | 15 | ✅ |
| | Analyse algorithms and justify a choice of data structure | Unit 15 asks why `Bag` is a Map and the party is a List | 15 | ✅ |
| | Apply software design patterns | Strategy (`BattleAI`), Factory (`TrainerTemplate`), MVC-style separation (`ui` vs `model`) | 16 | ✅ |
| | Apply the software development life cycle to a project | Unit 18 runs requirements → design → build → test → demo | 18 | ✅ |
| | Use project-management practices (roles, milestones, deadlines) | Capstone rubric and milestone schedule in the teacher guide | 18 | ✅ |
| | Design, write and run unit tests | `MiniTest`, 177 checks, and Unit 17 has students add their own | 17 | ✅ |
| | Practise defensive programming and input validation | `DataIntegrityTest`; `ConsoleUI.promptInt()`; `Game.currentArea()` fallback | 17 | ✅ |
| | Refactor code for readability and maintainability | Unit 16's refactoring lab on a deliberately ugly method | 16 | ✅ |
| | Evaluate and critique the design of an existing system | Unit 16's design review of this project, including its weaknesses | 16 | ✅ |
| | Read and modify a codebase written by someone else | The entire course | all | ✅ |
| | Use version control in a team project | Git appendix; capstone requires branches and a merge | 17, 18 | 🟡 depends on your lab |

---

## Honest gaps

No single project covers an entire three-course sequence. These expectations appear
in CS I–III scope and sequences and **are not** taught by this project. Plan them
separately:

| Topic | Why it is missing | Cheapest way to add it |
|---|---|---|
| Binary, hexadecimal and character encoding | The game never needs bit-level work | A one-week unit before Unit 2; then have students compute a type chart index in binary |
| Boolean algebra / logic gates / truth-table simplification | Only the practical side appears (the `&&`/`\|\|` truth table in lesson 2) | Standard worksheet unit; the `Battle.canMove()` conditions make good practice problems |
| Graphical user interfaces (Swing/JavaFX) | Deliberately console-only so it runs anywhere | Strong extension project: rewrite `ConsoleUI` as a GUI. Because nothing else prints, this genuinely works — that is the point of the design |
| Mobile or web development | Out of scope | Separate unit |
| Concurrency and threads | The game is single-threaded by design | Extension: animate the HP bar on a second thread |
| Databases and SQL | The project uses CSV files | Extension: replace `DataLoader` with JDBC and SQLite — the interface stays identical |
| Networking | Out of scope | Extension: two-player battles over sockets (ambitious, excellent) |
| Careers, ethics and social impact (in depth) | Discussion topics, not code | Teacher guide has prompts; pair with TEA-recommended resources |
| Number systems in data representation | See above | — |

---

## Evidence for an administrator

If someone asks "how do I know a student met this expectation?", the project gives
you four kinds of evidence, all of which are artefacts you can collect:

1. **Exercise workbook output** — `./exercises.sh` prints `DONE`/`TODO` per exercise,
   each tied to a unit and a concept. A screenshot is a gradeable artefact.
2. **Test suite output** — students who add a feature must add tests; `./test.sh`
   prints a pass/fail count.
3. **A working change to the game** — adding a species, an AI or a route is visible,
   demonstrable and hard to fake.
4. **The capstone** — design document, code, tests and a demo (rubric in the teacher
   guide).
