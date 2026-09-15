package pokemon.battle;

import pokemon.model.Category;
import pokemon.model.Move;
import pokemon.model.MoveEffect;
import pokemon.model.Pokemon;
import pokemon.model.Type;
import pokemon.util.RandomSource;

/**
 * The damage formula, and nothing else.
 *
 * <p>TEACHING NOTE (CS I - expressions; CS III - pure functions and testability):
 * Every method here is {@code static} and depends only on its parameters. There is
 * no screen output, no input, no saved state. That makes this the easiest class in
 * the project to test - see {@code test/pokemon/DamageCalculatorTest.java} - and it
 * is the class to point at when a student asks "why would I ever split my code into
 * small methods?".
 *
 * <p>The formula is a simplified version of the one the real games use:
 *
 * <pre>
 *   base = ((2 * level / 5 + 2) * power * attack / defense) / 50 + 2
 *   damage = base * STAB * type * critical * random
 * </pre>
 */
public final class DamageCalculator {

    /** Same-Type Attack Bonus: using a move that matches your own type. */
    public static final double STAB_MULTIPLIER = 1.5;
    public static final double CRITICAL_MULTIPLIER = 1.5;
    public static final int CRITICAL_CHANCE_PERCENT = 6;
    public static final int HIGH_CRITICAL_CHANCE_PERCENT = 20;

    private DamageCalculator() {
    }

    /**
     * The heart of the game, written with every input spelled out so it can be
     * tested with exact numbers.
     *
     * @param randomFactor between 0.85 and 1.00 - pass 1.0 for "maximum roll"
     * @return damage, never less than 1 for a damaging move
     */
    public static int computeDamage(int level, int power, int attackStat, int defenseStat,
                                    double stab, double typeMultiplier,
                                    boolean critical, double randomFactor) {
        if (power <= 0 || typeMultiplier == 0.0) {
            return 0;   // Status moves and immunities deal nothing.
        }
        int safeDefense = Math.max(1, defenseStat);

        // Integer arithmetic, in the same order the real games use it.
        int base = ((2 * level / 5 + 2) * power * attackStat / safeDefense) / 50 + 2;

        double total = base * stab * typeMultiplier * randomFactor;
        if (critical) {
            total *= CRITICAL_MULTIPLIER;
        }
        return Math.max(1, (int) total);
    }

    /** The convenient version the battle engine actually calls. */
    public static int computeDamage(Pokemon attacker, Pokemon defender, Move move,
                                    int attackStat, int defenseStat,
                                    boolean critical, double randomFactor) {
        return computeDamage(
                attacker.getLevel(),
                move.getPower(),
                attackStat,
                defenseStat,
                sameTypeAttackBonus(attacker, move),
                typeMultiplier(move, defender),
                critical,
                randomFactor);
    }

    /** 1.5 when the attacker shares a type with the move, otherwise 1.0. */
    public static double sameTypeAttackBonus(Pokemon attacker, Move move) {
        return attacker.hasType(move.getType()) ? STAB_MULTIPLIER : 1.0;
    }

    /** 0, 0.25, 0.5, 1, 2 or 4 depending on the defender's type(s). */
    public static double typeMultiplier(Move move, Pokemon defender) {
        return Type.effectiveness(
                move.getType(),
                defender.getSpecies().getPrimaryType(),
                defender.getSpecies().getSecondaryType());
    }

    /** Which stat a move attacks with. */
    public static boolean usesPhysicalStats(Move move) {
        return move.getCategory() == Category.PHYSICAL;
    }

    public static boolean rollCritical(Move move, RandomSource rng) {
        int chance = move.getEffect() == MoveEffect.HIGH_CRIT
                ? HIGH_CRITICAL_CHANCE_PERCENT
                : CRITICAL_CHANCE_PERCENT;
        return rng.chance(chance);
    }

    /** The 85%-100% damage roll that makes no two battles identical. */
    public static double rollRandomFactor(RandomSource rng) {
        return (85 + rng.nextInt(16)) / 100.0;
    }

    public static boolean rollHit(Move move, int accuracyStage, RandomSource rng) {
        if (move.neverMisses()) {
            return true;
        }
        double accuracy = move.getAccuracy() * StatStages.multiplier(accuracyStage);
        return rng.nextInt(100) < accuracy;
    }

    /** The message shown after a hit lands. */
    public static String effectivenessMessage(double multiplier) {
        if (multiplier == 0.0) {
            return "It has no effect...";
        }
        if (multiplier >= 4.0) {
            return "It's super effective! A devastating hit!";
        }
        if (multiplier > 1.0) {
            return "It's super effective!";
        }
        if (multiplier < 1.0) {
            return "It's not very effective...";
        }
        return "";
    }
}
