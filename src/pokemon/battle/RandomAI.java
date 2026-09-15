package pokemon.battle;

import java.util.ArrayList;
import java.util.List;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.util.RandomSource;

/**
 * The simplest possible opponent: it picks a move at random.
 *
 * <p>Used for wild Pokemon and for beginner trainers. Reading this class first
 * makes {@link TypeSmartAI} feel like an obvious improvement rather than magic.
 */
public class RandomAI implements BattleAI {

    @Override
    public String name() {
        return "Random";
    }

    @Override
    public MoveSlot chooseMove(Pokemon self, Pokemon foe, RandomSource rng) {
        List<MoveSlot> usable = new ArrayList<>();
        for (MoveSlot slot : self.getMoves()) {
            if (slot.hasPp()) {
                usable.add(slot);
            }
        }
        if (usable.isEmpty()) {
            return null;
        }
        return rng.pick(usable);
    }
}
