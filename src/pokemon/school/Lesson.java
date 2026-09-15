package pokemon.school;

import pokemon.data.GameData;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;

/**
 * One interactive computer-science lesson that runs inside the game.
 *
 * <p>TEACHING NOTE (CS II - interfaces as plug-in points):
 * Every lesson in the Trainer School implements this interface. Adding a lesson
 * means writing one new class and adding one line to {@link TrainerSchool} - no
 * other file changes. That is what people mean by "open for extension".
 */
public interface Lesson {

    /** The menu title. */
    String title();

    /** Which course this belongs to: "CS I", "CS II" or "CS III". */
    String courseLevel();

    /** The computer-science idea being taught, in a few words. */
    String concept();

    /** Runs the lesson. */
    void teach(ConsoleUI ui, GameData data, RandomSource rng);
}
