package pokemon.model;

/**
 * One attack a Pokemon can use. Loaded from {@code data/moves.csv}.
 *
 * <p>TEACHING NOTE (CS I/II - encapsulation):
 * Every field is {@code private final}. Outside code reads a move through getters
 * and can never change one, because a Move is shared by every Pokemon that knows
 * it. If Charizard could edit the power of Ember, every Charmander in the game
 * would be affected. Encapsulation is not busywork - it prevents entire families
 * of bugs.
 */
public class Move {

    private final String id;
    private final String name;
    private final Type type;
    private final Category category;
    private final int power;
    private final int accuracy;
    private final int maxPp;
    private final int priority;
    private final MoveEffect effect;
    private final int effectChance;
    private final String description;

    public Move(String id, String name, Type type, Category category, int power, int accuracy,
                int maxPp, int priority, MoveEffect effect, int effectChance, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
        this.accuracy = accuracy;
        this.maxPp = maxPp;
        this.priority = priority;
        this.effect = effect;
        this.effectChance = effectChance;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getMaxPp() {
        return maxPp;
    }

    public int getPriority() {
        return priority;
    }

    public MoveEffect getEffect() {
        return effect;
    }

    public int getEffectChance() {
        return effectChance;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDamaging() {
        return category != Category.STATUS && power > 0;
    }

    /** Accuracy of 0 in the data file means "never misses". */
    public boolean neverMisses() {
        return accuracy <= 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
