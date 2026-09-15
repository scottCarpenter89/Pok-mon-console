package pokemon;

/**
 * A 60-line unit-testing framework, written from scratch.
 *
 * <p>TEACHING NOTE (CS III - testing):
 * Professional Java projects use JUnit. This project deliberately does not, for two
 * reasons: the game must run on a school computer with nothing but a JDK installed,
 * and a framework you can read in one sitting demystifies the ones you cannot.
 * Every idea here has a JUnit equivalent:
 *
 * <pre>
 *   MiniTest.check(...)        -&gt;  assertTrue(...)
 *   MiniTest.checkEquals(...)  -&gt;  assertEquals(...)
 *   MiniTest.summary()         -&gt;  the green bar / red bar
 * </pre>
 */
public final class MiniTest {

    private static int passed = 0;
    private static int failed = 0;
    private static String currentSection = "";

    private MiniTest() {
    }

    public static void section(String name) {
        currentSection = name;
        System.out.println();
        System.out.println("== " + name + " " + "=".repeat(Math.max(0, 58 - name.length())));
    }

    /** Passes when {@code condition} is true. */
    public static void check(String what, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + what);
        } else {
            failed++;
            System.out.println("  FAIL  " + what);
        }
    }

    /** Passes when the two values are equal, and shows both when they are not. */
    public static void checkEquals(String what, Object expected, Object actual) {
        boolean same = expected == null ? actual == null : expected.equals(actual);
        if (same) {
            passed++;
            System.out.println("  PASS  " + what);
        } else {
            failed++;
            System.out.println("  FAIL  " + what);
            System.out.println("        expected: " + expected);
            System.out.println("        actual:   " + actual);
        }
    }

    /** Passes when running {@code action} throws the expected kind of exception. */
    public static void checkThrows(String what, Class<? extends Throwable> expected,
                                   Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("  FAIL  " + what + " (nothing was thrown)");
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                passed++;
                System.out.println("  PASS  " + what);
            } else {
                failed++;
                System.out.println("  FAIL  " + what + " (threw " + thrown.getClass().getSimpleName()
                        + " instead of " + expected.getSimpleName() + ")");
            }
        }
    }

    /** Prints the totals. @return 0 when everything passed, 1 otherwise. */
    public static int summary() {
        System.out.println();
        System.out.println("=".repeat(64));
        System.out.println("  " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " checks in total");
        System.out.println(failed == 0
                ? "  ALL TESTS PASSED"
                : "  THERE ARE FAILING TESTS - fix them before you commit!");
        System.out.println("=".repeat(64));
        return failed == 0 ? 0 : 1;
    }

    public static String getCurrentSection() {
        return currentSection;
    }
}
