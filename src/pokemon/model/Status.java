package pokemon.model;

/**
 * A lasting condition on a Pokemon.
 *
 * <p>TEACHING NOTE (CS I - enums with fields): an enum constant can carry data.
 * Here each condition knows the short tag shown next to the HP bar, so the UI
 * never needs a chain of if-statements to figure out what to print.
 */
public enum Status {
    NONE("   "),
    POISON("PSN"),
    BURN("BRN"),
    PARALYSIS("PAR"),
    SLEEP("SLP"),
    FROZEN("FRZ");

    private final String tag;

    Status(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    /** True if this condition can stop a Pokemon from moving this turn. */
    public boolean blocksMovement() {
        return this == SLEEP || this == FROZEN || this == PARALYSIS;
    }
}
