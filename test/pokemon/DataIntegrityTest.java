package pokemon;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Move;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.util.FixedRandom;
import pokemon.world.Area;
import pokemon.world.Encounter;
import pokemon.world.TrainerTemplate;

/**
 * Checks the CSV files themselves.
 *
 * <p>TEACHING NOTE (CS III - testing your DATA, not just your code):
 * Most classroom bugs in a data-driven project are typos in a spreadsheet, not
 * mistakes in Java. These tests catch a bad level range or an unreachable town the
 * moment a student adds one - which is much kinder than finding out during a demo.
 * When students add their own species, this file is what tells them they forgot the
 * learnset.
 */
public class DataIntegrityTest {

    public static void run(GameData data) {
        MiniTest.section("Data files");

        MiniTest.check("Species loaded", data.allSpecies().size() >= 20);
        MiniTest.check("Moves loaded", data.allMoves().size() >= 20);
        MiniTest.check("Items loaded", !data.allItems().isEmpty());
        MiniTest.check("Areas loaded", data.getWorldMap().size() >= 4);
        MiniTest.check("Trainers loaded", !data.allTrainers().isEmpty());
        MiniTest.checkEquals("There are three starters", 3, data.starters().size());

        List<String> problems = new ArrayList<>();

        for (Species species : data.allSpecies()) {
            if (species.movesKnownAt(1).isEmpty()) {
                problems.add(species.getId() + " knows no moves at level 1");
            }
            if (species.getBaseStats().total() <= 0) {
                problems.add(species.getId() + " has no base stats");
            }
            if (species.getCatchRate() < 1 || species.getCatchRate() > 255) {
                problems.add(species.getId() + " has an impossible catch rate");
            }
            if (species.getEvolvesInto() != null && species.getEvolutionLevel() <= 1) {
                problems.add(species.getId() + " evolves at an impossible level");
            }
            if (species.getEvolvesInto() == species) {
                problems.add(species.getId() + " evolves into itself");
            }
        }
        MiniTest.check("Every species is playable" + describe(problems), problems.isEmpty());

        problems.clear();
        for (Move move : data.allMoves()) {
            if (move.getMaxPp() <= 0) {
                problems.add(move.getId() + " has no PP");
            }
            if (move.getAccuracy() > 100) {
                problems.add(move.getId() + " has accuracy above 100");
            }
            if (move.isDamaging() && move.getPower() > 200) {
                problems.add(move.getId() + " is absurdly powerful");
            }
            if (move.getEffectChance() < 0 || move.getEffectChance() > 100) {
                problems.add(move.getId() + " has an impossible effect chance");
            }
        }
        MiniTest.check("Every move is sensible" + describe(problems), problems.isEmpty());

        problems.clear();
        for (Area area : data.getWorldMap().all()) {
            for (Encounter encounter : area.getEncounters()) {
                if (encounter.minLevel() > encounter.maxLevel()) {
                    problems.add(area.getId() + " has a backwards level range for "
                            + encounter.species().getId());
                }
                if (encounter.minLevel() < 1) {
                    problems.add(area.getId() + " has a level below 1");
                }
                if (encounter.weight() <= 0) {
                    problems.add(area.getId() + " has a zero-weight encounter");
                }
            }
        }
        MiniTest.check("Every encounter table is sensible" + describe(problems),
                problems.isEmpty());

        // Can the player actually walk from the start to every other area?
        problems.clear();
        for (Area area : data.getWorldMap().all()) {
            if (data.getWorldMap().shortestPath("pallet_town", area.getId()).isEmpty()) {
                problems.add(area.getId() + " cannot be reached from Pallet Town");
            }
        }
        MiniTest.check("Every area is reachable from the start" + describe(problems),
                problems.isEmpty());

        // Every trainer must be beatable: real species, sane levels, real moves.
        problems.clear();
        for (TrainerTemplate template : data.allTrainers()) {
            if (template.getTeam().isEmpty()) {
                problems.add(template.getId() + " has no Pokemon");
            }
            for (TrainerTemplate.TeamEntry entry : template.getTeam()) {
                if (entry.level() < 1 || entry.level() > 100) {
                    problems.add(template.getId() + " has an impossible level");
                }
                Pokemon built = Pokemon.create(entry.species(), entry.level(), new FixedRandom(7));
                if (built.getMoves().isEmpty()) {
                    problems.add(template.getId() + "'s " + entry.species().getId()
                            + " would enter battle with no moves");
                }
            }
        }
        MiniTest.check("Every trainer can actually battle" + describe(problems),
                problems.isEmpty());

        // A shop that sells nothing, or an area that says it has a shop but stocks
        // nothing, is a dead end for the player.
        problems.clear();
        for (Area area : data.getWorldMap().all()) {
            if (area.hasShop() && area.getShopItemIds().isEmpty()) {
                problems.add(area.getId() + " claims to have a shop but sells nothing");
            }
            if (!area.hasShop() && !area.getShopItemIds().isEmpty()) {
                problems.add(area.getId() + " stocks items but has no shop");
            }
        }
        MiniTest.check("Every shop works" + describe(problems), problems.isEmpty());

        // Wild Pokemon at every level in every table must be buildable.
        problems.clear();
        for (Area area : data.getWorldMap().all()) {
            for (Encounter encounter : area.getEncounters()) {
                for (int level = encounter.minLevel(); level <= encounter.maxLevel(); level++) {
                    Pokemon wild = Pokemon.create(encounter.species(), level, new FixedRandom(3));
                    if (wild.getMoves().isEmpty()) {
                        problems.add(encounter.species().getId() + " at level " + level
                                + " in " + area.getId() + " would have no moves");
                    }
                }
            }
        }
        MiniTest.check("Every wild Pokemon can fight back" + describe(problems),
                problems.isEmpty());
    }

    /** Adds the first few problems to the test name so a failure explains itself. */
    private static String describe(List<String> problems) {
        if (problems.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder(" -- " + problems.size() + " problem(s): ");
        for (int i = 0; i < Math.min(3, problems.size()); i++) {
            text.append(problems.get(i)).append("; ");
        }
        return text.toString();
    }
}
