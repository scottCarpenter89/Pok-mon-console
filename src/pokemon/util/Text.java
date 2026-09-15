package pokemon.util;

/**
 * Small string helpers used all over the game.
 *
 * <p>TEACHING NOTE (CS I - methods and String manipulation):
 * Every method here is {@code static}: you call it on the class
 * ({@code Text.titleCase("pikachu")}) instead of on an object. Static methods are
 * the right choice when a method needs no object state - it just transforms its
 * inputs into an output. Methods like these are called <em>pure functions</em>,
 * and they are the easiest kind of code to test.
 */
public final class Text {

    /** Private constructor: this class is a toolbox, never an object. */
    private Text() {
    }

    /** "CHARIZARD" or "charizard" -> "Charizard" */
    public static String titleCase(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        String lower = raw.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    /** "SP_ATTACK" -> "Sp. Attack"-ish: turns ENUM_NAMES into readable words. */
    public static String humanize(String enumName) {
        String[] words = enumName.toLowerCase().split("_");
        StringBuilder out = new StringBuilder();
        for (String word : words) {
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(titleCase(word));
        }
        return out.toString();
    }

    /** Pads (or trims) text so columns line up in the console. */
    public static String pad(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }
        return builder.toString();
    }

    /** Right-aligns text in a column of the given width. */
    public static String padLeft(String text, int width) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() + text.length() < width) {
            builder.append(' ');
        }
        return builder + text;
    }

    public static String repeat(char c, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(c);
        }
        return builder.toString();
    }
}
