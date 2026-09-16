package pokemon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A kind of Pokemon - "Charmander" - as opposed to an individual Charmander.
 *
 * <p>TEACHING NOTE (CS II - object modelling):
 * There is exactly ONE Species object for Charmander in memory, and every
 * Charmander a player catches points at it. Drawing that on the board (many arrows
 * pointing at one box) is the clearest way to explain the difference between a
 * reference and an object.
 */
public class Species {

    private final int dexNumber;
    private final String id;
    private final String name;
    private final Type primaryType;
    private final Type secondaryType;   // null for single-typed species
    private final Stats baseStats;
    private final int catchRate;        // 3 (very hard) .. 255 (very easy)
    private final int baseExperience;
    private final String dexEntry;

    /** Filled in by the data loader after every species exists. */
    private Species evolvesInto;
    private int evolutionLevel;

    /** Moves this species learns, sorted by level. */
    private final List<LevelUpMove> learnset = new ArrayList<>();

    public Species(int dexNumber, String id, String name, Type primaryType, Type secondaryType,
                   Stats baseStats, int catchRate, int baseExperience, String dexEntry) {
        this.dexNumber = dexNumber;
        this.id = id;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.baseStats = baseStats;
        this.catchRate = catchRate;
        this.baseExperience = baseExperience;
        this.dexEntry = dexEntry;
    }

    /** A move paired with the level at which it is learned. */
    public record LevelUpMove(int level, Move move) {
    }

    public void addLearnsetEntry(int level, Move move) {
        learnset.add(new LevelUpMove(level, move));
        learnset.sort((a, b) -> Integer.compare(a.level(), b.level()));
    }

    public List<LevelUpMove> getLearnset() {
        return learnset;
    }

    /** Every move this species knows by the time it reaches {@code level}. */
    public List<Move> movesKnownAt(int level) {
        List<Move> moves = new ArrayList<>();
        for (LevelUpMove entry : learnset) {
            if (entry.level() <= level && !moves.contains(entry.move())) {
                moves.add(entry.move());
            }
        }
        return moves;
    }

    /** Moves learned exactly at this level (usually zero or one). */
    public List<Move> movesLearnedAt(int level) {
        List<Move> moves = new ArrayList<>();
        for (LevelUpMove entry : learnset) {
            if (entry.level() == level) {
                moves.add(entry.move());
            }
        }
        return moves;
    }

    public void setEvolution(Species evolvesInto, int evolutionLevel) {
        this.evolvesInto = evolvesInto;
        this.evolutionLevel = evolutionLevel;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getPrimaryType() {
        return primaryType;
    }

    public Type getSecondaryType() {
        return secondaryType;
    }

    public Stats getBaseStats() {
        return baseStats;
    }

    public int getCatchRate() {
        return catchRate;
    }

    public int getBaseExperience() {
        return baseExperience;
    }

    public String getDexEntry() {
        return dexEntry;
    }

    public Species getEvolvesInto() {
        return evolvesInto;
    }

    public int getEvolutionLevel() {
        return evolutionLevel;
    }

    /** "Fire" or "Grass/Poison" */
    public String typeLine() {
        if (secondaryType == null) {
            return primaryType.displayName();
        }
        return primaryType.displayName() + "/" + secondaryType.displayName();
    }

    @Override
    public String toString() {
        return name;
    }
}
