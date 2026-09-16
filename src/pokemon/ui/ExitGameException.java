package pokemon.ui;

/**
 * Thrown when the player quits, or when input runs out (for example when the game
 * is fed a script with {@code java pokemon.Main < moves.txt}).
 *
 * <p>TEACHING NOTE (CS II - custom exceptions):
 * Writing your own exception type costs three lines and makes the intent obvious
 * at the catch site. This one extends {@link RuntimeException} (an "unchecked"
 * exception), so methods do not have to declare it - appropriate here because
 * quitting can happen at literally any prompt in the game.
 */
public class ExitGameException extends RuntimeException {

    public ExitGameException(String message) {
        super(message);
    }
}
