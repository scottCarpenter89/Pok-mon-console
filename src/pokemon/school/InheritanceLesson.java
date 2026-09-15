package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.battle.BattleAI;
import pokemon.battle.CleverAI;
import pokemon.battle.RandomAI;
import pokemon.battle.TypeSmartAI;
import pokemon.data.GameData;
import pokemon.model.MoveSlot;
import pokemon.model.NpcTrainer;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Trainer;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS II - inheritance, overriding, polymorphism, and interfaces. */
public class InheritanceLesson implements Lesson {

    @Override
    public String title() {
        return "Inheritance and polymorphism";
    }

    @Override
    public String courseLevel() {
        return "CS II";
    }

    @Override
    public String concept() {
        return "is-a, override, many shapes";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        ui.println("  THE FAMILY TREE IN THIS GAME:");
        ui.blank();
        ui.println("                 Trainer  (abstract - name + party)");
        ui.println("                /       \\");
        ui.println("          Player         NpcTrainer");
        ui.println("      (money, bag,       (dialogue, reward,");
        ui.println("       Pokedex)           a BattleAI)");
        ui.blank();

        Player player = new Player("Ash", "pallet_town");
        player.addToParty(Pokemon.create(data.species("pikachu"), 15, rng));
        NpcTrainer npc = new NpcTrainer("demo", "Gym Leader", "Brock", 900,
                "Let's battle!", "Well fought.", new CleverAI());
        npc.addToParty(Pokemon.create(data.species("onix"), 15, rng));

        // The same variable type holds both. That is polymorphism.
        List<Trainer> everyone = new ArrayList<>();
        everyone.add(player);
        everyone.add(npc);

        ui.println("  Both objects are stored in a List<Trainer>, and we call the SAME");
        ui.println("  method on each. Java picks the right version at run time:");
        ui.blank();
        for (Trainer trainer : everyone) {
            ui.println("    " + Text.pad(trainer.getClass().getSimpleName(), 14)
                    + "battleTitle() -> \"" + trainer.battleTitle() + "\"");
        }
        ui.blank();
        ui.println("  Player did not override battleTitle(), so it INHERITED the plain");
        ui.println("  version. NpcTrainer overrode it to add the trainer class.");
        ui.blank();

        ui.println("  INTERFACES do the same trick without a family tree. Three different");
        ui.println("  AI classes, one interface, asked the same question:");
        ui.blank();

        Pokemon self = npc.getParty().get(0);
        Pokemon foe = player.getParty().get(0);
        ui.println("    " + self.getNickname() + " (Lv." + self.getLevel() + ") vs "
                + foe.getNickname() + " (Lv." + foe.getLevel() + ")");
        ui.blank();

        List<BattleAI> brains = List.of(new RandomAI(), new TypeSmartAI(), new CleverAI());
        for (BattleAI brain : brains) {
            MoveSlot chosen = brain.chooseMove(self, foe, rng);
            ui.println("    " + Text.pad(brain.name() + " AI", 14) + "chooses "
                    + (chosen == null ? "(nothing)" : chosen.getMove().getName()));
        }
        ui.blank();
        ui.println("  The battle engine contains no if-statement about which AI is playing.");
        ui.println("  It just calls chooseMove(). Add a fourth AI tomorrow and the engine");
        ui.println("  does not change by one character. THAT is why we do this.");
    }
}
