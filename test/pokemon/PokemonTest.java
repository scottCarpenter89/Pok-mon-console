package pokemon;

import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Move;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.model.Stats;
import pokemon.model.Status;
import pokemon.util.FixedRandom;
import pokemon.util.RandomSource;

/** Tests for individual Pokemon: stats, experience, damage, status and evolution. */
public class PokemonTest {

    public static void run(GameData data) {
        MiniTest.section("Pokemon stats");

        Species charmander = data.species("charmander");
        // Zero IVs make the arithmetic predictable.
        Stats noIvs = new Stats(0, 0, 0, 0, 0, 0);
        Pokemon level50 = new Pokemon(charmander, 50, noIvs);

        // HP = ((2*39 + 0) * 50) / 100 + 50 + 10 = 39 + 60 = 99
        MiniTest.checkEquals("HP formula at level 50", 99, level50.getMaxHp());
        // Attack = ((2*52 + 0) * 50) / 100 + 5 = 52 + 5 = 57
        MiniTest.checkEquals("Attack formula at level 50", 57, level50.getStat(Stat.ATTACK));
        MiniTest.check("A fresh Pokemon starts at full health",
                level50.getCurrentHp() == level50.getMaxHp());

        Pokemon level5 = new Pokemon(charmander, 5, noIvs);
        MiniTest.check("A lower level means fewer HP", level5.getMaxHp() < level50.getMaxHp());

        Pokemon perfect = new Pokemon(charmander, 50, new Stats(31, 31, 31, 31, 31, 31));
        MiniTest.check("Perfect IVs beat zero IVs", perfect.getMaxHp() > level50.getMaxHp());

        MiniTest.section("Damage and healing");

        Pokemon subject = new Pokemon(charmander, 50, noIvs);
        MiniTest.checkEquals("takeDamage reports what it actually removed", 10,
                subject.takeDamage(10));
        MiniTest.checkEquals("HP went down by exactly that much", 89, subject.getCurrentHp());
        MiniTest.checkEquals("heal reports what it actually restored", 10, subject.heal(999));
        MiniTest.checkEquals("Healing stops at the maximum", 99, subject.getCurrentHp());
        subject.takeDamage(99999);
        MiniTest.checkEquals("HP never goes below zero", 0, subject.getCurrentHp());
        MiniTest.check("Zero HP means fainted", subject.isFainted());
        MiniTest.checkEquals("A fainted Pokemon cannot be healed with a potion", 0,
                subject.heal(50));
        subject.revive(0.5);
        // NOTE FOR STUDENTS: revive() uses Math.round, so a Pokemon with an odd
        // maximum comes back with one more HP than maxHp / 2 would suggest. The
        // first version of this test asserted maxHp / 2 and failed - a good
        // reminder that a failing test is not always the code's fault.
        MiniTest.checkEquals("Revive brings it back with half its HP (rounded)",
                (int) Math.round(subject.getMaxHp() * 0.5), subject.getCurrentHp());
        MiniTest.check("A revived Pokemon is no longer fainted", !subject.isFainted());
        MiniTest.checkEquals("Negative damage is ignored", 0, subject.takeDamage(-100));

        MiniTest.section("Experience and levelling");

        Pokemon grower = new Pokemon(charmander, 5, noIvs);
        MiniTest.checkEquals("Level 5 needs 125 total experience", 125,
                Pokemon.experienceNeededForLevel(5));
        MiniTest.checkEquals("Level 6 needs 216 total experience", 216,
                Pokemon.experienceNeededForLevel(6));

        grower.gainExperience(91);      // 125 + 91 = 216, exactly level 6
        MiniTest.checkEquals("Exactly enough experience levels it up once", 6, grower.getLevel());

        Pokemon jumper = new Pokemon(charmander, 5, noIvs);
        jumper.gainExperience(1000);    // 1125 total is past level 10 (1000)
        MiniTest.check("A big win can grant several levels at once", jumper.getLevel() >= 10);

        Pokemon healthy = new Pokemon(charmander, 5, noIvs);
        int hpBefore = healthy.getCurrentHp();
        healthy.gainExperience(2000);
        MiniTest.check("Levelling up raises current HP as well as maximum HP",
                healthy.getCurrentHp() > hpBefore);

        Pokemon learner = new Pokemon(charmander, 6, noIvs);
        List<Move> learned = learner.gainExperience(5000);
        MiniTest.check("Levelling past a learn-level returns the new moves",
                !learned.isEmpty());

        MiniTest.section("Status conditions");

        RandomSource rng = new FixedRandom(1);
        Pokemon fireType = new Pokemon(charmander, 20, noIvs);
        MiniTest.check("A Fire type cannot be burned",
                !fireType.applyStatus(Status.BURN, rng));

        Pokemon grassType = new Pokemon(data.species("bulbasaur"), 20, noIvs);
        MiniTest.check("A Poison type cannot be poisoned",
                !grassType.applyStatus(Status.POISON, rng));
        MiniTest.check("But it can be put to sleep",
                grassType.applyStatus(Status.SLEEP, rng));
        MiniTest.check("A Pokemon can only carry one condition at a time",
                !grassType.applyStatus(Status.BURN, rng));
        grassType.clearStatus();
        MiniTest.checkEquals("clearStatus works", Status.NONE, grassType.getStatus());

        Pokemon burned = new Pokemon(charmander, 50, noIvs);
        int attackBefore = burned.getStat(Stat.ATTACK);
        Pokemon burnedTarget = new Pokemon(data.species("squirtle"), 50, noIvs);
        int waterAttackBefore = burnedTarget.getStat(Stat.ATTACK);
        burnedTarget.applyStatus(Status.BURN, rng);
        MiniTest.check("A burn halves physical Attack",
                burnedTarget.getStat(Stat.ATTACK) < waterAttackBefore);
        MiniTest.check("An unburned Pokemon keeps its Attack",
                burned.getStat(Stat.ATTACK) == attackBefore);

        Pokemon paralysed = new Pokemon(data.species("squirtle"), 50, noIvs);
        int speedBefore = paralysed.getStat(Stat.SPEED);
        paralysed.applyStatus(Status.PARALYSIS, rng);
        MiniTest.check("Paralysis halves Speed", paralysed.getStat(Stat.SPEED) < speedBefore);

        MiniTest.section("Evolution");

        Pokemon young = new Pokemon(charmander, 15, noIvs);
        MiniTest.check("Charmander cannot evolve at level 15", !young.canEvolve());
        Pokemon ready = new Pokemon(charmander, 16, noIvs);
        MiniTest.check("Charmander can evolve at level 16", ready.canEvolve());

        ready.setNickname("Blaze");
        ready.takeDamage(5);
        Pokemon evolved = ready.evolve();
        MiniTest.checkEquals("It evolves into the right species", "Charmeleon",
                evolved.getSpecies().getName());
        MiniTest.checkEquals("A custom nickname survives evolution", "Blaze",
                evolved.getNickname());
        MiniTest.checkEquals("The level is unchanged", 16, evolved.getLevel());
        MiniTest.check("Evolving is not a free full heal",
                evolved.getCurrentHp() < evolved.getMaxHp());
        MiniTest.check("A Pokemon that keeps its species name gets the new one",
                new Pokemon(charmander, 16, noIvs).evolve().getNickname().equals("Charmeleon"));

        Pokemon finalForm = new Pokemon(data.species("charizard"), 60, noIvs);
        MiniTest.check("A fully evolved Pokemon cannot evolve again", !finalForm.canEvolve());
        MiniTest.checkEquals("evolve() returns null when it cannot evolve", null,
                finalForm.evolve());

        MiniTest.section("Moves");

        Pokemon fighter = new Pokemon(charmander, 30, noIvs);
        MiniTest.check("A new Pokemon starts with no moves", fighter.getMoves().isEmpty());
        for (String id : List.of("scratch", "ember", "growl", "metal_claw")) {
            MiniTest.check("Learning " + id + " works", fighter.learnMove(data.move(id)));
        }
        MiniTest.check("A fifth move is refused", !fighter.learnMove(data.move("slash")));
        MiniTest.check("Learning a known move is harmless",
                fighter.learnMove(data.move("ember")));
        MiniTest.checkEquals("It still knows exactly four moves", 4, fighter.getMoves().size());
        fighter.replaceMove(0, data.move("slash"));
        MiniTest.check("Replacing a move works", fighter.knows(data.move("slash")));
        MiniTest.check("The replaced move is gone", !fighter.knows(data.move("scratch")));

        Pokemon created = Pokemon.create(charmander, 30, new FixedRandom(5));
        MiniTest.check("Pokemon.create gives a wild Pokemon up to four moves",
                !created.getMoves().isEmpty() && created.getMoves().size() <= 4);
    }
}
