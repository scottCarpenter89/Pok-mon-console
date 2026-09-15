package pokemon.battle;

import pokemon.model.Item;
import pokemon.model.Pokemon;
import pokemon.model.Status;
import pokemon.util.RandomSource;

/**
 * Deciding whether a thrown Poke Ball works.
 *
 * <p>TEACHING NOTE (CS I - compound expressions; CS II - algorithm design):
 * The catch rate combines four separate ideas into one number: how hurt the target
 * is, how rare the species is, how good the ball is, and whether the target is
 * asleep or paralysed. Have students predict the effect of each factor BEFORE
 * reading the code, then check their predictions with
 * {@link #catchValue(Pokemon, Item)}.
 */
public final class CatchCalculator {

    /** How many times the ball wobbles before it clicks. */
    public static final int SHAKES_REQUIRED = 4;

    private CatchCalculator() {
    }

    /**
     * The raw catch value, 0-255. Bigger is easier.
     *
     * <pre>
     *   value = (3*maxHP - 2*currentHP) * catchRate * ballBonus * statusBonus / (3*maxHP)
     * </pre>
     */
    public static int catchValue(Pokemon target, Item ball) {
        int maxHp = target.getMaxHp();
        int currentHp = Math.max(1, target.getCurrentHp());

        double hpFactor = (3.0 * maxHp - 2.0 * currentHp) / (3.0 * maxHp);
        double value = hpFactor * target.getSpecies().getCatchRate()
                * ball.ballBonus() * statusBonus(target.getStatus());

        return (int) Math.max(1, Math.min(255, value));
    }

    /** Sleep and freeze help the most; poison, burn and paralysis help a little. */
    public static double statusBonus(Status status) {
        switch (status) {
            case SLEEP:
            case FROZEN:
                return 2.0;
            case POISON:
            case BURN:
            case PARALYSIS:
                return 1.5;
            default:
                return 1.0;
        }
    }

    /**
     * Throws the ball.
     *
     * @return how many times the ball shook, 0-4. Four means the Pokemon is caught.
     */
    public static int attemptCatch(Pokemon target, Item ball, RandomSource rng) {
        int value = catchValue(target, ball);
        int shakes = 0;
        for (int i = 0; i < SHAKES_REQUIRED; i++) {
            if (rng.nextInt(256) < value) {
                shakes++;
            } else {
                break;      // The ball popped open early.
            }
        }
        return shakes;
    }

    public static String shakeMessage(int shakes) {
        switch (shakes) {
            case 0: return "Oh no! The Pokemon broke free!";
            case 1: return "Aww! It appeared to be caught!";
            case 2: return "Aargh! Almost had it!";
            case 3: return "Shoot! It was so close, too!";
            default: return "Gotcha!";
        }
    }
}
