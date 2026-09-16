package pokemon.exercises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pokemon.battle.BattleAI;
import pokemon.data.DataLoader;
import pokemon.data.GameData;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.model.Stats;
import pokemon.model.Type;
import pokemon.util.FixedRandom;
import pokemon.util.Text;
import pokemon.world.Area;
import pokemon.world.WorldMap;

/**
 * Marks the student workbook and explains what went wrong.
 *
 * <pre>
 *   ./exercises.sh        or        java -cp out pokemon.exercises.ExerciseRunner
 * </pre>
 *
 * <p>TEACHING NOTE: this is the same idea as the test suite in {@code test/}, aimed
 * at a student instead of a developer. Immediate, specific feedback is the single
 * most effective thing a programming exercise can give, and it costs you no marking
 * time at all.
 */
public final class ExerciseRunner {

    private static final List<String> passed = new ArrayList<>();
    private static final List<String> failed = new ArrayList<>();
    private static String currentExercise = "";
    private static boolean currentOk = true;
    private static String currentHint = "";

    public static void main(String[] args) {
        GameData data = DataLoader.loadDefault();

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("  POKEMON CONSOLE RPG - EXERCISE WORKBOOK");
        System.out.println("  Edit src/pokemon/exercises/Exercises.java, then run this again.");
        System.out.println("=".repeat(70));

        exercise01(data);
        exercise02();
        exercise03();
        exercise04(data);
        exercise05();
        exercise06(data);
        exercise07(data);
        exercise08(data);
        exercise09(data);
        exercise10(data);
        exercise11(data);
        exercise12(data);
        exercise13();
        exercise14();
        exercise15(data);
        exercise16(data);
        exercise17(data);
        exercise18(data);

        summary();
    }

    // ==================================================================
    // Marking helpers
    // ==================================================================

    private static void start(String name, String hint) {
        currentExercise = name;
        currentOk = true;
        currentHint = hint;
    }

    private static void expect(String what, Object expected, Object actual) {
        if (!currentOk) {
            return;         // Report only the first failure of each exercise.
        }
        boolean same = expected == null ? actual == null : expected.equals(actual);
        if (!same) {
            currentOk = false;
            currentHint = currentHint + "\n         " + what
                    + "\n         expected: " + expected + "\n         your answer: " + actual;
        }
    }

    private static void expectTrue(String what, boolean condition) {
        expect(what, true, condition);
    }

    private static void finish() {
        if (currentOk) {
            passed.add(currentExercise);
            System.out.println("  DONE    " + currentExercise);
        } else {
            failed.add(currentExercise);
            System.out.println("  TODO    " + currentExercise);
            System.out.println("         " + currentHint);
        }
    }

    /** Runs one exercise, turning a crash into a normal failure with advice. */
    private static void attempt(Runnable body) {
        try {
            body.run();
        } catch (StackOverflowError e) {
            currentOk = false;
            currentHint = "Your recursion never stopped (StackOverflowError). "
                    + "Check your base case - the condition that returns WITHOUT calling itself.";
        } catch (Throwable t) {
            currentOk = false;
            currentHint = "Your code threw " + t.getClass().getSimpleName()
                    + (t.getMessage() == null ? "" : ": " + t.getMessage())
                    + "\n         " + currentHint;
        }
        finish();
    }

    // ==================================================================
    // The exercises
    // ==================================================================

    private static void exercise01(GameData data) {
        start("1.  totalBaseStats      (CS I - expressions)",
                "Add the six values together and return the sum.");
        attempt(() -> {
            Stats bulbasaur = data.species("bulbasaur").getBaseStats();
            expect("Bulbasaur's base stat total", 318, Exercises.totalBaseStats(bulbasaur));
            expect("A zero species totals zero", 0,
                    Exercises.totalBaseStats(new Stats(0, 0, 0, 0, 0, 0)));
            expect("Onix's base stat total", 385,
                    Exercises.totalBaseStats(data.species("onix").getBaseStats()));
        });
    }

