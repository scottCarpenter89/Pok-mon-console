package pokemon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import pokemon.battle.CatchCalculator;
import pokemon.data.DataException;
import pokemon.data.GameData;
import pokemon.data.SaveManager;
import pokemon.model.Bag;
import pokemon.model.Item;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Stats;
import pokemon.model.Status;
import pokemon.util.FixedRandom;
import pokemon.world.Area;
import pokemon.world.Encounter;

/** Tests for catching, the bag, the world map and saved games. */
public class GameplayTest {

    public static void run(GameData data) {
        catching(data);
        bag(data);
        world(data);
        saving(data);
    }

    private static void catching(GameData data) {
        MiniTest.section("Catching");

        Stats noIvs = new Stats(0, 0, 0, 0, 0, 0);
        Item pokeball = data.item("pokeball");
        Item ultraball = data.item("ultraball");

        Pokemon healthy = new Pokemon(data.species("pidgey"), 10, noIvs);
        Pokemon hurt = new Pokemon(data.species("pidgey"), 10, noIvs);
        hurt.takeDamage(hurt.getMaxHp() - 1);

        MiniTest.check("A weakened target is easier to catch",
                CatchCalculator.catchValue(hurt, pokeball)
                        > CatchCalculator.catchValue(healthy, pokeball));
        MiniTest.check("A better ball is better",
                CatchCalculator.catchValue(healthy, ultraball)
                        > CatchCalculator.catchValue(healthy, pokeball));

        Pokemon asleep = new Pokemon(data.species("pidgey"), 10, noIvs);
        asleep.applyStatus(Status.SLEEP, new FixedRandom(1));
        MiniTest.check("A sleeping target is easier to catch",
                CatchCalculator.catchValue(asleep, pokeball)
                        > CatchCalculator.catchValue(healthy, pokeball));

        Pokemon rare = new Pokemon(data.species("dratini"), 10, noIvs);
        Pokemon common = new Pokemon(data.species("rattata"), 10, noIvs);
        MiniTest.check("A rare species is harder to catch",
                CatchCalculator.catchValue(rare, pokeball)
                        < CatchCalculator.catchValue(common, pokeball));

        MiniTest.checkEquals("A guaranteed roll catches it",
                CatchCalculator.SHAKES_REQUIRED,
                CatchCalculator.attemptCatch(hurt, ultraball, new FixedRandom(0)));
        MiniTest.checkEquals("A hopeless roll breaks out immediately", 0,
                CatchCalculator.attemptCatch(healthy, pokeball, new FixedRandom(255)));
        MiniTest.checkEquals("Sleep doubles the bonus", 2.0,
                CatchCalculator.statusBonus(Status.SLEEP));

        // The overall catch chance must actually be playable. A full-health Pidgey in a
        // Poke Ball should be roughly a one-in-three throw, not one in a hundred.
        int commonChance = CatchCalculator.catchChancePercent(healthy, pokeball);
        MiniTest.check("A common species at full health is catchable (" + commonChance
                + "%)", commonChance >= 20 && commonChance <= 60);
        int weakenedChance = CatchCalculator.catchChancePercent(hurt, ultraball);
        MiniTest.check("A weakened species in an Ultra Ball is nearly certain ("
                + weakenedChance + "%)", weakenedChance >= 90);
        MiniTest.check("Hurting the target improves the odds",
                CatchCalculator.catchChancePercent(hurt, pokeball) > commonChance);

        // Four wobbles in a row must multiply out to the advertised chance.
        double perShake = CatchCalculator.shakeThreshold(
                CatchCalculator.catchValue(healthy, pokeball)) / 256.0;
        double overall = Math.pow(perShake, CatchCalculator.SHAKES_REQUIRED) * 100;
        MiniTest.check("Four shakes multiply out to the advertised chance ("
                + Math.round(overall) + "% vs " + commonChance + "%)",
                Math.abs(overall - commonChance) <= 3);
        MiniTest.checkEquals("A healthy target gets no bonus", 1.0,
                CatchCalculator.statusBonus(Status.NONE));
    }

    private static void bag(GameData data) {
        MiniTest.section("Bag");

        Bag bag = new Bag();
        Item potion = data.item("potion");
        Item pokeball = data.item("pokeball");
        Item townMap = data.item("town_map");

        MiniTest.check("A new bag is empty", bag.isEmpty());
        bag.add(potion, 3);
        bag.add(potion, 2);
        MiniTest.checkEquals("Adding the same item stacks", 5, bag.countOf(potion));
        MiniTest.check("Removing more than you own fails", !bag.remove(potion, 99));
        MiniTest.checkEquals("A failed removal changes nothing", 5, bag.countOf(potion));
        MiniTest.check("Using one works", bag.useOne(potion));
        MiniTest.checkEquals("The count went down by one", 4, bag.countOf(potion));
        bag.remove(potion, 4);
        MiniTest.checkEquals("An emptied item is gone from the bag", 0, bag.countOf(potion));
        MiniTest.check("Adding zero of something does nothing",
                bag.countOf(pokeball) == 0 && addNothing(bag, pokeball));

        bag.add(pokeball, 5);
        bag.add(townMap, 1);
        List<Item> wild = bag.itemsUsableInBattle(true);
        List<Item> trainer = bag.itemsUsableInBattle(false);
        MiniTest.check("Poke Balls can be used in a wild battle", wild.contains(pokeball));
        MiniTest.check("Poke Balls cannot be used on another trainer's Pokemon",
                !trainer.contains(pokeball));
        MiniTest.check("Key items are never usable in battle", !wild.contains(townMap));
    }

