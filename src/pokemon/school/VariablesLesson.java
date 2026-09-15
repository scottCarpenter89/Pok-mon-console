package pokemon.school;

import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I - what a variable is, and why its TYPE matters. */
public class VariablesLesson implements Lesson {

    @Override
    public String title() {
        return "Variables and types";
    }

    @Override
    public String courseLevel() {
        return "CS I";
    }

    @Override
    public String concept() {
        return "int, double, boolean, String";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        Species species = data.species("pikachu");
        Pokemon pikachu = Pokemon.create(species, 12, rng);

        ui.println("  A variable is a labelled box that holds one value.");
        ui.println("  Java insists you say what KIND of value goes in the box.");
        ui.blank();
        ui.box("Inside this Pikachu object", List.of(
                Text.pad("String nickname", 22) + "= \"" + pikachu.getNickname() + "\"",
                Text.pad("int level", 22) + "= " + pikachu.getLevel(),
                Text.pad("int currentHp", 22) + "= " + pikachu.getCurrentHp(),
                Text.pad("int maxHp", 22) + "= " + pikachu.getMaxHp(),
                Text.pad("boolean isFainted", 22) + "= " + pikachu.isFainted(),
                Text.pad("double hpFraction", 22) + "= "
                        + (double) pikachu.getCurrentHp() / pikachu.getMaxHp()));

        ui.blank();
        ui.println("  Watch what happens with int vs double division:");
        int current = pikachu.getCurrentHp();
        int max = pikachu.getMaxHp();
        ui.println("    current / max            -> " + (current / max)
                + "      <-- int division throws away the remainder!");
        ui.println("    (double) current / max   -> " + ((double) current / max)
                + "   <-- cast one side to double first");
        ui.blank();
        ui.println("  That single line is the cause of more beginner bugs than any other.");

        int guess = ui.promptInt("Quick check: what does 7 / 2 equal in Java?", 0, 10);
        ui.println(guess == 3
                ? "  Correct - 3. Java throws the 0.5 away."
                : "  Not quite: it is 3. Integer division discards the remainder.");
    }
}
