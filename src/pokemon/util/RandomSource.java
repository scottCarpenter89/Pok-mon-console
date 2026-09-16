package pokemon.util;

import java.util.List;

/**
 * A tiny abstraction over "randomness".
 *
 * <p>TEACHING NOTE (CS II - abstraction / interfaces):
 * The game never calls {@code Math.random()} directly. Every random decision goes
 * through this interface. That single design choice buys us two things:
 * <ul>
 *   <li>Unit tests can plug in a <em>predictable</em> random source, so a test can
 *       assert an exact damage number.</li>
 *   <li>A student can run the whole game with a fixed seed and get the exact same
 *       adventure twice, which makes debugging possible.</li>
 * </ul>
 * This is the difference between "code that works" and "code you can test".
 */
public interface RandomSource {

    /** @return a value from 0 (inclusive) to {@code bound} (exclusive). */
    int nextInt(int bound);

    /** @return a value from {@code min} to {@code max}, both inclusive. */
    default int between(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max (" + max + ") < min (" + min + ")");
        }
        return min + nextInt(max - min + 1);
    }

    /** @return true roughly {@code percent} times out of 100. */
    default boolean chance(int percent) {
        return nextInt(100) < percent;
    }

    /** @return a random element of the list. */
    default <T> T pick(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from an empty list");
        }
        return items.get(nextInt(items.size()));
    }
}