    private static void exercise02() {
        start("2.  healthPercent       (CS I - integer division)",
                "Multiply BEFORE you divide: current * 100 / max.");
        attempt(() -> {
            expect("15 of 20 HP", 75, Exercises.healthPercent(15, 20));
            expect("full health", 100, Exercises.healthPercent(20, 20));
            expect("fainted", 0, Exercises.healthPercent(0, 20));
            expect("1 of 3 HP rounds down", 33, Exercises.healthPercent(1, 3));
        });
    }

    private static void exercise03() {
        start("3.  isSuperEffective    (CS I - booleans)",
                "Return the comparison itself - no if-statement needed.");
        attempt(() -> {
            expectTrue("Fire beats Grass", Exercises.isSuperEffective(Type.FIRE, Type.GRASS));
            expect("Fire does not beat Water", false,
                    Exercises.isSuperEffective(Type.FIRE, Type.WATER));
            expect("Normal does not beat Normal", false,
                    Exercises.isSuperEffective(Type.NORMAL, Type.NORMAL));
            expect("Immunity is not super effective", false,
                    Exercises.isSuperEffective(Type.ELECTRIC, Type.GROUND));
        });
    }

    private static void exercise04(GameData data) {
        start("4.  needsHealing        (CS I - || and comparisons)",
                "Two conditions joined with ||: fainted, or below a quarter health.");
        attempt(() -> {
            Pokemon healthy = new Pokemon(data.species("pikachu"), 20, perfect());
            expect("A healthy Pokemon is fine", false, Exercises.needsHealing(healthy));

            Pokemon hurt = new Pokemon(data.species("pikachu"), 20, perfect());
            hurt.takeDamage(hurt.getMaxHp() * 4 / 5);       // 20% left
            expectTrue("A badly hurt Pokemon needs healing", Exercises.needsHealing(hurt));

            Pokemon scratched = new Pokemon(data.species("pikachu"), 20, perfect());
            scratched.takeDamage(scratched.getMaxHp() / 2); // 50% left
            expect("A scratched Pokemon is fine", false, Exercises.needsHealing(scratched));

            Pokemon fainted = new Pokemon(data.species("pikachu"), 20, perfect());
            fainted.takeDamage(99999);
            expectTrue("A fainted Pokemon needs healing", Exercises.needsHealing(fainted));
        });
    }

    private static void exercise05() {
        start("5.  hpBar               (CS I - loops and Strings)",
                "Math.round((double) currentHp / maxHp * width) gives the filled count.");
        attempt(() -> {
            expect("15/20 at width 10", "[########..]", Exercises.hpBar(15, 20, 10));
            expect("full bar", "[##########]", Exercises.hpBar(20, 20, 10));
            expect("empty bar", "[..........]", Exercises.hpBar(0, 20, 10));
            expect("half a width-4 bar", "[##..]", Exercises.hpBar(10, 20, 4));
        });
    }

