package pokemon;

import pokemon.battle.DamageCalculator;
import pokemon.battle.StatStages;

/**
 * Tests for the damage formula.
 *
 * <p>TEACHING NOTE (CS III): these tests are only possible because
 * {@code computeDamage} is a pure function - same inputs, same output, every time.
 * Ask students to imagine testing the formula if it were buried inside the battle
 * loop and depended on {@code Math.random()}.
 */
public class DamageCalculatorTest {

    public static void run() {
        MiniTest.section("Damage formula");

        // ((2*50/5 + 2) * 60 * 100 / 100) / 50 + 2  =  (22 * 60) / 50 + 2 = 26 + 2 = 28
        int plain = DamageCalculator.computeDamage(50, 60, 100, 100, 1.0, 1.0, false, 1.0);
        MiniTest.checkEquals("A known set of inputs gives a known answer", 28, plain);

        int stab = DamageCalculator.computeDamage(50, 60, 100, 100, 1.5, 1.0, false, 1.0);
        MiniTest.checkEquals("STAB multiplies damage by 1.5", 42, stab);

        int superEffective = DamageCalculator.computeDamage(50, 60, 100, 100, 1.0, 2.0, false, 1.0);
        MiniTest.checkEquals("A 2x type match doubles the damage", 56, superEffective);

        int critical = DamageCalculator.computeDamage(50, 60, 100, 100, 1.0, 1.0, true, 1.0);
        MiniTest.check("A critical hit does more than a normal hit", critical > plain);

        int lowRoll = DamageCalculator.computeDamage(50, 60, 100, 100, 1.0, 1.0, false, 0.85);
        MiniTest.check("The minimum roll does less than the maximum roll", lowRoll < plain);

        MiniTest.checkEquals("An immune defender takes no damage", 0,
                DamageCalculator.computeDamage(50, 60, 100, 100, 1.0, 0.0, false, 1.0));
        MiniTest.checkEquals("A status move (0 power) deals no damage", 0,
                DamageCalculator.computeDamage(50, 0, 100, 100, 1.0, 1.0, false, 1.0));

        int tiny = DamageCalculator.computeDamage(2, 10, 1, 999, 1.0, 0.5, false, 0.85);
        MiniTest.check("A damaging move always deals at least 1 HP", tiny >= 1);

        int strongAttacker = DamageCalculator.computeDamage(50, 60, 200, 100, 1.0, 1.0, false, 1.0);
        MiniTest.check("Doubling Attack roughly doubles damage",
                strongAttacker > plain * 3 / 2);

        int strongDefender = DamageCalculator.computeDamage(50, 60, 100, 200, 1.0, 1.0, false, 1.0);
        MiniTest.check("Doubling Defense roughly halves damage", strongDefender < plain);

        MiniTest.check("Defense of 0 does not divide by zero",
                DamageCalculator.computeDamage(50, 60, 100, 0, 1.0, 1.0, false, 1.0) > 0);

        MiniTest.section("Balance constants");

        // TEACHING NOTE (CS III): these are TUNING values, not logic. Pinning them in a
        // test does not stop you changing them - it stops you changing them BY ACCIDENT,
        // and it makes a deliberate change show up in the diff as "I meant this".
        // If you rebalance the game, update these numbers in the same commit.
        //
        // A REAL GOTCHA, found while writing this test: javac INLINES a
        // "static final" primitive at compile time, so a test compiled against the old
        // value keeps seeing the old value until the test is recompiled too. That is why
        // build.sh always rebuilds both source trees. It is also a nice demonstration
        // that "compiled" and "up to date" are not the same thing.
        MiniTest.checkEquals("Same-type attack bonus is 1.5x", 1.5,
                DamageCalculator.STAB_MULTIPLIER);
        MiniTest.checkEquals("A critical hit is 1.5x", 1.5,
                DamageCalculator.CRITICAL_MULTIPLIER);
        MiniTest.checkEquals("Critical hits happen 6% of the time", 6,
                DamageCalculator.CRITICAL_CHANCE_PERCENT);
        MiniTest.checkEquals("High-crit moves crit 20% of the time", 20,
                DamageCalculator.HIGH_CRITICAL_CHANCE_PERCENT);
        MiniTest.check("High-crit moves really are better",
                DamageCalculator.HIGH_CRITICAL_CHANCE_PERCENT
                        > DamageCalculator.CRITICAL_CHANCE_PERCENT);

        MiniTest.section("Stat stages");
        MiniTest.checkEquals("Stage 0 changes nothing", 1.0, StatStages.multiplier(0));
        MiniTest.checkEquals("Stage +2 doubles the stat", 2.0, StatStages.multiplier(2));
        MiniTest.checkEquals("Stage -2 halves the stat", 0.5, StatStages.multiplier(-2));
        MiniTest.checkEquals("Stages are capped at +6", StatStages.multiplier(6),
                StatStages.multiplier(99));
        MiniTest.checkEquals("Stages are capped at -6", StatStages.multiplier(-6),
                StatStages.multiplier(-99));
        MiniTest.check("A stat boost is always an improvement",
                StatStages.multiplier(1) > StatStages.multiplier(0));
    }
}
