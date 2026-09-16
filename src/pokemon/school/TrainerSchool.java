package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/**
 * The in-game computer science classroom.
 *
 * <p>Students reach it from the main menu or from any town. Each lesson explains one
 * idea and then DEMONSTRATES it using the live game data - the same species, moves
 * and type chart the battles use. Nothing here is a toy example bolted on the side.
 *
 * <p>SUGGESTED CLASSROOM USE: run one lesson at the start of a unit as the hook,
 * then open the matching source file and read the real implementation together.
 */
public class TrainerSchool {

    private final GameData data;
    private final ConsoleUI ui;
    private final RandomSource rng;
    private final List<Lesson> lessons = new ArrayList<>();

    public TrainerSchool(GameData data, ConsoleUI ui, RandomSource rng) {
        this.data = data;
        this.ui = ui;
        this.rng = rng;

        // Adding a lesson is one line. The menu, numbering and routing are automatic.
        lessons.add(new VariablesLesson());
        lessons.add(new ConditionalsLesson());
        lessons.add(new LoopsLesson());
        lessons.add(new ArraysLesson());
        lessons.add(new MethodsLesson());
        lessons.add(new ObjectsLesson());
        lessons.add(new InheritanceLesson());
        lessons.add(new RecursionLesson());
        lessons.add(new SearchSortLesson());
        lessons.add(new DataStructuresLesson());
    }

    public void run() {
        while (true) {
            ui.header("Trainer School");
            ui.println("  Every lesson uses the real data and the real code of this game.");

            List<String> labels = new ArrayList<>();
            for (Lesson lesson : lessons) {
                labels.add(Text.pad(lesson.courseLevel(), 10)
                        + Text.pad(lesson.title(), 32) + lesson.concept());
            }
            int choice = ui.chooseFromList("Which lesson?", labels, "Leave the school");
            if (choice < 0) {
                return;
            }
            Lesson lesson = lessons.get(choice);
            ui.header(lesson.title());
            lesson.teach(ui, data, rng);
            ui.pause();
        }
    }

    public List<Lesson> getLessons() {
        return lessons;
    }
}
