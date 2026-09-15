package pokemon.data;

/**
 * Thrown when a data file is missing, malformed, or refers to something that does
 * not exist.
 *
 * <p>TEACHING NOTE (CS II - exceptions that actually help):
 * Compare these two error messages:
 * <pre>
 *   Exception in thread "main" java.lang.NumberFormatException: For input string: "fourty"
 *   data/species.csv line 12: column 'baseAttack' is not a whole number ("fourty")
 * </pre>
 * Both stop the program. Only one tells you how to fix it. Teaching students to
 * wrap low-level exceptions in a message with CONTEXT is one of the highest-value
 * habits in the course.
 */
public class DataException extends RuntimeException {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
