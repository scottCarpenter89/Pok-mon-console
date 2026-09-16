package pokemon.school;

import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I to CS II - classes, objects, and encapsulation. */
public class ObjectsLesson implements Lesson {

    @Override
    public String title() {
        return "Classes and objects";
    }

    @Override
    public String courseLevel() {
        return "CS I/II";
    }

    @Override
    public String concept() {
        return "class vs object, encapsulation";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        Species species = data.species("eevee");
        Pokemon first = Pokemon.create(species, 10, rng);
        Pokemon second = Pokemon.create(species, 10, rng);
        first.setNickname("Vee");
        second.setNickname("Sandy");

        ui.println("  A CLASS is the blueprint. An OBJECT is one thing built from it.");
        ui.println("  Two Eevee, same species object, different individual values:");
        ui.blank();
        ui.box("Two objects of class Pokemon", List.of(
                Text.pad("", 10) + Text.pad("Max HP", 9) + Text.pad("Attack", 9)
                        + Text.pad("Speed", 9) + "HP/Atk/Spe IVs",
                describe(first),
                describe(second)));
        ui.blank();
        ui.println("  Both point at the SAME Species object (there is only one 'Eevee'),");
        ui.println("  but each has its own level, HP, nickname and IVs.");
        ui.blank();

        ui.println("  ENCAPSULATION: currentHp is private. You cannot write");
        ui.println("      eevee.currentHp = -500;");
        ui.println("  You must go through a method that protects the rules:");
        ui.blank();
        ui.println("      public int takeDamage(int amount) {");
        ui.println("          currentHp = Math.max(0, currentHp - Math.max(0, amount));");
        ui.println("          ...");
        ui.println("      }");
        ui.blank();
        ui.println("  Watch the object defend its own rules. " + first.getNickname()
                + " starts at " + first.getCurrentHp() + "/" + first.getMaxHp() + " HP.");
        first.takeDamage(5);
        ui.println("    takeDamage(5)       -> HP " + first.getCurrentHp());
        int healed = first.heal(99999);
        ui.println("    heal(99999)         -> restored only " + healed
                + " HP, stopping at the maximum of " + first.getMaxHp());
        first.takeDamage(99999);
        ui.println("    takeDamage(99999)   -> HP " + first.getCurrentHp()
                + ", never negative. isFainted() = " + first.isFainted());
        int wasted = first.heal(50);
        ui.println("    heal(50) on a fainted Pokemon -> restored " + wasted
                + " HP. A Potion cannot revive; that is the game rule, enforced in code.");
        first.revive(1.0);
        ui.println("    revive(1.0)         -> HP " + first.getCurrentHp() + ", back in the fight.");
        ui.blank();
        ui.println("  Every one of those rules lives in ONE file. That is the payoff:");
        ui.println("  when the rule changes, there is exactly one place to change it.");
    }

    /** Formats one Pokemon as a table row. */
    private String describe(Pokemon mon) {
        return Text.pad(mon.getNickname(), 10)
                + Text.pad(String.valueOf(mon.getMaxHp()), 9)
                + Text.pad(String.valueOf(mon.getStat(Stat.ATTACK)), 9)
                + Text.pad(String.valueOf(mon.getStat(Stat.SPEED)), 9)
                + mon.getIndividualValues().hp() + "/"
                + mon.getIndividualValues().attack() + "/"
                + mon.getIndividualValues().speed();
    }
}
