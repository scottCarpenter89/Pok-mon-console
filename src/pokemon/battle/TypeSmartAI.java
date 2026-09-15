package pokemon.battle;

import pokemon.model.Category;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.model.Stat;
import pokemon.util.RandomSource;

/**
 * An opponent that actually thinks: it scores every move and uses the best one.
 *
 * <p>TEACHING NOTE (CS II/III - greedy algorithms):
 * This is a <b>greedy</b> algorithm. It picks whatever looks best THIS turn without
 * planning ahead. Greedy is the right first algorithm to teach because the pattern
 * - "loop over the options, keep the best one so far" - shows up everywhere, and
 * because its weakness (it never sets up for a future turn) is easy to demonstrate
 * in a live battle.
 */
public class TypeSmartAI implements BattleAI {

    @Override
    public String name() {
        return "Smart";
    }

    @Override
    public MoveSlot chooseMove(Pokemon self, Pokemon foe, RandomSource rng) {
        MoveSlot best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        // The classic "find the maximum" loop.
        for (MoveSlot slot : self.getMoves()) {
            if (!slot.hasPp()) {
                continue;
            }
            double score = scoreMove(self, foe, slot, rng);
            if (score > bestScore) {
                bestScore = score;
                best = slot;
            }
        }
        return best;
    }

    /**
     * How good a move looks. Subclasses override this to think differently -
     * see {@link CleverAI}.
     */
    protected double scoreMove(Pokemon self, Pokemon foe, MoveSlot slot, RandomSource rng) {
        var move = slot.getMove();
        if (move.getCategory() == Category.STATUS) {
            // Status moves are worth something, but the value has to be measured in
            // the same units as damage - a fraction of the target's health - or the
            // AI will happily use Harden while being knocked out.
            return foe.getMaxHp() * 0.2 + rng.nextInt(3);
        }
        int attackStat = move.getCategory() == Category.PHYSICAL
                ? self.getStat(Stat.ATTACK) : self.getStat(Stat.SP_ATTACK);
        int defenseStat = move.getCategory() == Category.PHYSICAL
                ? foe.getStat(Stat.DEFENSE) : foe.getStat(Stat.SP_DEFENSE);

        int estimate = DamageCalculator.computeDamage(
                self.getLevel(), move.getPower(), attackStat, defenseStat,
                DamageCalculator.sameTypeAttackBonus(self, move),
                DamageCalculator.typeMultiplier(move, foe),
                false, 0.925);

        // Factor in accuracy, then add a pinch of randomness so battles vary.
        double accuracy = move.neverMisses() ? 100 : move.getAccuracy();
        return estimate * (accuracy / 100.0) + rng.nextInt(4);
    }
}
