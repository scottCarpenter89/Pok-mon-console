# Example files

## `demo-savegame.txt`

A hand-written save with a level-14 party of six, two Pokémon in the storage box,
$3,000 and one gym trainer already defeated.

**To use it:**

```bash
cp examples/demo-savegame.txt savegame.txt
./run.sh          # choose "Continue"
```

**Why it is here:**

- **Demos.** Starting a lesson at level 14 with a full party means you can show
  switching, the storage box, type matchups and a real trainer fight in five minutes
  instead of forty.
- **Unit 12 (file I/O).** It is proof that the save file is honest text. Open it in
  any editor, read it alongside `SaveManager.encode()`, and the serialization lesson
  writes itself.
- **A safe thing to break.** Ask students to corrupt it on purpose — delete a `|`,
  change `NONE` to `BANANA`, remove the moves from a line — and predict what the
  loader will do before they run it. (`SaveManager` defaults an unreadable status to
  `NONE` and re-teaches a move-less Pokémon its level-up moves, so some corruptions
  are survived and some are not. Working out which is the exercise.)

Delete `savegame.txt` when you are done; the game will offer a new game again. It is
listed in `.gitignore`, so your own playthrough is never committed.
