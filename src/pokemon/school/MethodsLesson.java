package pokemon.school;

import java.util.List;
import pokemon.battle.DamageCalculator;
import pokemon.data.GameData;
import pokemon.model.Move;
import pokemon.model.Pokemon;
import pokemon.model.Stat;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS I - methods: parameters, return values, and why small is better. */
public class MethodsLesson implements Lesson {

    @Override
    public String title() {
        return "Methods and the damage formula";
    }

    @Override
    public String courseLevel() {
        return "CS I";
    }

    @Override
    public String concept() {
        return "parameters, return values, reuse";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        Pokemon attacker = Pokemon.create(data.species("charmander"), 12, rng);
        Pokemon defender = Pokemon.create(data.species("bulbasaur"), 12, rng);
        Move ember = data.move("ember");

        ui.println("  A method takes inputs (parameters), does one job, and returns a result.");
        ui.println("  DamageCalculator.computeDamage() takes eight parameters and returns an int.");
        ui.blank();

        int attackStat = attacker.getStat(Stat.SP_ATTACK);
        int defenseStat = defender.getStat(Stat.SP_DEFENSE);
        double stab = DamageCalculator.sameTypeAttackBonus(attacker, ember);
        double typeMultiplier = DamageCalculator.typeMultiplier(ember, defender);

        ui.box(attacker.getNickname() + " uses " + ember.getName() + " on "
                + defender.getNickname(), List.of(
                Text.pad("level", 20) + attacker.getLevel(),
                Text.pad("move power", 20) + ember.getPower(),
                Text.pad("attacker Sp. Atk", 20) + attackStat,
                Text.pad("defender Sp. Def", 20) + defenseStat,
                Text.pad("STAB", 20) + stab + "   (Fire move, Fire Pokemon)",
                Text.pad("type multiplier", 20) + typeMultiplier
                        + "   (Fire vs Grass/Poison)"));

        ui.blank();
        ui.println("  Step by step, with no critical hit and no random roll:");
        int base = ((2 * attacker.getLevel() / 5 + 2) * ember.getPower() * attackStat
                / Math.max(1, defenseStat)) / 50 + 2;
        ui.println("    base = ((2*" + attacker.getLevel() + "/5 + 2) * " + ember.getPower()
                + " * " + attackStat + " / " + defenseStat + ") / 50 + 2  =  " + base);
        ui.println("    damage = base * " + stab + " * " + typeMultiplier + "  =  "
                + DamageCalculator.computeDamage(attacker.getLevel(), ember.getPower(),
                        attackStat, defenseStat, stab, typeMultiplier, false, 1.0));
        ui.blank();

        ui.println("  The same method, called three ways:");
        ui.println("    minimum roll (0.85): " + DamageCalculator.computeDamage(
                attacker.getLevel(), ember.getPower(), attackStat, defenseStat,
                stab, typeMultiplier, false, 0.85));
        ui.println("    maximum roll (1.00): " + DamageCalculator.computeDamage(
                attacker.getLevel(), ember.getPower(), attackStat, defenseStat,
                stab, typeMultiplier, false, 1.0));
        ui.println("    critical hit      : " + DamageCalculator.computeDamage(
                attacker.getLevel(), ember.getPower(), attackStat, defenseStat,
                stab, typeMultiplier, true, 1.0));
        ui.blank();
        ui.println("  One method. Three questions answered. No copied code.");
        ui.println("  That is the whole argument for writing methods.");
    }
}
