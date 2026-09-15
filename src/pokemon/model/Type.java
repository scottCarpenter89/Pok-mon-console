package pokemon.model;

import pokemon.util.Text;

/**
 * The 18 elemental types, plus the type-effectiveness chart.
 *
 * <p>TEACHING NOTE (CS I - enumerations and 2D arrays):
 * An {@code enum} is a data type with a fixed list of legal values. Using
 * {@code Type.FIRE} instead of the string {@code "fire"} means a typo becomes a
 * <em>compiler</em> error instead of a bug you find three weeks later.
 *
 * <p>The chart itself is a classic <b>two-dimensional array</b>: the row is the
 * attacking type, the column is the defending type, and the cell is the damage
 * multiplier. 18 x 18 = 324 numbers stored in one variable.
 */
public enum Type {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND,
    FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY;

    /**
     * The chart, written compactly so a human can proofread it.
     * One row per attacking type, in the same order as the constants above.
     * Each character is the multiplier against the defending type in that column:
     * '0' = no effect, '1' = half damage, '2' = normal damage, '4' = double damage.
     */
    private static final String[] CHART_ROWS = {
        //NOR FIR WAT ELE GRA ICE FIG POI GRO FLY PSY BUG ROC GHO DRA DAR STE FAI
        "222222222222102212", // NORMAL
        "211244222224121242", // FIRE
        "241212224222421222", // WATER
        "224112220422221222", // ELECTRIC
        "214212214121421212", // GRASS
        "211241224422224212", // ICE
        "422224212111402441", // FIGHTING
        "222242211222112204", // POISON
        "242412242021422242", // GROUND
        "222142422224122212", // FLYING
        "222222442212222012", // PSYCHIC
        "212242112142212411", // BUG
        "242224121424222212", // ROCK
        "022222222242224122", // GHOST
        "222222222222224210", // DRAGON
        "222222122242224121", // DARK
        "211124222222422214", // STEEL
        "212222412222224412"  // FAIRY
    };

    /** The decoded chart. Built once, the first time the class is used. */
    private static final double[][] MULTIPLIERS = decodeChart();

    /**
     * Turns the compact text chart into real numbers.
     *
     * <p>TEACHING NOTE (CS I - nested loops): the outer loop walks the rows, the
     * inner loop walks the columns. This is THE pattern for every 2D array you
     * will ever touch.
     */
    private static double[][] decodeChart() {
        int count = values().length;
        double[][] table = new double[count][count];
        for (int attacker = 0; attacker < count; attacker++) {
            String row = CHART_ROWS[attacker];
            if (row.length() != count) {
                throw new IllegalStateException(
                        "Type chart row " + attacker + " has " + row.length()
                                + " entries but there are " + count + " types.");
            }
            for (int defender = 0; defender < count; defender++) {
                table[attacker][defender] = decode(row.charAt(defender));
            }
        }
        return table;
    }

    /**
     * TEACHING NOTE (CS I - switch statements): a switch is the clean way to map
     * one small set of values onto another.
     */
    private static double decode(char code) {
        switch (code) {
            case '0': return 0.0;   // immune
            case '1': return 0.5;   // not very effective
            case '2': return 1.0;   // normal
            case '4': return 2.0;   // super effective
            default:
                throw new IllegalStateException("Unknown chart code: " + code);
        }
    }

    /** Multiplier for one attacking type against one defending type. */
    public static double effectiveness(Type attacking, Type defending) {
        return MULTIPLIERS[attacking.ordinal()][defending.ordinal()];
    }

    /**
     * Multiplier against a Pokemon that may have two types.
     * A dual-typed defender multiplies both numbers together, which is how a
     * 4x weakness (2.0 * 2.0) happens.
     *
     * @param secondary may be null for single-typed Pokemon
     */
    public static double effectiveness(Type attacking, Type primary, Type secondary) {
        double multiplier = effectiveness(attacking, primary);
        if (secondary != null) {
            multiplier *= effectiveness(attacking, secondary);
        }
        return multiplier;
    }

    /** "Fire" instead of "FIRE" - used when printing to the player. */
    public String displayName() {
        return Text.titleCase(name());
    }

    /** Parses a type name from a data file, with a helpful error message. */
    public static Type parse(String raw) {
        String cleaned = raw.trim().toUpperCase();
        for (Type type : values()) {
            if (type.name().equals(cleaned)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: '" + raw + "'");
    }
}
