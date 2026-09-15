package pokemon.exercises;

import pokemon.battle.BattleAI;
import pokemon.model.Category;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.util.RandomSource;

/**
 * TEACHER'S COPY of exercise 18.
 *
 * <p>Note the shape: one pass to find the best damaging move, a fallback for when
 * there is none, and a {@code null} when there is nothing left at all. Students who
 * solve it with one loop and two variables have understood it; students who write
 * four nested ifs have not, even when the tests pass. That is worth a conversation.
 */
public class MyFirstAI implements BattleAI {

    @Override
    public String name() {
        return "MyFirst";
    }

    @Override
    public MoveSlot chooseMove(Pokemon self, Pokemon foe, RandomSource rng) {
        MoveSlot bestDamaging = null;
        MoveSlot anyUsable = null;

        for (MoveSlot slot : self.getMoves()) {
            if (!slot.hasPp()) {
                continue;                       // Rule 1: never choose an empty move.
            }
            if (anyUsable == null) {
                anyUsable = slot;               // Rule 3: remember a fallback.
            }
            if (slot.getMove().getCategory() == Category.STATUS) {
                continue;
            }
            if (bestDamaging == null
                    || slot.getMove().getPower() > bestDamaging.getMove().getPower()) {
                bestDamaging = slot;            // Rule 2: keep the strongest.
            }
        }
        if (bestDamaging != null) {
            return bestDamaging;
        }
        return anyUsable;                       // Rule 4: null when nothing has PP.
    }

    @SuppressWarnings("unused")
    private boolean isDamaging(MoveSlot slot) {
        return slot.getMove().getCategory() != Category.STATUS;
    }
}
