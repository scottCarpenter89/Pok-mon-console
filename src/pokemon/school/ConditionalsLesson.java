package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Type;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I - if / else if / else, boolean logic, and switch. */
public class ConditionalsLesson implements Lesson {

    @Override
    public String title() {
        return "Making decisions";
    }

    @Override
    public String courseLevel() {
        return "CS I";
    }

    @Override
    public String concept() {
        return "if / else if / else, && and ||";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        ui.println("  The game constantly asks yes/no questions. Here is a real one:");
        ui.blank();
        ui.println("    if (multiplier == 0.0)      -> \"It has no effect...\"");
        ui.println("    else if (multiplier > 1.0)  -> \"It's super effective!\"");
        ui.println("    else if (multiplier < 1.0)  -> \"It's not very effective...\"");
        ui.println("    else                        -> (say nothing)");
        ui.blank();
        ui.println("  ORDER MATTERS. Java checks the branches top to bottom and stops at");
        ui.println("  the first true one. Swap the first two lines and a 2x hit would be");
        ui.println("  announced as 'no effect' whenever the multiplier was 0.");
        ui.blank();

        List<String> types = new ArrayList<>();
        for (Type type : Type.values()) {
            types.add(type.displayName());
        }
        int attackIndex = ui.chooseFromList("Pick an attacking type:", types, null);
        int defendIndex = ui.chooseFromList("Pick a defending type:", types, null);

        Type attacking = Type.values()[attackIndex];
        Type defending = Type.values()[defendIndex];
        double multiplier = Type.effectiveness(attacking, defending);

        ui.blank();
        ui.println("  " + attacking.displayName() + " -> " + defending.displayName()
                + " has a multiplier of " + multiplier);
        ui.println("  The if-chain above prints: \""
                + pokemon.battle.DamageCalculator.effectivenessMessage(multiplier) + "\"");
        ui.blank();

        ui.println("  Now a compound condition, straight out of Pokemon.applyStatus():");
        ui.println("    if (isFainted() || status != Status.NONE || newStatus == Status.NONE)");
        ui.println("        return false;");
        ui.blank();
        ui.println("  || means OR: ANY of the three being true blocks the status.");
        ui.println("  && means AND: EVERY part must be true.");
        ui.blank();
        ui.box("Truth table for A && B and A || B", List.of(
                Text.pad("A", 8) + Text.pad("B", 8) + Text.pad("A && B", 10) + "A || B",
                Text.pad("true", 8) + Text.pad("true", 8) + Text.pad("true", 10) + "true",
                Text.pad("true", 8) + Text.pad("false", 8) + Text.pad("false", 10) + "true",
                Text.pad("false", 8) + Text.pad("true", 8) + Text.pad("false", 10) + "true",
                Text.pad("false", 8) + Text.pad("false", 8) + Text.pad("false", 10) + "false"));
    }
}
