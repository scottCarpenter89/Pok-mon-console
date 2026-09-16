# Unit 12 — File I/O and exceptions

**Course:** Computer Science II · **Time:** 5 class periods
**TEKS strands:** Critical thinking (read from and write to external files; parse and
validate input; exception handling including try-with-resources; user-defined
exceptions; systematic debugging).

## Objectives

1. Read a text file line by line and close it correctly.
2. Write objects out as text and read them back (serialization by hand).
3. Use `try`/`catch`, and explain checked vs unchecked exceptions.
4. Write a custom exception that carries useful context.
5. Validate external input and fail with a message that helps the person who caused
   the failure.

## Hook (15 minutes)

Corrupt `data/species.csv` — change a stat to `fourty` — and run the game:

```
The game data could not be loaded:
  data/species.csv line 14: column 'hp' should be a whole number but was "fourty"
Fix the file above and run the game again.
```

Then show what it *would* have said without the work in `CsvRow.getInt`:

```
Exception in thread "main" java.lang.NumberFormatException: For input string: "fourty"
```

Both stop the program. Only one tells you where to look. This unit is about being the
programmer who writes the first message.

## Direct instruction

**try-with-resources.**

```java
try (BufferedReader reader = Files.newBufferedReader(file, UTF_8)) {
    ...
} catch (IOException e) {
    throw new DataException("Could not read " + file + ": " + e.getMessage(), e);
}
```

The reader is closed automatically — even if an exception is thrown halfway through.
Show the old `finally` version once so they appreciate it.

**Wrapping exceptions.** `DataLoader` catches low-level `IOException` and
`NumberFormatException` and rethrows a `DataException` that names the file and line.
The original is passed as the `cause`, so nothing is lost.

**Checked vs unchecked.** `IOException` is checked: the compiler forces you to deal
with it. `DataException` extends `RuntimeException` (unchecked) because a broken data
file is not something a caller can recover from — it should stop the program with a
clear message. That is a design decision, and it is worth arguing about in class.

**Parsing.** `CsvRow.split()` is a small state machine with one `boolean inQuotes`.
That single flag is what lets a description contain a comma. Trace it by hand on
`4,charmander,"Fire, and ash",45`.

## Read

- `src/pokemon/data/DataLoader.java` — `readRows()` and any two `load*` methods.
- `src/pokemon/data/CsvRow.java` — `split()` and `getInt()`.
- `src/pokemon/data/SaveManager.java` — `encode()` and `decode()`, the two halves of
  serialization.
- `src/pokemon/data/DataException.java` — three lines that improve every error in the
  project.

## Lab: the corrupt-save lab (2 periods)

1. Play, save, then open `savegame.txt` in a text editor. Read it together — students
   can see their whole adventure as text.
2. Edit the money to `999999`. Reload. Discuss: why can they do this, and how would a
   real online game stop them? (Never trust the client.)
3. Now corrupt it properly: delete a `|` from a party line. Reload and read the error.
4. **Build:** add a new field to the save file — say, `playtimeMinutes` — end to end.
   Write it in `save()`, read it in `load()`, and check that an *old* save file
   without the field still loads (it should: unknown keys are ignored and missing ones
   get a default). That backwards-compatibility requirement is the real lesson.

## Exercises

No new workbook exercises. The lab *is* the assessment, and it is a better one.

## Errors to expect

- `FileNotFoundException` from a relative path and the wrong working directory.
- Catching `Exception` and doing nothing (`catch (Exception e) {}`). Ban it out loud;
  explain that a swallowed exception turns a crash into a mystery.
- `ArrayIndexOutOfBoundsException` after `split()` on a line with a missing field.
  This is why `decode()` checks `parts.length` first.

## Exit ticket

1. What does try-with-resources do that a plain `try` does not?
2. Why is `DataException` unchecked while `IOException` is checked?
3. A save file from last week is missing a field you added today. What should happen,
   and how did this project arrange it?
