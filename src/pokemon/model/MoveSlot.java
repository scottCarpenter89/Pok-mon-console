package pokemon.model;

/**
 * One of the four move slots on an individual Pokemon: the move plus its
 * remaining Power Points.
 *
 * <p>TEACHING NOTE (CS I/II - shared vs. per-object data):
 * The {@link Move} is shared data ("what Ember is"). The PP counter is per-object
 * data ("how many times THIS Charmander can still use Ember"). Separating the two
 * is the single most useful modelling decision in this whole codebase, and it is a
 * great whiteboard discussion before students write any code.
 */
public class MoveSlot {

    private final Move move;
    private int currentPp;

    public MoveSlot(Move move) {
        this.move = move;
        this.currentPp = move.getMaxPp();
    }

    public MoveSlot(Move move, int currentPp) {
        this.move = move;
        this.currentPp = Math.max(0, Math.min(currentPp, move.getMaxPp()));
    }

    public Move getMove() {
        return move;
    }

    public int getCurrentPp() {
        return currentPp;
    }

    public int getMaxPp() {
        return move.getMaxPp();
    }

    public boolean hasPp() {
        return currentPp > 0;
    }

    public void spendPp() {
        if (currentPp > 0) {
            currentPp--;
        }
    }

    public void restore() {
        currentPp = move.getMaxPp();
    }

    @Override
    public String toString() {
        return move.getName() + " (" + currentPp + "/" + getMaxPp() + ")";
    }
}
