package pokemon.model;

/** The families of items the game understands. */
public enum ItemKind {
    /** Used in a wild battle to try to catch a Pokemon. */
    BALL,
    /** Restores HP. */
    POTION,
    /** Cures a status condition. */
    STATUS_HEAL,
    /** Brings a fainted Pokemon back. */
    REVIVE,
    /** Story items that are never consumed. */
    KEY;

    public static ItemKind parse(String raw) {
        return valueOf(raw.trim().toUpperCase());
    }
}
