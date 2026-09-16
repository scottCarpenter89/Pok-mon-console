package pokemon.model;

/**
 * A bundle of the six stat values.
 *
 * <p>TEACHING NOTE (CS II/III - records and immutability):
 * This is a Java <b>record</b>. One line gives us six private final fields, a
 * constructor, accessor methods ({@code stats.attack()}), {@code equals},
 * {@code hashCode} and {@code toString}. Records are for data that never changes
 * after it is created - which is exactly what a species' base stats are.
 *
 * <p>Compare this to the 60+ lines the same class would need if you wrote every
 * getter by hand. Ask students to write that version once, so they appreciate
 * what the record is doing for them.
 */
public record Stats(int hp, int attack, int defense, int spAttack, int spDefense, int speed) {

    /** Reads one stat by name - handy for loops over all six stats. */
    public int get(Stat stat) {
        switch (stat) {
            case HP: return hp;
            case ATTACK: return attack;
            case DEFENSE: return defense;
            case SP_ATTACK: return spAttack;
            case SP_DEFENSE: return spDefense;
            case SPEED: return speed;
            default: throw new IllegalArgumentException("Unhandled stat: " + stat);
        }
    }

    /** The sum of all six - a rough "how strong is this species" number. */
    public int total() {
        return hp + attack + defense + spAttack + spDefense + speed;
    }
}
