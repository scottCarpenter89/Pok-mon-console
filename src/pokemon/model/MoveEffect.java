package pokemon.model;

/**
 * The extra thing a move does besides (or instead of) damage.
 *
 * <p>TEACHING NOTE (CS I - enums as a controlled vocabulary):
 * Every move in {@code data/moves.csv} names one of these. If a data file contains
 * a typo, {@link #parse(String)} fails loudly at start-up with a clear message
 * instead of silently doing nothing during a battle. "Fail fast, fail loudly" is a
 * professional habit worth teaching early.
 */
public enum MoveEffect {
    NONE,
    BURN,
    POISON,
    PARALYZE,
    SLEEP,
    FREEZE,
    ATTACK_UP,
    DEFENSE_UP,
    SPEED_UP,
    SP_ATTACK_UP,
    ATTACK_DOWN,
    DEFENSE_DOWN,
    SPEED_DOWN,
    HEAL_HALF,
    DRAIN_HALF,
    RECOIL_THIRD,
    HIGH_CRIT;

    public static MoveEffect parse(String raw) {
        String cleaned = raw == null || raw.isBlank() ? "NONE" : raw.trim().toUpperCase();
        for (MoveEffect effect : values()) {
            if (effect.name().equals(cleaned)) {
                return effect;
            }
        }
        throw new IllegalArgumentException("Unknown move effect: '" + raw + "'");
    }

    /** Status conditions this effect inflicts, or {@link Status#NONE}. */
    public Status inflictedStatus() {
        switch (this) {
            case BURN: return Status.BURN;
            case POISON: return Status.POISON;
            case PARALYZE: return Status.PARALYSIS;
            case SLEEP: return Status.SLEEP;
            case FREEZE: return Status.FROZEN;
            default: return Status.NONE;
        }
    }

    /** The stat this effect raises or lowers, or null if it changes no stat. */
    public Stat affectedStat() {
        switch (this) {
            case ATTACK_UP:
            case ATTACK_DOWN: return Stat.ATTACK;
            case DEFENSE_UP:
            case DEFENSE_DOWN: return Stat.DEFENSE;
            case SPEED_UP:
            case SPEED_DOWN: return Stat.SPEED;
            case SP_ATTACK_UP: return Stat.SP_ATTACK;
            default: return null;
        }
    }

    /** +1 for a boost, -1 for a drop, 0 if this effect does not change stats. */
    public int stageChange() {
        switch (this) {
            case ATTACK_UP:
            case DEFENSE_UP:
            case SPEED_UP:
            case SP_ATTACK_UP: return 1;
            case ATTACK_DOWN:
            case DEFENSE_DOWN:
            case SPEED_DOWN: return -1;
            default: return 0;
        }
    }
}