    private static void exercise06(GameData data) {
        start("6.  countFainted        (CS I - counting loop)",
                "Counter starts at 0 outside the loop, adds 1 inside the if.");
        attempt(() -> {
            List<Pokemon> party = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                party.add(new Pokemon(data.species("rattata"), 10, perfect()));
            }
            expect("Nobody has fainted yet", 0, Exercises.countFainted(party));
            party.get(0).takeDamage(99999);
            party.get(2).takeDamage(99999);
            expect("Two have fainted", 2, Exercises.countFainted(party));
            expect("An empty party has none", 0, Exercises.countFainted(new ArrayList<>()));
        });
    }

    private static void exercise07(GameData data) {
        start("7.  strongestAttacker   (CS I - find the maximum)",
                "Keep a 'best so far' variable and replace it when you find something bigger.");
        attempt(() -> {
            Pokemon weak = new Pokemon(data.species("caterpie"), 10, perfect());
            Pokemon strong = new Pokemon(data.species("machop"), 10, perfect());
            Pokemon middle = new Pokemon(data.species("pidgey"), 10, perfect());
            List<Pokemon> party = new ArrayList<>(Arrays.asList(weak, strong, middle));

            expect("Machop has the highest Attack", strong, Exercises.strongestAttacker(party));
            expect("It works when the best one is first",
                    strong, Exercises.strongestAttacker(new ArrayList<>(
                            Arrays.asList(strong, weak, middle))));
            expect("It works when the best one is last",
                    strong, Exercises.strongestAttacker(new ArrayList<>(
                            Arrays.asList(weak, middle, strong))));
            expect("An empty party has no strongest", null,
                    Exercises.strongestAttacker(new ArrayList<>()));
        });
    }

    private static void exercise08(GameData data) {
        start("8.  usableMoveCount     (CS I - loop with a condition)",
                "Loop over pokemon.getMoves() and count the slots where hasPp() is true.");
        attempt(() -> {
            Pokemon fighter = new Pokemon(data.species("pikachu"), 20, perfect());
            fighter.learnMove(data.move("thunder_shock"));
            fighter.learnMove(data.move("quick_attack"));
            fighter.learnMove(data.move("tail_whip"));
            expect("All three moves are fresh", 3, Exercises.usableMoveCount(fighter));

            MoveSlot first = fighter.getMoves().get(0);
            while (first.hasPp()) {
                first.spendPp();
            }
            expect("One move is now spent", 2, Exercises.usableMoveCount(fighter));

            Pokemon moveless = new Pokemon(data.species("pikachu"), 20, perfect());
            expect("A Pokemon with no moves has none usable", 0,
                    Exercises.usableMoveCount(moveless));
        });
    }

    private static void exercise09(GameData data) {
        start("9.  experienceReward    (CS I - translating a formula)",
                "baseExperience * level / 7, then * 3 / 2 for a trainer battle.");
        attempt(() -> {
            Pokemon pidgey = new Pokemon(data.species("pidgey"), 10, perfect());
            expect("Wild Pidgey at level 10", 50 * 10 / 7,
                    Exercises.experienceReward(pidgey, false));
            expect("The same Pidgey on a trainer's team", (50 * 10 / 7) * 3 / 2,
                    Exercises.experienceReward(pidgey, true));

            Pokemon onix = new Pokemon(data.species("onix"), 20, perfect());
            expect("Wild Onix at level 20", 77 * 20 / 7,
                    Exercises.experienceReward(onix, false));
        });
    }

    private static void exercise10(GameData data) {
        start("10. countByPrimaryType  (CS II - Map)",
                "map.merge(type, 1, Integer::sum) counts nicely.");
        attempt(() -> {
            Map<Type, Integer> expected = new HashMap<>();
            for (Species species : data.allSpecies()) {
                expected.merge(species.getPrimaryType(), 1, Integer::sum);
            }
            Map<Type, Integer> actual = Exercises.countByPrimaryType(data.allSpecies());
            expect("The map has the right number of types", expected.size(), actual.size());
            for (Map.Entry<Type, Integer> entry : expected.entrySet()) {
                expect("Count of " + entry.getKey().displayName(), entry.getValue(),
                        actual.get(entry.getKey()));
            }
            expect("An empty list gives an empty map", 0,
                    Exercises.countByPrimaryType(new ArrayList<>()).size());
        });
    }

    private static void exercise11(GameData data) {
        start("11. moveNames           (CS II - building a List)",
                "new ArrayList<>(), then add() inside the loop, then return it.");
        attempt(() -> {
            Pokemon pikachu = new Pokemon(data.species("pikachu"), 20, perfect());
            pikachu.learnMove(data.move("thunder_shock"));
            pikachu.learnMove(data.move("quick_attack"));
            expect("Two moves, in order",
                    Arrays.asList("Thunder Shock", "Quick Attack"), Exercises.moveNames(pikachu));
            expect("No moves gives an empty list", new ArrayList<String>(),
                    Exercises.moveNames(new Pokemon(data.species("pikachu"), 5, perfect())));
        });
    }

    private static void exercise12(GameData data) {
        start("12. evolutionsRemaining (CS II - recursion)",
                "Base case: getEvolvesInto() == null returns 0.");
        attempt(() -> {
            expect("Charmander has two evolutions left", 2,
                    Exercises.evolutionsRemaining(data.species("charmander")));
            expect("Charmeleon has one", 1,
                    Exercises.evolutionsRemaining(data.species("charmeleon")));
            expect("Charizard has none", 0,
                    Exercises.evolutionsRemaining(data.species("charizard")));
            expect("Onix never evolves", 0,
                    Exercises.evolutionsRemaining(data.species("onix")));
        });
    }

    private static void exercise13() {
        start("13. sumTo               (CS II - recursion)",
                "sumTo(0) is 0. Otherwise n + sumTo(n - 1).");
        attempt(() -> {
            expect("sumTo(0)", 0, Exercises.sumTo(0));
            expect("sumTo(1)", 1, Exercises.sumTo(1));
            expect("sumTo(4)", 10, Exercises.sumTo(4));
            expect("sumTo(100)", 5050, Exercises.sumTo(100));
        });
    }

    private static void exercise14() {
        start("14. binarySearch        (CS II/III - algorithms)",
                "low = 0, high = size - 1, and loop while low <= high.");
        attempt(() -> {
            List<String> names = Arrays.asList("Abra", "Bulbasaur", "Charmander", "Diglett",
                    "Eevee", "Gastly", "Machop", "Onix", "Pikachu", "Zubat");
            expect("Find the first", 0, Exercises.binarySearch(names, "Abra"));
            expect("Find the last", 9, Exercises.binarySearch(names, "Zubat"));
            expect("Find one in the middle", 4, Exercises.binarySearch(names, "Eevee"));
            expect("Find another", 6, Exercises.binarySearch(names, "Machop"));
            expect("Report a missing name", -1, Exercises.binarySearch(names, "Mewtwo"));
            expect("An empty list finds nothing", -1,
                    Exercises.binarySearch(new ArrayList<>(), "Eevee"));
        });
    }

    private static void exercise15(GameData data) {
        start("15. sortByStatDescending(CS III - write a sort)",
                "Selection sort: for each i, find the biggest in the rest and swap it in.");
        attempt(() -> {
            List<Pokemon> party = new ArrayList<>();
            party.add(new Pokemon(data.species("caterpie"), 20, perfect()));   // slow-ish
            party.add(new Pokemon(data.species("pikachu"), 20, perfect()));    // fastest
            party.add(new Pokemon(data.species("geodude"), 20, perfect()));    // slowest
            party.add(new Pokemon(data.species("rattata"), 20, perfect()));

            Exercises.sortByStatDescending(party, Stat.SPEED);
            expect("The list still has everyone", 4, party.size());

            boolean ordered = true;
            for (int i = 0; i < party.size() - 1; i++) {
                if (party.get(i).getStat(Stat.SPEED) < party.get(i + 1).getStat(Stat.SPEED)) {
                    ordered = false;
                }
            }
            expectTrue("Speeds run from highest to lowest", ordered);
            expect("The fastest is first", "Pikachu", party.get(0).getSpecies().getName());
            expect("The slowest is last", "Geodude",
                    party.get(party.size() - 1).getSpecies().getName());
        });
    }

    private static void exercise16(GameData data) {
        start("16. reachableAreas      (CS III - breadth-first search)",
                "Queue of areas to visit + list of areas already seen.");
        attempt(() -> {
            WorldMap map = data.getWorldMap();
            List<String> reachable = Exercises.reachableAreas(map, "pallet_town");
            expectTrue("It includes where you started", reachable.contains("pallet_town"));
            expectTrue("It includes a far-off city", reachable.contains("cerulean_city"));
            expect("It finds every area in this map", map.size(), reachable.size());

            List<String> unique = new ArrayList<>();
            for (String id : reachable) {
                if (!unique.contains(id)) {
                    unique.add(id);
                }
            }
            expect("No area is listed twice", reachable.size(), unique.size());
        });
    }

    private static void exercise17(GameData data) {
        start("17. nearestPokemonCenter(CS III - search over a graph)",
                "Loop over map.all(), skip areas with no Center, keep the shortest route.");
        attempt(() -> {
            WorldMap map = data.getWorldMap();
            for (String from : List.of("route1", "viridian_forest", "mt_moon", "pallet_town")) {
                String answer = Exercises.nearestPokemonCenter(map, from);
                expectTrue("There is an answer for " + from, answer != null);
                if (answer == null) {
                    return;
                }
                Area found = map.get(answer);
                expectTrue(answer + " is a real area", found != null);
                if (found == null) {
                    return;
                }
                expectTrue(answer + " has a Pokemon Center", found.hasPokemonCenter());
                expect("The Center found from " + from + " is the closest one",
                        shortestCenterDistance(map, from), map.shortestPath(from, answer).size());
            }
        });
    }

    private static void exercise18(GameData data) {
        start("18. MyFirstAI           (CS II/III - implement an interface)",
                "See src/pokemon/exercises/MyFirstAI.java - pick the strongest usable move.");
        attempt(() -> {
            BattleAI ai = new MyFirstAI();
            Pokemon fighter = new Pokemon(data.species("charizard"), 40, perfect());
            fighter.learnMove(data.move("growl"));          // status, power 0
            fighter.learnMove(data.move("ember"));          // power 40
            fighter.learnMove(data.move("flamethrower"));   // power 90
            fighter.learnMove(data.move("wing_attack"));    // power 60
            Pokemon target = new Pokemon(data.species("venusaur"), 40, perfect());

            MoveSlot choice = ai.chooseMove(fighter, target, new FixedRandom(1));
            expectTrue("It chose something", choice != null);
            if (choice == null) {
                return;
            }
            expect("It chose the strongest move", "Flamethrower", choice.getMove().getName());

            // Drain the best move: it should fall back to the next strongest.
            MoveSlot best = fighter.getMoves().get(2);
            while (best.hasPp()) {
                best.spendPp();
            }
            MoveSlot second = ai.chooseMove(fighter, target, new FixedRandom(1));
            expectTrue("It never chooses a move with no PP", second != null && second.hasPp());
            if (second != null) {
                expect("It falls back to the next strongest", "Wing Attack",
                        second.getMove().getName());
            }

            // Drain everything: it must give up and return null.
            for (MoveSlot slot : fighter.getMoves()) {
                while (slot.hasPp()) {
                    slot.spendPp();
                }
            }
            expect("With no PP at all it returns null", null,
                    ai.chooseMove(fighter, target, new FixedRandom(1)));
        });
    }

    // ==================================================================
    // Helpers used by the marking code
    // ==================================================================

    /** Perfect IVs, so every check gets predictable numbers. */
    private static Stats perfect() {
        return new Stats(31, 31, 31, 31, 31, 31);
    }

    private static int shortestCenterDistance(WorldMap map, String from) {
        int best = Integer.MAX_VALUE;
        for (Area area : map.all()) {
            if (!area.hasPokemonCenter()) {
                continue;
            }
            List<String> path = map.shortestPath(from, area.getId());
            if (!path.isEmpty()) {
                best = Math.min(best, path.size());
            }
        }
        return best;
    }

    private static void summary() {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("  " + passed.size() + " of " + (passed.size() + failed.size())
                + " exercises complete");
        if (failed.isEmpty()) {
            System.out.println("  Everything passes. Now go and add a feature to the game!");
        } else {
            System.out.println("  Next up: " + failed.get(0).trim());
        }
        System.out.println("  " + Text.repeat('#', Math.max(0, passed.size() * 2))
                + Text.repeat('.', Math.max(0, failed.size() * 2)));
        System.out.println("=".repeat(70));
    }
}
