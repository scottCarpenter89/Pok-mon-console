package pokemon.battle;

import java.util.ArrayList;
import java.util.List;
import pokemon.model.Category;
import pokemon.model.Item;
import pokemon.model.ItemKind;
import pokemon.model.Move;
import pokemon.model.MoveEffect;
import pokemon.model.MoveSlot;
import pokemon.model.NpcTrainer;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.model.Status;
import pokemon.model.Trainer;
import pokemon.model.Type;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/**
 * Runs one battle from "A wild PIDGEY appeared!" to the last Pokemon standing.
 *
 * <p>This class is the project's <b>state machine</b> and its longest read. Suggested
 * way to teach it: give students {@link #runTurn()} on paper first and have them
 * label the four phases (choose, order, execute, end-of-turn) before they ever open
 * the file.
 *
 * <p>TEACHING NOTES:
 * <ul>
 *   <li>CS I - loops and branching: the whole battle is one {@code while} loop with
 *       decisions inside it.</li>
 *   <li>CS II - objects collaborating: Battle owns almost no data. It asks the
 *       {@link Pokemon}, the {@link BattleAI} and the {@link DamageCalculator} to do
 *       their jobs and it stitches the answers together.</li>
 *   <li>CS III - state machines and design: one method per phase keeps a 500-line
 *       process readable. Compare to writing it as one giant method.</li>
 * </ul>
 */
public class Battle {

    /** The move a Pokemon is forced to use when every other move is out of PP. */
    public static final Move STRUGGLE = new Move("struggle", "Struggle", Type.NORMAL,
            Category.PHYSICAL, 50, 0, 1, 0, MoveEffect.RECOIL_THIRD, 100,
            "A desperate last-resort attack that also hurts the user.");

    private final Player player;
    private final NpcTrainer opponent;
    private final ConsoleUI ui;
    private final RandomSource rng;
    private final boolean wild;

    private final Side playerSide;
    private final Side foeSide;

    private BattleResult result;
    private boolean finished;

    public Battle(Player player, NpcTrainer opponent, ConsoleUI ui, RandomSource rng) {
        this.player = player;
        this.opponent = opponent;
        this.ui = ui;
        this.rng = rng;
        this.wild = opponent.isWild();
        this.playerSide = new Side(player);
        this.foeSide = new Side(opponent);
    }

    /**
     * One side of the battlefield: whose Pokemon it is, plus the temporary stat
     * boosts that vanish when the battle ends.
     *
     * <p>TEACHING NOTE (CS II - where does data belong?): stat stages are NOT stored
     * on the Pokemon, because they are a fact about this battle, not about the
     * creature. Asking students "where should this variable live?" is one of the
     * most valuable design questions in the course.
     */
    private static class Side {
        final Trainer trainer;
        final int[] stages = new int[Stat.values().length];

        /**
         * The Pokemon currently on the field.
         *
         * <p>TEACHING NOTE (CS II - a bug worth showing students): the first version of
         * this class had no such field. It asked the Trainer for "the first Pokemon that
         * has not fainted", which seemed equivalent - until a Pokemon fainted, the
         * answer became null, and the battle crashed with a NullPointerException.
         * The field below fixes it: a Pokemon stays on the field until something takes
         * it off. "Derived data is not the same as remembered data" is a lesson better
         * learned from this five-line story than from a lecture.
         */
        private Pokemon active;

        Side(Trainer trainer) {
            this.trainer = trainer;
            this.active = trainer.getActivePokemon();
        }

        Pokemon active() {
            return active;
        }

        void setActive(Pokemon pokemon) {
            this.active = pokemon;
        }

        void resetStages() {
            for (int i = 0; i < stages.length; i++) {
                stages[i] = 0;
            }
        }
    }

    // ==================================================================
    // Main loop
    // ==================================================================

    /** Plays the battle to its end and reports how it finished. */
    public BattleResult start() {
        announceOpening();

        while (!finished) {
            showBattlefield();
            runTurn();
        }

        finishUp();
        return result;
    }

