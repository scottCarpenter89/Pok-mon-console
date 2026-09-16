package pokemon.util;

/**
 * A fake random source that returns numbers you choose, in order.
 *
 * <p>TEACHING NOTE (CS III - testing / test doubles):
 * This class is a "stub" (sometimes called a fake or a test double). Tests use it
 * so that "random" events become 100% predictable:
 *
 * <pre>{@code
 * RandomSource rng = new FixedRandom(0, 99);  // first call returns 0, then 99
 * }</pre>
 *
 * Without something like this you cannot write a test for a critical hit, because
 * you can never be sure a critical hit will happen.
 */
public class FixedRandom implements RandomSource {

    private final int[] values;
    private int index = 0;

    public FixedRandom(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("FixedRandom needs at least one value");
        }
        this.values = values;
    }

    @Override
    public int nextInt(int bound) {
        int value = values[index % values.length];
        index++;
        return Math.floorMod(value, Math.max(1, bound));
    }
}
