package pokemon.util;

import java.util.Random;

/**
 * The normal, "real" random source used when a student actually plays the game.
 *
 * <p>TEACHING NOTE (CS I - objects; CS II - implementing an interface):
 * {@code SeededRandom} <em>implements</em> {@link RandomSource}. It promises to
 * provide every method the interface lists. Notice how small the class is: all of
 * the convenience methods ({@code between}, {@code chance}, {@code pick}) were
 * written once as {@code default} methods in the interface.
 */
public class SeededRandom implements RandomSource {

    private final Random random;
    private final long seed;

    public SeededRandom(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    /** Creates a source seeded from the clock - a different game every time. */
    public SeededRandom() {
        this(System.nanoTime());
    }

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public long getSeed() {
        return seed;
    }
}
