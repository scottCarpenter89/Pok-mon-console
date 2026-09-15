package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Type;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I - one-dimensional and two-dimensional arrays. */
public class ArraysLesson implements Lesson {

    @Override
    public String title() {
        return "Arrays and the type chart";
    }

    @Override
    public String courseLevel() {
        return "CS I";
    }

    @Override
    public String concept() {
        return "1D and 2D arrays, nested loops";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        ui.println("  Type.values() is a one-dimensional array of all "
                + Type.values().length + " types:");
        StringBuilder row = new StringBuilder("    ");
        for (int i = 0; i < Type.values().length; i++) {
            row.append(i).append("=").append(Type.values()[i].displayName()).append("  ");
            if ((i + 1) % 6 == 0) {
                ui.println(row.toString());
                row = new StringBuilder("    ");
            }
        }
        if (row.length() > 4) {
            ui.println(row.toString());
        }
        ui.blank();
        ui.println("  Index numbers start at 0, so the LAST index is "
                + (Type.values().length - 1) + ", not " + Type.values().length + ".");
        ui.println("  Asking for index " + Type.values().length
                + " throws ArrayIndexOutOfBoundsException.");
        ui.blank();

        ui.println("  The type chart is a TWO-dimensional array: [attacker][defender].");
        ui.println("  Here is a corner of it. Read a row as 'this type attacking'.");
        ui.blank();

        Type[] sample = {Type.FIRE, Type.WATER, Type.GRASS, Type.ELECTRIC, Type.ROCK, Type.GROUND};
        List<String> lines = new ArrayList<>();
        StringBuilder header = new StringBuilder(Text.pad("", 10));
        for (Type defending : sample) {
            header.append(Text.pad(defending.displayName().substring(0, 4), 7));
        }
        lines.add(header.toString());
        for (Type attacking : sample) {
            StringBuilder line = new StringBuilder(Text.pad(attacking.displayName(), 10));
            for (Type defending : sample) {
                line.append(Text.pad(String.valueOf(Type.effectiveness(attacking, defending)), 7));
            }
            lines.add(line.toString());
        }
        ui.box("effectiveness[attacker][defender]", lines);

        ui.blank();
        ui.println("  The code that printed that box is the nested-loop pattern:");
        ui.println("    for (Type attacking : sample)          // outer loop = rows");
        ui.println("        for (Type defending : sample)      // inner loop = columns");
        ui.blank();
        ui.println("  The inner loop finishes completely for EACH pass of the outer loop,");
        ui.println("  so this printed " + sample.length + " x " + sample.length + " = "
                + (sample.length * sample.length) + " numbers.");
        ui.blank();
        ui.println("  Open src/pokemon/model/Type.java to see the full "
                + Type.values().length + "x" + Type.values().length + " chart and how");
        ui.println("  decodeChart() builds it from " + Type.values().length + " lines of text.");
    }
}