    private static boolean addNothing(Bag bag, Item item) {
        bag.add(item, 0);
        return bag.countOf(item) == 0;
    }

    private static void world(GameData data) {
        MiniTest.section("World map");

        MiniTest.check("The map has areas", data.getWorldMap().size() > 0);

        List<String> path = data.getWorldMap().shortestPath("pallet_town", "pewter_city");
        MiniTest.check("There is a route from Pallet Town to Pewter City", !path.isEmpty());
        MiniTest.checkEquals("The route starts where we asked", "pallet_town", path.get(0));
        MiniTest.checkEquals("The route ends where we asked", "pewter_city",
                path.get(path.size() - 1));

        List<String> samePlace = data.getWorldMap().shortestPath("pallet_town", "pallet_town");
        MiniTest.checkEquals("Travelling nowhere is a one-step route", 1, samePlace.size());

        MiniTest.check("An unknown destination has no route",
                data.getWorldMap().shortestPath("pallet_town", "atlantis").isEmpty());

        // Every step of the route must really be connected to the next one.
        boolean everyStepConnected = true;
        for (int i = 0; i < path.size() - 1; i++) {
            Area here = data.getWorldMap().get(path.get(i));
            if (!here.getConnections().contains(path.get(i + 1))) {
                everyStepConnected = false;
            }
        }
        MiniTest.check("Every step of the route is a real road", everyStepConnected);

        // Weighted encounters must always return something from the table.
        Area route1 = data.getWorldMap().get("route1");
        boolean alwaysValid = true;
        for (int roll = 0; roll < 200; roll++) {
            Encounter encounter = route1.rollEncounter(new FixedRandom(roll));
            if (encounter == null || encounter.minLevel() > encounter.maxLevel()) {
                alwaysValid = false;
            }
        }
        MiniTest.check("Rolling a wild encounter always gives a sensible result", alwaysValid);

        Area pallet = data.getWorldMap().get("pallet_town");
        MiniTest.check("Pallet Town has no wild Pokemon", !pallet.hasWildPokemon());
        MiniTest.check("Pallet Town has a Pokemon Center", pallet.hasPokemonCenter());
    }

    private static void saving(GameData data) {
        MiniTest.section("Saving and loading");

        Player player = new Player("Tester", "route1");
        player.setMoney(4321);
        player.getBag().add(data.item("potion"), 7);
        player.getBag().add(data.item("greatball"), 2);
        player.markDefeated("youngster_joey");

        Pokemon starter = Pokemon.create(data.species("squirtle"), 17, new FixedRandom(3, 11, 29));
        starter.setNickname("Shellshock");
        starter.takeDamage(6);
        starter.applyStatus(Status.PARALYSIS, new FixedRandom(1));
        player.addToParty(starter);
        player.recordCaught(data.species("squirtle"));

        try {
            Path file = Files.createTempFile("pokemon-save-test", ".txt");
            SaveManager.save(player, file);
            Player loaded = SaveManager.load(file, data);

            MiniTest.checkEquals("The name comes back", "Tester", loaded.getName());
            MiniTest.checkEquals("The money comes back", 4321, loaded.getMoney());
            MiniTest.checkEquals("The location comes back", "route1", loaded.getCurrentAreaId());
            MiniTest.checkEquals("The bag comes back", 7,
                    loaded.getBag().countOf(data.item("potion")));
            MiniTest.check("Defeated trainers come back",
                    loaded.hasDefeated("youngster_joey"));
            MiniTest.checkEquals("The party size comes back", 1, loaded.getParty().size());

            Pokemon reloaded = loaded.getParty().get(0);
            MiniTest.checkEquals("The nickname comes back", "Shellshock", reloaded.getNickname());
            MiniTest.checkEquals("The level comes back", 17, reloaded.getLevel());
            MiniTest.checkEquals("The current HP comes back", starter.getCurrentHp(),
                    reloaded.getCurrentHp());
            MiniTest.checkEquals("The status condition comes back", Status.PARALYSIS,
                    reloaded.getStatus());
            MiniTest.checkEquals("The exact stats come back", starter.getMaxHp(),
                    reloaded.getMaxHp());
            MiniTest.checkEquals("The moves come back", starter.getMoves().size(),
                    reloaded.getMoves().size());
            MiniTest.check("The Pokedex comes back",
                    loaded.hasCaught(data.species("squirtle")));

            Files.deleteIfExists(file);
        } catch (Exception e) {
            MiniTest.check("Saving and loading did not throw: " + e, false);
        }

        MiniTest.checkThrows("Loading a missing file gives a clear error",
                DataException.class,
                () -> SaveManager.load(Path.of("no-such-save-file-12345.txt"), data));
    }
}
