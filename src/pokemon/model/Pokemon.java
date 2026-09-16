package pokemon.model;

import java.util.ArrayList;
import java.util.List;
import pokemon.util.RandomSource;

/**
 * An individual Pokemon on a team: its level, its scars, its nickname.
 *
 * <p>This is the class students spend the most time reading, so it is worth
 * walking through in class line by line.
 *
 * <p>TEACHING NOTES:
 * <ul>
 *   <li>CS I - fields, constructors, getters, and arithmetic (the stat formulas).</li>
 *   <li>CS I - {@code if} statements and loops (see {@link #gainExperience(int)}).</li>
 *   <li>CS II - encapsulation: {@code currentHp} is private and can only change
 *       through {@link #takeDamage(int)} and {@link #heal(int)}, which clamp it
 *       between 0 and the maximum. A public field would let any line of code in
 *       the project set HP to -9999.</li>
 * </ul>
 */
public class Pokemon {

    public static final int MAX_LEVEL = 100;
    public static final int MAX_MOVES = 4;

    private final Species species;
    private String nickname;
    private int level;
    private int experience;

    /** Individual values: a small per-Pokemon bonus, 0-31, that makes each one unique. */
    private final Stats individualValues;

    private int currentHp;
    private Status status = Status.NONE;
    private int sleepTurnsLeft = 0;

    private final List<MoveSlot> moves = new ArrayList<>();

    public Pokemon(Species species, int level, Stats individualValues) {
        this.species = species;
        this.nickname = species.getName();
        this.level = Math.max(1, Math.min(level, MAX_LEVEL));
        this.individualValues = individualValues;
        this.experience = experienceNeededForLevel(this.level);
        this.currentHp = getMaxHp();
    }

    /**
     * Builds a wild or trainer-owned Pokemon: random IVs and the four most recent
     * moves it would know at this level.
     *
     * <p>TEACHING NOTE (CS II - static factory methods): a constructor must be
     * named after its class, so when you need two different ways to build an
     * object, a named static method like this one is clearer than a second
     * constructor with a confusing parameter list.
     */
    public static Pokemon create(Species species, int level, RandomSource rng) {
        Stats ivs = new Stats(
                rng.nextInt(32), rng.nextInt(32), rng.nextInt(32),
                rng.nextInt(32), rng.nextInt(32), rng.nextInt(32));
        Pokemon pokemon = new Pokemon(species, level, ivs);
        List<Move> known = species.movesKnownAt(level);
        // Keep the LAST four moves learned - the newest moves are usually the best.
        int start = Math.max(0, known.size() - MAX_MOVES);
        for (int i = start; i < known.size(); i++) {
            pokemon.moves.add(new MoveSlot(known.get(i)));
        }
        pokemon.currentHp = pokemon.getMaxHp();
        return pokemon;
    }

    // ------------------------------------------------------------------
    // Stat formulas - pure arithmetic, perfect for a CS I "expressions" lab
    // ------------------------------------------------------------------

    /**
     * HP = ((2 * base + IV) * level) / 100 + level + 10
     *
     * <p>TEACHING NOTE (CS I - integer division): every division here is INTEGER
     * division, which throws away the remainder. That is deliberate - it is how the
     * real games work - and it is a fantastic way to show students that
     * {@code 7 / 2 == 3} in Java.
     */
    public int getMaxHp() {
        int base = species.getBaseStats().hp();
        int iv = individualValues.hp();
        return ((2 * base + iv) * level) / 100 + level + 10;
    }

    /** Every other stat = ((2 * base + IV) * level) / 100 + 5 */
    public int getStat(Stat stat) {
        if (stat == Stat.HP) {
            return getMaxHp();
        }
        int base = species.getBaseStats().get(stat);
        int iv = individualValues.get(stat);
        int value = ((2 * base + iv) * level) / 100 + 5;
        // Burn cuts physical Attack in half; paralysis slows a Pokemon down.
        if (stat == Stat.ATTACK && status == Status.BURN) {
            value = value / 2;
        }
        if (stat == Stat.SPEED && status == Status.PARALYSIS) {
            value = value / 2;
        }
        return Math.max(1, value);
    }

    // ------------------------------------------------------------------
    // Experience and levelling
    // ------------------------------------------------------------------

    /** The "medium fast" growth curve: total experience needed = level cubed. */
    public static int experienceNeededForLevel(int level) {
        return level * level * level;
    }

    public int experienceToNextLevel() {
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return experienceNeededForLevel(level + 1) - experience;
    }

    /**
     * Adds experience and levels up as many times as the total allows.
     *
     * <p>TEACHING NOTE (CS I - while loops): students almost always write this with
     * an {@code if}, which breaks when a big battle grants two levels at once. Show
     * the bug, then fix it with a {@code while}. It is the clearest classroom
     * example of "loop, not branch" that exists.
     *
     * @return the moves learned along the way (may be empty)
     */
    public List<Move> gainExperience(int amount) {
        List<Move> learned = new ArrayList<>();
        if (level >= MAX_LEVEL) {
            return learned;
        }
        experience += amount;
        while (level < MAX_LEVEL && experience >= experienceNeededForLevel(level + 1)) {
            int hpBefore = getMaxHp();
            level++;
            // Growing up heals you by exactly as much as your maximum HP grew.
            currentHp += getMaxHp() - hpBefore;
            learned.addAll(species.movesLearnedAt(level));
        }
        return learned;
    }