    private void announceOpening() {
        ui.header("BATTLE");
        if (wild) {
            Pokemon wildPokemon = foeSide.active();
            ui.message("A wild " + wildPokemon.getNickname().toUpperCase()
                    + " appeared! (Lv." + wildPokemon.getLevel() + ")");
            player.recordSeen(wildPokemon.getSpecies());
        } else {
            ui.message(opponent.battleTitle() + " wants to battle!");
            if (!opponent.getDialogueBefore().isEmpty()) {
                ui.message("\"" + opponent.getDialogueBefore() + "\"");
            }
            ui.message(opponent.battleTitle() + " sent out "
                    + foeSide.active().getNickname().toUpperCase() + "!");
            player.recordSeen(foeSide.active().getSpecies());
        }
        ui.message("Go! " + playerSide.active().getNickname().toUpperCase() + "!");
    }

    private void showBattlefield() {
        Pokemon mine = playerSide.active();
        Pokemon theirs = foeSide.active();
        ui.blank();
        List<String> lines = new ArrayList<>();
        lines.add("FOE  " + ui.pokemonLine(theirs) + "   " + theirs.getSpecies().typeLine());
        lines.add("");
        lines.add("YOU  " + ui.pokemonLine(mine) + "   " + mine.getSpecies().typeLine());
        ui.box(null, lines);
    }

    /** One full turn: choose, order, execute, clean up. */
    private void runTurn() {
        // --- Phase 1: both sides choose what to do -------------------
        Action playerAction = choosePlayerAction();
        if (playerAction == null) {
            return;     // The player backed out of a menu; ask again.
        }
        MoveSlot foeChoice = opponent.getAi().chooseMove(foeSide.active(), playerSide.active(), rng);

        // --- Phase 2 and 3: order, then execute ---------------------
        if (playerAction.kind != ActionKind.FIGHT) {
            // Items, switching and running all happen before the opponent attacks.
            boolean spendsTurn = performNonAttack(playerAction);
            if (finished) {
                return;
            }
            if (spendsTurn) {
                attack(foeSide, playerSide, foeChoice);
            }
        } else {
            boolean playerFirst = playerMovesFirst(playerAction.move, foeChoice);
            if (playerFirst) {
                attack(playerSide, foeSide, playerAction.move);
                if (!finished && foeSide.active() != null && !foeSide.active().isFainted()) {
                    attack(foeSide, playerSide, foeChoice);
                }
            } else {
                attack(foeSide, playerSide, foeChoice);
                if (!finished && playerSide.active() != null && !playerSide.active().isFainted()) {
                    attack(playerSide, foeSide, playerAction.move);
                }
            }
        }

        // --- Phase 4: end-of-turn effects ---------------------------
        if (!finished) {
            endOfTurn(playerSide);
        }
        if (!finished) {
            endOfTurn(foeSide);
        }
    }

    /**
     * Speed decides who moves first, but move priority beats speed.
     *
     * <p>TEACHING NOTE (CS I - compound conditions): three rules in one method -
     * priority first, then speed, then a coin flip for an exact tie.
     */
    private boolean playerMovesFirst(MoveSlot mine, MoveSlot theirs) {
        int myPriority = mine == null ? 0 : mine.getMove().getPriority();
        int theirPriority = theirs == null ? 0 : theirs.getMove().getPriority();
        if (myPriority != theirPriority) {
            return myPriority > theirPriority;
        }
        int mySpeed = effectiveStat(playerSide, Stat.SPEED);
        int theirSpeed = effectiveStat(foeSide, Stat.SPEED);
        if (mySpeed != theirSpeed) {
            return mySpeed > theirSpeed;
        }
        return rng.chance(50);
    }

    // ==================================================================
    // The player's menu
    // ==================================================================

    private enum ActionKind { FIGHT, ITEM, SWITCH, RUN }

    /** What one side decided to do this turn. */
    private static class Action {
        final ActionKind kind;
        final MoveSlot move;
        final Item item;
        final Pokemon target;

        Action(ActionKind kind, MoveSlot move, Item item, Pokemon target) {
            this.kind = kind;
            this.move = move;
            this.item = item;
            this.target = target;
        }
    }

