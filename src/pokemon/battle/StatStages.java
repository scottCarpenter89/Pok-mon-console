package pokemon.battle;

/**
 * Temporary stat boosts and drops inside a battle, from -6 to +6.
 *
 * <p>TEACHING NOTE (CS I - branching on a number line):
 * The multiplier is {@code (2 + stage) / 2} when the stage is positive and
 * {@code 2 / (2 - stage)} when it is negative. Working out why both halves are
 * needed - and why a single formula does not do the job - makes a nice five-minute
 * warm-up problem.
 */
public final class StatStages {

    public static final int MIN_STAGE = -6;
    public static final int MAX_STAGE = 6;

    private StatStages() {
    }

    public static double multiplier(int stage) {
        int clamped = clamp(stage);
        if (clamped >= 0) {
            return (2.0 + clamped) / 2.0;
        }
        return 2.0 / (2.0 - clamped);
    }

    public static int clamp(int stage) {
        return Math.max(MIN_STAGE, Math.min(MAX_STAGE, stage));
    }

    /** "rose sharply", "fell", ... for the battle log. */
    public static String changeWord(int change) {
        if (change >= 2) {
            return "rose sharply";
        }
        if (change == 1) {
            return "rose";
        }
        if (change == -1) {
            return "fell";
        }
        return "fell sharply";
    }
}
