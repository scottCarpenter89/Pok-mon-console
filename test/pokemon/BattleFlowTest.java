package pokemon;

import pokemon.battle.BattleAI;
import pokemon.battle.CleverAI;
import pokemon.battle.DamageCalculator;
import pokemon.battle.RandomAI;
import pokemon.battle.TypeSmartAI;
import pokemon.data.GameData;
import pokemon.model.MoveSlot;
import pokemon.model.NpcTrainer;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Stats;
import pokemon.model.Trainer;
import pokemon.util.FixedRandom;
import pokemon.util.SeededRandom;

/** Tests for the battle AIs and for the trainer hierarchy. */
public class BattleFlowTest {

    public static void run(GameData data) {
        MiniTest.section("Battle AI");

        Stats noIvs = new Stats(0, 0, 0, 0, 0, 0);
        Pokemon charizard = new Pokemon(data.species("charizard"), 40, noIvs);
        charizard.learnMove(data.move("flamethrower"));   // 90 power, super effective
        charizard.learnMove(data.move("tackle"));         // 40 power, plain
        charizard.learnMove(data.move("growl"));          // status

        Pokemon venusaur = new Pokemon(data.species("venusaur"), 40, noIvs);

        BattleAI smart = new TypeSmartAI();
        MoveSlot choice = smart.chooseMove(charizard, venusaur, new SeededRandom(1));
        MiniTest.checkEquals("The smart AI picks the super-effective move",
                "Flamethrower", choice.getMove().getName());

        BattleAI clever = new CleverAI();
        MoveSlot cleverChoice = clever.chooseMove(charizard, venusaur, new SeededRandom(1));
        MiniTest.check("The clever AI also picks a damaging move",
                cleverChoice.getMove().isDamaging());

        BattleAI random = new RandomAI();
        MiniTest.check("The random AI picks something it knows",
                charizard.getMoves().contains(random.chooseMove(charizard, venusaur,
                        new SeededRandom(2))));

        // An AI must never choose a move with no PP left.
        Pokemon drained = new Pokemon(data.species("pikachu"), 20, noIvs);
        drained.learnMove(data.move("thunder_shock"));
        drained.learnMove(data.move("quick_attack"));
        for (MoveSlot slot : drained.getMoves()) {
            while (slot.hasPp()) {
                slot.spendPp();
            }
        }
        MiniTest.check("A Pokemon with no PP is detected", drained.isOutOfPp());
        MiniTest.checkEquals("The AI returns nothing when every move is spent", null,
                smart.chooseMove(drained, venusaur, new SeededRandom(3)));
        MiniTest.checkEquals("The random AI does the same", null,
                random.chooseMove(drained, venusaur, new SeededRandom(3)));

        MiniTest.checkThrows("An unknown AI name in a data file is rejected",
                IllegalArgumentException.class, () -> BattleAI.byName("telepathy"));
        MiniTest.check("Known AI names load", BattleAI.byName("CLEVER") instanceof CleverAI);

        MiniTest.section("Trainers and polymorphism");

        Player player = new Player("Ash", "pallet_town");
        player.addToParty(new Pokemon(data.species("pikachu"), 10, noIvs));

        NpcTrainer npc = data.trainer("gym_leader_brock").build(new FixedRandom(5));

        MiniTest.checkEquals("A player is announced by name", "Ash", player.battleTitle());
        MiniTest.checkEquals("An NPC is announced with a trainer class",
                "Gym Leader Brock", npc.battleTitle());
        MiniTest.check("Both are Trainers", player instanceof Trainer && npc instanceof Trainer);
        MiniTest.checkEquals("Brock's team was built from the data file", 3,
                npc.getParty().size());
        MiniTest.check("Every Pokemon on a built team is at full health",
                npc.getParty().stream().noneMatch(p -> p.getCurrentHp() < p.getMaxHp()));

        // Building the same trainer twice must give two independent teams - this is the
        // whole point of the factory, and it is easy to break.
        NpcTrainer again = data.trainer("gym_leader_brock").build(new FixedRandom(5));
        npc.getParty().get(0).takeDamage(9999);
        MiniTest.check("Hurting one copy does not hurt the other",
                !again.getParty().get(0).isFainted());

        MiniTest.section("Party management");

        MiniTest.checkEquals("The active Pokemon is the first healthy one",
                player.getParty().get(0), player.getActivePokemon());
        player.addToParty(new Pokemon(data.species("squirtle"), 10, noIvs));
        player.getParty().get(0).takeDamage(99999);
        MiniTest.checkEquals("A fainted leader is skipped",
                player.getParty().get(1), player.getActivePokemon());
        player.getParty().get(1).takeDamage(99999);
        MiniTest.check("A wiped-out party has nobody to send out", !player.hasUsablePokemon());
        player.healParty();
        MiniTest.check("Healing the party brings everyone back", player.hasUsablePokemon());

        Pokemon second = player.getParty().get(1);
        player.sendOut(second);
        MiniTest.checkEquals("Switching moves that Pokemon to the front", second,
                player.getParty().get(0));

        while (player.getParty().size() < Trainer.MAX_PARTY) {
            player.addToParty(new Pokemon(data.species("rattata"), 3, noIvs));
        }
        MiniTest.check("A seventh Pokemon does not fit in the party",
                !player.addToParty(new Pokemon(data.species("pidgey"), 3, noIvs)));
        MiniTest.check("It goes to the storage box instead",
                !player.receive(new Pokemon(data.species("pidgey"), 3, noIvs))
                        && player.getStorageBox().size() == 1);

        MiniTest.section("Money");

        Player buyer = new Player("Shopper", "viridian_city");
        buyer.setMoney(1000);
        MiniTest.check("You can spend money you have", buyer.spend(600));
        MiniTest.checkEquals("The money went down", 400, buyer.getMoney());
        MiniTest.check("You cannot spend money you do not have", !buyer.spend(9999));
        MiniTest.checkEquals("A failed purchase changes nothing", 400, buyer.getMoney());
        buyer.earn(100);
        MiniTest.checkEquals("Winning a battle pays", 500, buyer.getMoney());
        MiniTest.checkEquals("Blacking out costs half", 250, buyer.loseHalfMoney());
        MiniTest.checkEquals("...and leaves the other half", 250, buyer.getMoney());

        MiniTest.section("Same-type attack bonus");

        MiniTest.checkEquals("A Fire Pokemon gets STAB on a Fire move",
                DamageCalculator.STAB_MULTIPLIER,
                DamageCalculator.sameTypeAttackBonus(charizard, data.move("flamethrower")));
        MiniTest.checkEquals("...but not on a Normal move", 1.0,
                DamageCalculator.sameTypeAttackBonus(charizard, data.move("tackle")));
        MiniTest.checkEquals("Fire vs a Grass/Poison defender is 2x", 2.0,
                DamageCalculator.typeMultiplier(data.move("flamethrower"), venusaur));
    }
}