    private Action choosePlayerAction() {
        Pokemon mine = playerSide.active();
        if (mine.isOutOfPp()) {
            ui.message(mine.getNickname() + " has no moves left and must Struggle!");
            return new Action(ActionKind.FIGHT, new MoveSlot(STRUGGLE, 1), null, null);
        }
        int choice = ui.menu("What will " + mine.getNickname().toUpperCase() + " do?",
                "FIGHT", "BAG", "POKEMON", wild ? "RUN" : "RUN (you can't flee a trainer!)");
        switch (choice) {
            case 0: {
                MoveSlot slot = chooseMove(mine);
                return slot == null ? null : new Action(ActionKind.FIGHT, slot, null, null);
            }
            case 1: {
                Action action = chooseItem();
                return action;
            }
            case 2: {
                Pokemon swap = promptSwitch(false);
                return swap == null ? null : new Action(ActionKind.SWITCH, null, null, swap);
            }
            default:
                return new Action(ActionKind.RUN, null, null, null);
        }
    }

    private MoveSlot chooseMove(Pokemon mine) {
        List<String> labels = new ArrayList<>();
        for (MoveSlot slot : mine.getMoves()) {
            Move move = slot.getMove();
            labels.add(Text.pad(move.getName(), 14)
                    + Text.pad(move.getType().displayName(), 10)
                    + Text.pad(move.getCategory().name().substring(0, 4), 6)
                    + "PP " + slot.getCurrentPp() + "/" + slot.getMaxPp()
                    + (move.isDamaging() ? "   pow " + move.getPower() : "   status"));
        }
        int index = ui.chooseFromList("Choose a move:", labels, "Back");
        if (index < 0) {
            return null;
        }
        MoveSlot slot = mine.getMoves().get(index);
        if (!slot.hasPp()) {
            ui.message("There's no PP left for that move!");
            return null;
        }
        return slot;
    }

    private Action chooseItem() {
        List<Item> usable = player.getBag().itemsUsableInBattle(wild);
        if (usable.isEmpty()) {
            ui.message("Your bag has nothing useful right now.");
            return null;
        }
        List<String> labels = new ArrayList<>();
        for (Item item : usable) {
            String note = item.getDescription();
            if (item.getKind() == ItemKind.BALL && foeSide.active() != null) {
                // Showing the real number turns every throw into a probability lesson:
                // weaken it, put it to sleep, watch the percentage move.
                note = "about " + CatchCalculator.catchChancePercent(foeSide.active(), item)
                        + "% on this target";
            }
            labels.add(Text.pad(item.getName(), 16) + "x" + player.getBag().countOf(item)
                    + "   " + note);
        }
        int index = ui.chooseFromList("Your bag:", labels, "Back");
        if (index < 0) {
            return null;
        }
        Item item = usable.get(index);

        if (item.getKind() == ItemKind.BALL) {
            return new Action(ActionKind.ITEM, null, item, foeSide.active());
        }
        Pokemon target = chooseTeamMember("Use " + item.getName() + " on whom?",
                item.getKind() == ItemKind.REVIVE);
        if (target == null) {
            return null;
        }
        return new Action(ActionKind.ITEM, null, item, target);
    }

