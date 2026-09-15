package pokemon.battle;

import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.util.RandomSource;

/**
 * The brain of a computer-controlled trainer.
 *
 * <p>TEACHING NOTE (CS II - interfaces; CS III - the Strategy pattern):
 * An interface is a CONTRACT: "whoever you are, you must be able to choose a move."
 * The battle engine holds a {@code BattleAI} variable and never knows or cares
 * which class is really behind it. Three implementations ship with the game:
 * {@link RandomAI}, {@link TypeSmartAI} and {@link CleverAI}. Writing a fourth is
 * one of the best assignments in the course - the student never touches the
 * engine, only their own new class.
 */
public interface BattleAI {

    /** A short label shown in the trainer data files and in debugging output. */
    String name();

    /**
     * Picks which of the attacker's moves to use.
     *
     * @param self the AI's own Pokemon
     * @param foe  the player's Pokemon
     * @return the chosen slot, or null when every move is out of PP
     */
    MoveSlot chooseMove(Pokemon self, Pokemon foe, RandomSource rng);

    /** Looks up an AI by the name used in {@code data/trainers.csv}. */
    static BattleAI byName(String raw) {
        String cleaned = raw == null ? "" : raw.trim().toUpperCase();
        switch (cleaned) {
            case "RANDOM": return new RandomAI();
            case "SMART": return new TypeSmartAI();
            case "CLEVER": return new CleverAI();
            default:
                throw new IllegalArgumentException("Unknown AI type: '" + raw + "'");
        }
    }
}
