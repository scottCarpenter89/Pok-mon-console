package pokemon.exercises;

import pokemon.battle.BattleAI;
import pokemon.model.Category;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.util.RandomSource;

/**
 * EXERCISE 18 (CS II/III) - WRITE YOUR OWN OPPONENT.
 *
 * <p>This class already implements {@link BattleAI}, so the battle engine will
 * happily use it. Right now it is terrible: it always picks the first move.
 *
 * <p>YOUR JOB: make {@link #chooseMove} follow these rules, in order.
 * <ol>
 *   <li>Never choose a move with no PP left ({@code slot.hasPp()}).</li>
 *   <li>Among the moves left, choose the DAMAGING move with the highest power
 *       ({@code slot.getMove().getPower()} and {@code getCategory() != Category.STATUS}).</li>
 *   <li>If there is no damaging move with PP, choose any move that still has PP.</li>
 *   <li>If nothing at all has PP, return {@code null} - the engine then makes the
 *       Pokemon Struggle.</li>
 * </ol>
 *
 * <p>When it passes, put it in a real battle. Open {@code data/trainers.csv}, add
 * your class to {@code BattleAI.byName()} in {@code src/pokemon/battle/BattleAI.java},
 * and give a trainer your AI. You will have changed the behaviour of the game
 * without touching the battle engine at all - that is what an interface buys you.
 */
public class MyFirstAI implements BattleAI {

    @Override
    public String name() {
        return "MyFirst";
    }

    @Override
    public MoveSlot chooseMove(Pokemon self, Pokemon foe, RandomSource rng) {
        // TODO exercise 18 - replace this with the four rules above.
        if (self.getMoves().isEmpty()) {
            return null;
        }
        return self.getMoves().get(0);
    }

    /** Kept so the import is used even before you start. Delete it if you like. */
    @SuppressWarnings("unused")
    private boolean isDamaging(MoveSlot slot) {
        return slot.getMove().getCategory() != Category.STATUS;
    }
}