    private Pokemon chooseTeamMember(String title, boolean faintedOnly) {
        List<Pokemon> options = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Pokemon pokemon : player.getParty()) {
            if (faintedOnly != pokemon.isFainted()) {
                continue;
            }
            options.add(pokemon);
            labels.add(ui.pokemonLine(pokemon));
        }
        if (options.isEmpty()) {
            ui.message("There's nobody that can be used on.");
            return null;
        }
        int index = ui.chooseFromList(title, labels, "Back");
        return index < 0 ? null : options.get(index);
    }

    /**
     * @param forced true when the active Pokemon has fainted and the player MUST pick
     * @return the Pokemon to send out, or null if the player cancelled
     */
    private Pokemon promptSwitch(boolean forced) {
        List<Pokemon> options = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Pokemon pokemon : player.getParty()) {
            if (pokemon.isFainted() || pokemon == playerSide.active()) {
                continue;
            }
            options.add(pokemon);
            labels.add(ui.pokemonLine(pokemon) + "  " + pokemon.getSpecies().typeLine());
        }
        if (options.isEmpty()) {
            if (!forced) {
                ui.message("There's nobody else able to battle!");
            }
            return null;
        }
        int index = ui.chooseFromList("Send out which Pokemon?", labels, forced ? null : "Back");
        return index < 0 ? null : options.get(index);
    }

    // ==================================================================
    // Non-attacking actions
    // ==================================================================

    /** @return true if the action used up the turn (so the opponent gets to attack). */
    private boolean performNonAttack(Action action) {
        switch (action.kind) {
            case SWITCH:
                ui.message("Come back, " + playerSide.active().getNickname().toUpperCase() + "!");
                player.sendOut(action.target);
                playerSide.setActive(action.target);
                playerSide.resetStages();
                ui.message("Go! " + action.target.getNickname().toUpperCase() + "!");
                return true;
            case ITEM:
                return useItem(action.item, action.target);
            case RUN:
                return attemptEscape();
            default:
                return true;
        }
    }

    private boolean useItem(Item item, Pokemon target) {
        switch (item.getKind()) {
            case BALL:
                return throwBall(item);
            case POTION: {
                if (target.isFainted()) {
                    ui.message("It won't have any effect on a fainted Pokemon.");
                    return false;
                }
                int healed = target.heal(item.getPower());
                if (healed == 0) {
                    ui.message(target.getNickname() + " is already at full health.");
                    return false;
                }
                player.getBag().useOne(item);
                ui.message("Used " + item.getName() + ". " + target.getNickname()
                        + " recovered " + healed + " HP!");
                return true;
            }
            case STATUS_HEAL: {
                if (target.getStatus() == Status.NONE) {
                    ui.message(target.getNickname() + " has no condition to cure.");
                    return false;
                }
                player.getBag().useOne(item);
                target.clearStatus();
                ui.message(target.getNickname() + " was cured!");
                return true;
            }
            case REVIVE: {
                if (!target.isFainted()) {
                    ui.message(target.getNickname() + " doesn't need reviving.");
                    return false;
                }
                player.getBag().useOne(item);
                target.revive(0.5);
                ui.message(target.getNickname() + " was revived!");
                return true;
            }
            default:
                ui.message("That can't be used here.");
                return false;
        }
    }

    private boolean throwBall(Item ball) {
        Pokemon target = foeSide.active();
        player.getBag().useOne(ball);
        ui.message("You threw a " + ball.getName() + "!");

        int shakes = CatchCalculator.attemptCatch(target, ball, rng);
        for (int i = 0; i < Math.min(shakes, CatchCalculator.SHAKES_REQUIRED - 1); i++) {
            ui.message("... the ball shakes ...");
        }
        ui.message(CatchCalculator.shakeMessage(shakes));

        if (shakes >= CatchCalculator.SHAKES_REQUIRED) {
            ui.message(target.getNickname().toUpperCase() + " was caught!");
            player.recordCaught(target.getSpecies());
            if (player.receive(target)) {
                ui.message(target.getNickname() + " joined your party.");
            } else {
                ui.message("Your party is full - " + target.getNickname()
                        + " was sent to the storage box.");
            }
            if (ui.confirm("Give " + target.getNickname() + " a nickname?")) {
                target.setNickname(ui.prompt("Nickname:"));
            }
            result = BattleResult.CAUGHT;
            finished = true;
            return true;
        }
        return true;
    }

    private boolean attemptEscape() {
        if (!wild) {
            ui.message("You can't run from a trainer battle!");
            return false;
        }
        int mySpeed = effectiveStat(playerSide, Stat.SPEED);
        int theirSpeed = effectiveStat(foeSide, Stat.SPEED);
        int chance = mySpeed > theirSpeed ? 90 : 45;
        if (rng.chance(chance)) {
            ui.message("Got away safely!");
            result = BattleResult.FLED;
            finished = true;
            return true;
        }
        ui.message("Couldn't get away!");
        return true;
    }

    // ==================================================================
    // Attacking
    // ==================================================================

    private void attack(Side attackerSide, Side defenderSide, MoveSlot slot) {
        Pokemon attacker = attackerSide.active();
        Pokemon defender = defenderSide.active();
        if (attacker == null || defender == null || attacker.isFainted()) {
            return;
        }
        if (slot == null) {
            slot = new MoveSlot(STRUGGLE, 1);
            ui.message(attacker.getNickname() + " has no moves left and struggles!");
        }
        if (!canMove(attacker)) {
            return;
        }

        Move move = slot.getMove();
        slot.spendPp();
        ui.message(attacker.getNickname().toUpperCase() + " used " + move.getName() + "!");

        if (!DamageCalculator.rollHit(move, 0, rng)) {
            ui.message("But it missed!");
            return;
        }

        if (move.isDamaging()) {
            dealDamage(attackerSide, defenderSide, move);
        } else {
            applyStatusMove(attackerSide, defenderSide, move);
        }

        // A secondary effect on a damaging move (like Ember's burn chance).
        if (move.isDamaging() && move.getEffect() != MoveEffect.NONE
                && !defender.isFainted() && rng.chance(move.getEffectChance())) {
            applySecondaryEffect(attackerSide, defenderSide, move);
        }

        checkFaints();
    }

    /** Sleep, paralysis and freeze can all stop a Pokemon before it acts. */
    private boolean canMove(Pokemon pokemon) {
        switch (pokemon.getStatus()) {
            case SLEEP:
                if (pokemon.getSleepTurnsLeft() <= 0) {
                    pokemon.clearStatus();
                    ui.message(pokemon.getNickname() + " woke up!");
                    return true;
                }
                pokemon.decrementSleep();
                ui.message(pokemon.getNickname() + " is fast asleep.");
                return false;
            case FROZEN:
                if (rng.chance(20)) {
                    pokemon.clearStatus();
                    ui.message(pokemon.getNickname() + " thawed out!");
                    return true;
                }
                ui.message(pokemon.getNickname() + " is frozen solid!");
                return false;
            case PARALYSIS:
                if (rng.chance(25)) {
                    ui.message(pokemon.getNickname() + " is paralysed! It can't move!");
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    private void dealDamage(Side attackerSide, Side defenderSide, Move move) {
        Pokemon attacker = attackerSide.active();
        Pokemon defender = defenderSide.active();

        boolean physical = DamageCalculator.usesPhysicalStats(move);
        int attackStat = effectiveStat(attackerSide, physical ? Stat.ATTACK : Stat.SP_ATTACK);
        int defenseStat = effectiveStat(defenderSide, physical ? Stat.DEFENSE : Stat.SP_DEFENSE);

        double multiplier = DamageCalculator.typeMultiplier(move, defender);
        if (multiplier == 0.0) {
            ui.message("It doesn't affect " + defender.getNickname().toUpperCase() + "...");
            return;
        }

        boolean critical = DamageCalculator.rollCritical(move, rng);
        double randomFactor = DamageCalculator.rollRandomFactor(rng);
        int damage = DamageCalculator.computeDamage(attacker, defender, move,
                attackStat, defenseStat, critical, randomFactor);

        int dealt = defender.takeDamage(damage);
        if (critical) {
            ui.message("A critical hit!");
        }
        String note = DamageCalculator.effectivenessMessage(multiplier);
        if (!note.isEmpty()) {
            ui.message(note);
        }
        ui.message(defender.getNickname().toUpperCase() + " lost " + dealt + " HP. ("
                + defender.getCurrentHp() + "/" + defender.getMaxHp() + " left)");

        // Moves that give back or cost HP.
        if (move.getEffect() == MoveEffect.DRAIN_HALF && !attacker.isFainted()) {
            int drained = attacker.heal(Math.max(1, dealt / 2));
            if (drained > 0) {
                ui.message(attacker.getNickname() + " drained " + drained + " HP!");
            }
        }
        if (move.getEffect() == MoveEffect.RECOIL_THIRD) {
            int recoil = Math.max(1, dealt / 3);
            attacker.takeDamage(recoil);
            ui.message(attacker.getNickname() + " is hit with " + recoil + " HP of recoil!");
        }
    }

    private void applyStatusMove(Side attackerSide, Side defenderSide, Move move) {
        Pokemon attacker = attackerSide.active();
        MoveEffect effect = move.getEffect();

        if (effect == MoveEffect.HEAL_HALF) {
            int healed = attacker.heal(attacker.getMaxHp() / 2);
            ui.message(healed > 0
                    ? attacker.getNickname() + " recovered " + healed + " HP!"
                    : "But it failed!");
            return;
        }
        applySecondaryEffect(attackerSide, defenderSide, move);
    }

    /** Status conditions and stat changes, whichever the move carries. */
    private void applySecondaryEffect(Side attackerSide, Side defenderSide, Move move) {
        MoveEffect effect = move.getEffect();
        Pokemon attacker = attackerSide.active();
        Pokemon defender = defenderSide.active();

        Status inflicted = effect.inflictedStatus();
        if (inflicted != Status.NONE) {
            if (defender.applyStatus(inflicted, rng)) {
                ui.message(defender.getNickname().toUpperCase() + " " + statusVerb(inflicted));
            } else if (move.getCategory() == Category.STATUS) {
                ui.message("But it failed!");
            }
            return;
        }

        int change = effect.stageChange();
        Stat stat = effect.affectedStat();
        if (change != 0 && stat != null) {
            boolean targetsSelf = change > 0;
            Side targetSide = targetsSelf ? attackerSide : defenderSide;
            Pokemon target = targetsSelf ? attacker : defender;
            int index = stat.ordinal();
            int before = targetSide.stages[index];
            targetSide.stages[index] = StatStages.clamp(before + change);
            if (targetSide.stages[index] == before) {
                ui.message(target.getNickname() + "'s " + stat.displayName()
                        + " won't go any " + (change > 0 ? "higher" : "lower") + "!");
            } else {
                ui.message(target.getNickname().toUpperCase() + "'s " + stat.displayName()
                        + " " + StatStages.changeWord(change) + "!");
            }
        }
    }

    private String statusVerb(Status status) {
        switch (status) {
            case BURN: return "was burned!";
            case POISON: return "was poisoned!";
            case PARALYSIS: return "is paralysed! It may be unable to move!";
            case SLEEP: return "fell asleep!";
            case FROZEN: return "was frozen solid!";
            default: return "is affected!";
        }
    }

    /** Applies a stat stage on top of the Pokemon's own stat value. */
    private int effectiveStat(Side side, Stat stat) {
        Pokemon pokemon = side.active();
        if (pokemon == null) {
            return 1;
        }
        double multiplier = StatStages.multiplier(side.stages[stat.ordinal()]);
        return Math.max(1, (int) (pokemon.getStat(stat) * multiplier));
    }

    // ==================================================================
    // End of turn and fainting
    // ==================================================================

    private void endOfTurn(Side side) {
        Pokemon pokemon = side.active();
        if (pokemon == null || pokemon.isFainted()) {
            return;
        }
        Status status = pokemon.getStatus();
        if (status == Status.POISON) {
            int damage = Math.max(1, pokemon.getMaxHp() / 8);
            pokemon.takeDamage(damage);
            ui.message(pokemon.getNickname() + " is hurt by poison! (-" + damage + ")");
        } else if (status == Status.BURN) {
            int damage = Math.max(1, pokemon.getMaxHp() / 16);
            pokemon.takeDamage(damage);
            ui.message(pokemon.getNickname() + " is hurt by its burn! (-" + damage + ")");
        }
        checkFaints();
    }

    private void checkFaints() {
        if (finished) {
            return;
        }
        Pokemon foe = foeSide.active();
        if (foe != null && foe.isFainted()) {
            ui.message(foe.getNickname().toUpperCase() + " fainted!");
            awardExperience(foe);
            foeSide.resetStages();
            if (!opponent.hasUsablePokemon()) {
                winBattle();
                return;
            }
            Pokemon next = opponent.getActivePokemon();
            foeSide.setActive(next);
            ui.message(opponent.battleTitle() + " sent out " + next.getNickname().toUpperCase() + "!");
            player.recordSeen(next.getSpecies());
        }

        Pokemon mine = playerSide.active();
        if (mine != null && mine.isFainted()) {
            ui.message(mine.getNickname().toUpperCase() + " fainted!");
            playerSide.resetStages();
            if (!player.hasUsablePokemon()) {
                loseBattle();
                return;
            }
            Pokemon next = promptSwitch(true);
            if (next != null) {
                player.sendOut(next);
                playerSide.setActive(next);
                ui.message("Go! " + next.getNickname().toUpperCase() + "!");
            }
        }
    }

    /**
     * Experience for knocking out a Pokemon.
     *
     * <pre>experience = baseExperience * foeLevel / 7, and 1.5x from a trainer</pre>
     */
    private void awardExperience(Pokemon defeated) {
        Pokemon winner = playerSide.active();
        if (winner == null || winner.isFainted()) {
            return;
        }
        int gained = defeated.getSpecies().getBaseExperience() * defeated.getLevel() / 7;
        if (!wild) {
            gained = gained * 3 / 2;
        }
        int levelBefore = winner.getLevel();
        List<pokemon.model.Move> learned = winner.gainExperience(gained);
        ui.message(winner.getNickname().toUpperCase() + " gained " + gained + " EXP!");

        if (winner.getLevel() > levelBefore) {
            ui.message(winner.getNickname().toUpperCase() + " grew to level "
                    + winner.getLevel() + "!");
        }
        for (pokemon.model.Move move : learned) {
            teachMove(winner, move);
        }
    }

    /** Learns a move, asking the player to drop one when all four slots are full. */
    private void teachMove(Pokemon pokemon, pokemon.model.Move move) {
        if (pokemon.knows(move)) {
            return;
        }
        if (pokemon.learnMove(move)) {
            ui.message(pokemon.getNickname().toUpperCase() + " learned " + move.getName() + "!");
            return;
        }
        ui.message(pokemon.getNickname() + " wants to learn " + move.getName()
                + ", but already knows four moves.");
        if (!ui.confirm("Forget a move to make room?")) {
            ui.message(pokemon.getNickname() + " did not learn " + move.getName() + ".");
            return;
        }
        List<String> labels = new ArrayList<>();
        for (MoveSlot slot : pokemon.getMoves()) {
            labels.add(slot.getMove().getName() + " (" + slot.getMove().getType().displayName()
                    + ", power " + slot.getMove().getPower() + ")");
        }
        int index = ui.chooseFromList("Forget which move?", labels, "Cancel");
        if (index < 0) {
            ui.message(pokemon.getNickname() + " did not learn " + move.getName() + ".");
            return;
        }
        String forgotten = pokemon.getMoves().get(index).getMove().getName();
        pokemon.replaceMove(index, move);
        ui.message(pokemon.getNickname() + " forgot " + forgotten
                + " and learned " + move.getName() + "!");
    }

    private void winBattle() {
        result = BattleResult.WON;
        finished = true;
        player.addWin();
        if (!wild) {
            ui.message("You defeated " + opponent.battleTitle() + "!");
            if (!opponent.getDialogueAfter().isEmpty()) {
                ui.message("\"" + opponent.getDialogueAfter() + "\"");
            }
            if (opponent.getRewardMoney() > 0) {
                player.earn(opponent.getRewardMoney());
                ui.message("You got $" + opponent.getRewardMoney() + " for winning!");
            }
            player.markDefeated(opponent.getId());
        }
    }

    private void loseBattle() {
        result = BattleResult.LOST;
        finished = true;
        ui.message("You have no Pokemon able to fight!");
    }

    /** Clears battle-only state and checks whether anyone is ready to evolve. */
    private void finishUp() {
        playerSide.resetStages();
        foeSide.resetStages();
        if (result == BattleResult.WON || result == BattleResult.CAUGHT) {
            checkEvolutions();
        }
    }

    private void checkEvolutions() {
        List<Pokemon> party = player.getParty();
        for (int i = 0; i < party.size(); i++) {
            Pokemon pokemon = party.get(i);
            if (!pokemon.canEvolve()) {
                continue;
            }
            Species into = pokemon.getSpecies().getEvolvesInto();
            ui.blank();
            ui.message("What? " + pokemon.getNickname().toUpperCase() + " is evolving!");
            if (!ui.confirm("Let it evolve into " + into.getName() + "?")) {
                ui.message(pokemon.getNickname() + " stopped evolving.");
                continue;
            }
            Pokemon evolved = pokemon.evolve();
            party.set(i, evolved);
            player.recordCaught(into);
            ui.message("Congratulations! Your " + pokemon.getSpecies().getName()
                    + " evolved into " + into.getName().toUpperCase() + "!");
        }
    }
}