    public boolean canEvolve() {
        return species.getEvolvesInto() != null
                && species.getEvolutionLevel() > 0
                && level >= species.getEvolutionLevel();
    }

    /**
     * @return the evolved Pokemon, or null if it cannot evolve. The returned object
     *         keeps this Pokemon's level, moves, nickname and damage.
     */
    public Pokemon evolve() {
        if (!canEvolve()) {
            return null;
        }
        Species next = species.getEvolvesInto();
        Pokemon evolved = new Pokemon(next, level, individualValues);
        boolean keptNickname = !nickname.equals(species.getName());
        evolved.nickname = keptNickname ? nickname : next.getName();
        evolved.experience = experience;
        evolved.status = status;
        evolved.moves.clear();
        evolved.moves.addAll(moves);
        // Scale the damage taken so evolving is never a free full heal.
        double hpRatio = (double) currentHp / getMaxHp();
        evolved.currentHp = Math.max(1, (int) Math.round(evolved.getMaxHp() * hpRatio));
        return evolved;
    }

    // ------------------------------------------------------------------
    // Health and status
    // ------------------------------------------------------------------

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isFainted() {
        return currentHp <= 0;
    }

    /** @return how much damage was actually dealt. */
    public int takeDamage(int amount) {
        int before = currentHp;
        currentHp = Math.max(0, currentHp - Math.max(0, amount));
        if (currentHp == 0) {
            status = Status.NONE;
            sleepTurnsLeft = 0;
        }
        return before - currentHp;
    }

    /** @return how much HP was actually restored. */
    public int heal(int amount) {
        if (isFainted()) {
            return 0;
        }
        int before = currentHp;
        currentHp = Math.min(getMaxHp(), currentHp + Math.max(0, amount));
        return currentHp - before;
    }

    /** Brings a fainted Pokemon back with a fraction of its HP. */
    public void revive(double fractionOfMaxHp) {
        if (!isFainted()) {
            return;
        }
        currentHp = Math.max(1, (int) Math.round(getMaxHp() * fractionOfMaxHp));
        status = Status.NONE;
    }

    /** Full heal, as at a Pokemon Center: HP, status and PP. */
    public void fullyRestore() {
        currentHp = getMaxHp();
        status = Status.NONE;
        sleepTurnsLeft = 0;
        for (MoveSlot slot : moves) {
            slot.restore();
        }
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Applies a status condition.
     *
     * @return false if it failed, because a Pokemon can only carry one condition
     *         at a time (and a fainted one carries none).
     */
    public boolean applyStatus(Status newStatus, RandomSource rng) {
        if (isFainted() || status != Status.NONE || newStatus == Status.NONE) {
            return false;
        }
        // Types that shrug off their own element.
        if (newStatus == Status.BURN && hasType(Type.FIRE)) {
            return false;
        }
        if (newStatus == Status.POISON && (hasType(Type.POISON) || hasType(Type.STEEL))) {
            return false;
        }
        status = newStatus;
        if (newStatus == Status.SLEEP) {
            sleepTurnsLeft = rng.between(1, 3);
        }
        return true;
    }

    public void clearStatus() {
        status = Status.NONE;
        sleepTurnsLeft = 0;
    }

    public int getSleepTurnsLeft() {
        return sleepTurnsLeft;
    }

    public void decrementSleep() {
        if (sleepTurnsLeft > 0) {
            sleepTurnsLeft--;
            if (sleepTurnsLeft == 0 && status == Status.SLEEP) {
                status = Status.NONE;
            }
        }
    }

    public boolean hasType(Type type) {
        return species.getPrimaryType() == type || species.getSecondaryType() == type;
    }

    // ------------------------------------------------------------------
    // Moves
    // ------------------------------------------------------------------

    public List<MoveSlot> getMoves() {
        return moves;
    }

    public boolean knows(Move move) {
        for (MoveSlot slot : moves) {
            if (slot.getMove().getId().equals(move.getId())) {
                return true;
            }
        }
        return false;
    }

    /** @return false when all four slots are full - the caller must then ask the player. */
    public boolean learnMove(Move move) {
        if (knows(move)) {
            return true;
        }
        if (moves.size() >= MAX_MOVES) {
            return false;
        }
        moves.add(new MoveSlot(move));
        return true;
    }

    public void replaceMove(int slotIndex, Move move) {
        moves.set(slotIndex, new MoveSlot(move));
    }

    /** True when every move is out of PP - the Pokemon must then use Struggle. */
    public boolean isOutOfPp() {
        for (MoveSlot slot : moves) {
            if (slot.hasPp()) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Simple accessors
    // ------------------------------------------------------------------

    public Species getSpecies() {
        return species;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = (nickname == null || nickname.isBlank()) ? species.getName() : nickname.trim();
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public Stats getIndividualValues() {
        return individualValues;
    }

    /** Used only by the save-file loader, which restores an exact saved state. */
    public void restoreState(int level, int experience, int currentHp, Status status) {
        this.level = Math.max(1, Math.min(level, MAX_LEVEL));
        this.experience = experience;
        this.currentHp = Math.max(0, Math.min(currentHp, getMaxHp()));
        this.status = status;
    }

    @Override
    public String toString() {
        return nickname + " Lv." + level;
    }
}
