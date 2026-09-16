package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I - for, while, and how to choose between them. */
public class LoopsLesson implements Lesson {

    @Override
    public String title() {
        return "Loops that do the work";
    }

    @Override
    public String courseLevel() {
        return "CS I";
    }

    @Override
    public String concept() {
        return "for vs while, accumulators";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        ui.println("  RULE OF THUMB:");
        ui.println("    for   - you know how many times ('check all 4 move slots')");
        ui.println("    while - you repeat until something happens ('until one side faints')");
        ui.blank();

        Species species = data.species("charmander");
        Pokemon charmander = Pokemon.create(species, 5, rng);

        ui.println("  A 'while' loop in action - " + charmander.getNickname()
                + " gains experience until it stops levelling:");
        ui.blank();
        int level = charmander.getLevel();
        int battles = 0;
        List<String> log = new ArrayList<>();
        while (charmander.getLevel() < 12 && battles < 60) {
            battles++;
            charmander.gainExperience(40);
            if (charmander.getLevel() != level) {
                level = charmander.getLevel();
                log.add("  after " + Text.pad(battles + " battles", 14)
                        + "level " + level + "   (total exp " + charmander.getExperience() + ")");
            }
        }
        for (String line : log) {
            ui.println(line);
        }
        ui.blank();
        ui.println("  Notice: the loop ran " + battles + " times, and NOBODY had to know");
        ui.println("  that number in advance. That is exactly when you choose 'while'.");
        ui.blank();

        ui.println("  A 'for' loop in action - adding up base stats (an ACCUMULATOR):");
        int total = 0;
        StringBuilder sum = new StringBuilder();
        for (pokemon.model.Stat stat : pokemon.model.Stat.values()) {
            int value = species.getBaseStats().get(stat);
            total += value;                                  // <- the accumulator
            sum.append(sum.length() == 0 ? "" : " + ").append(value);
        }
        ui.println("    " + sum + " = " + total);
        ui.println("    (Species.getBaseStats().total() does exactly this.)");
        ui.blank();
        ui.println("  THE CLASSIC BUG: declaring 'int total = 0;' INSIDE the loop resets it");
        ui.println("  every pass and the answer comes out as just the last value ("
                + species.getBaseStats().speed() + ").");
        ui.blank();

        int answer = ui.promptInt("If a Pokemon gains 3 levels at once, which loop do you need?"
                + " (1 = if, 2 = while)", 1, 2);
        ui.println(answer == 2
                ? "  Right. An 'if' would only level it up once - a real bug in this project's history."
                : "  Careful: an 'if' checks once. You need a 'while' to level up repeatedly.");
    }
}
