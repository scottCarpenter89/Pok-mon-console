package pokemon.battle;

import pokemon.model.Category;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.model.Status;
import pokemon.util.RandomSource;

/**
 * A gym-leader brain: everything {@link TypeSmartAI} does, plus two extra ideas.
 *
 * <p>TEACHING NOTE (CS II - overriding and {@code super}):
 * {@link #scoreMove} calls {@code super.scoreMove(...)} first and then ADJUSTS the
 * result. That is the everyday shape of a good override - extend the parent's
 * behaviour instead of copying and pasting it. Change the parent's formula and
 * this class improves for free.
 */
public class CleverAI extends TypeSmartAI {

    @Override
    public String name() {
        return "Clever";
    }

    @Override
    protected double scoreMove(Pokemon self, Pokemon foe, MoveSlot slot, RandomSource rng) {
        double score = super.scoreMove(self, foe, slot, rng);
        var move = slot.getMove();

        // Idea 1: a status move is excellent early, and useless on a target that
        // already has a condition.
        if (move.getCategory() == Category.STATUS) {
            boolean foeHealthy = foe.getCurrentHp() > foe.getMaxHp() / 2;
            boolean foeUntouched = foe.getCurrentHp() > foe.getMaxHp() * 3 / 4;
            boolean foeClean = foe.getStatus() == Status.NONE;
            if (move.getEffect().inflictedStatus() != Status.NONE) {
                score = foeClean && foeHealthy ? score + foe.getMaxHp() * 0.5 : 0;
            } else if (move.getEffect().stageChange() > 0) {
                // Only worth a turn while the fight is still young.
                score = foeUntouched ? score + foe.getMaxHp() * 0.15 : 0;
            }
        }

        // Idea 2: if a move can finish the job this turn, take it.
        if (move.isDamaging() && score >= foe.getCurrentHp()) {
            score += foe.getMaxHp() * 2;
        }
        return score;
    }
}
