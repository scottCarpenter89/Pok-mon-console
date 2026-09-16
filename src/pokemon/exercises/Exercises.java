package pokemon.exercises;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pokemon.model.Move;
import pokemon.model.MoveSlot;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.model.Stats;
import pokemon.model.Type;
import pokemon.world.Area;
import pokemon.world.WorldMap;

/**
 * THE STUDENT WORKBOOK.
 *
 * <p>Every method below is unfinished. Replace the {@code TODO} in each one with
 * real code, then run:
 *
 * <pre>
 *   ./exercises.sh          (Mac or Linux)
 *   exercises.bat           (Windows)
 * </pre>
 *
 * <p>The runner tells you which exercises pass and gives you a hint for the ones
 * that do not. Work down the list - they are in order of difficulty and they follow
 * the units in {@code lessons/}.
 *
 * <p>Nothing else in the game calls these methods, so you cannot break your save
 * file by experimenting here.
 */
public class Exercises {

    // ==================================================================
    // UNIT 2-3   CS I   Variables, expressions, integer division
    // ==================================================================

    /**
     * EXERCISE 1. Add up all six base stats of a species.
     *
     * <p>Example: Bulbasaur is 45 + 49 + 49 + 65 + 65 + 45 = 318.
     *
     * <p>HINT: {@code stats.hp()}, {@code stats.attack()}, {@code stats.defense()},
     * {@code stats.spAttack()}, {@code stats.spDefense()}, {@code stats.speed()}.
     */
    public static int totalBaseStats(Stats stats) {
        return 0;   // TODO exercise 1
    }

    /**
     * EXERCISE 2. What percentage of its health does this Pokemon have left?
     *
     * <p>A Pokemon with 15 of 20 HP is at 75. Round DOWN to a whole number.
     *
     * <p>HINT: {@code current * 100 / max} in integers already rounds down - but be
     * careful about the ORDER. {@code current / max * 100} gives zero every time.
     * This is the integer-division trap from Unit 2.
     */
    public static int healthPercent(int currentHp, int maxHp) {
        return 0;   // TODO exercise 2
    }

    // ==================================================================
    // UNIT 4   CS I   Conditionals and boolean logic
    // ==================================================================

    /**
     * EXERCISE 3. Is this attacking type super effective against this defender?
     *
     * <p>"Super effective" means the multiplier is greater than 1.
     *
     * <p>HINT: {@code Type.effectiveness(attacking, defending)} returns a double.
     * You do NOT need an if-statement - a comparison already IS a boolean.
     */
    public static boolean isSuperEffective(Type attacking, Type defending) {
        return false;   // TODO exercise 3
    }

    /**
     * EXERCISE 4. Decide whether a Pokemon should be sent to the Center.
     *
     * <p>Return true when it has fainted, OR when it has less than a quarter of its
     * HP left. Otherwise return false.
     *
     * <p>HINT: this is one line with {@code ||}. Use {@code pokemon.isFainted()},
     * {@code pokemon.getCurrentHp()} and {@code pokemon.getMaxHp()}.
     */
    public static boolean needsHealing(Pokemon pokemon) {
        return false;   // TODO exercise 4
    }

    // ==================================================================
    // UNIT 5-6   CS I   Loops and arrays
    // ==================================================================

    /**
     * EXERCISE 5. Draw an HP bar such as {@code [#######...]}.
     *
     * <p>{@code width} is the total number of characters between the brackets. Use
     * '#' for the filled part and '.' for the empty part. A Pokemon at 15/20 HP with
     * a width of 10 gives {@code [########..]} (8 filled, because 15*10/20 = 7.5,
     * rounded to 8 - use {@code Math.round}).
     *
     * <p>HINT: build the String with a loop, or use a StringBuilder. Remember to add
     * the '[' and ']'.
     */
    public static String hpBar(int currentHp, int maxHp, int width) {
        return "";   // TODO exercise 5
    }

    /**
     * EXERCISE 6. Count how many Pokemon in a party have fainted.
     *
     * <p>HINT: the counting pattern - start a counter at 0, loop over the list, and
     * add 1 every time the {@code if} is true.
     */
    public static int countFainted(List<Pokemon> party) {
        return 0;   // TODO exercise 6
    }

    /**
     * EXERCISE 7. Find the Pokemon in the party with the highest Attack stat.
     *
     * <p>Return null for an empty list. If two are tied, return the earlier one.
     *
     * <p>HINT: the "find the maximum" pattern. Keep a {@code best} variable, loop,
     * and replace {@code best} whenever you find something bigger. Get the stat with
     * {@code pokemon.getStat(Stat.ATTACK)}.
     */
    public static Pokemon strongestAttacker(List<Pokemon> party) {
        return null;   // TODO exercise 7
    }

    /**
     * EXERCISE 8. Count how many of a Pokemon's moves it can still use.
     *
     * <p>A move is usable when it has PP remaining.
     *
     * <p>HINT: loop over {@code pokemon.getMoves()} and ask each {@link MoveSlot}
     * whether it {@code hasPp()}.
     */
    public static int usableMoveCount(Pokemon pokemon) {
        return 0;   // TODO exercise 8
    }

    // ==================================================================
    // UNIT 7   CS I   Methods and the damage formula
    // ==================================================================

    /**
     * EXERCISE 9. How much experience is won for defeating a Pokemon?
     *
     * <p>The formula this game uses is:
     * <pre>
     *   experience = baseExperience * level / 7
     *   and then multiplied by 1.5 (rounded down) if it was a trainer's Pokemon
     * </pre>
     *
     * <p>HINT: {@code defeated.getSpecies().getBaseExperience()} and
     * {@code defeated.getLevel()}. For the 1.5x, multiply by 3 and divide by 2 so
     * you stay in whole numbers.
     */
    public static int experienceReward(Pokemon defeated, boolean trainerBattle) {
        return 0;   // TODO exercise 9
    }

    // ==================================================================
    // UNIT 11   CS II   Collections: lists and maps
    // ==================================================================

    /**
     * EXERCISE 10. Count how many species have each primary type.
     *
     * <p>Given every species in the game, return a Map where the key is a type and
     * the value is how many species have that type as their FIRST type. Types with
     * no species should not appear in the map at all.
     *
     * <p>HINT: {@code map.merge(key, 1, Integer::sum)} adds one to a counter,
     * starting it at 1 if the key is new. Or do it the long way with
     * {@code containsKey} - both are fine.
     */
    public static Map<Type, Integer> countByPrimaryType(List<Species> allSpecies) {
        return new HashMap<>();   // TODO exercise 10
    }

    /**
     * EXERCISE 11. Return the names of every move a Pokemon knows, in order.
     *
     * <p>HINT: make an {@code ArrayList<String>}, loop over
     * {@code pokemon.getMoves()}, and add {@code slot.getMove().getName()}.
     */
    public static List<String> moveNames(Pokemon pokemon) {
        return new ArrayList<>();   // TODO exercise 11
    }

    // ==================================================================
    // UNIT 13   CS II   Recursion
    // ==================================================================

    /**
     * EXERCISE 12. How many times can this species still evolve?
     *
     * <p>Charmander returns 2 (Charmeleon, then Charizard). Charizard returns 0.
     * You must solve this RECURSIVELY - no loops.
     *
     * <p>HINT: the base case is "this species evolves into nothing, so the answer is
     * 0". Otherwise the answer is 1 + the answer for {@code getEvolvesInto()}.
     */
    public static int evolutionsRemaining(Species species) {
        return -1;   // TODO exercise 12
    }

    /**
     * EXERCISE 13. Add up the numbers from 1 to n, recursively.
     *
     * <p>{@code sumTo(4)} is 4 + 3 + 2 + 1 = 10. {@code sumTo(0)} is 0.
     *
     * <p>HINT: base case first, ALWAYS. What is the smallest n you can answer
     * without calling yourself?
     */
    public static int sumTo(int n) {
        return -1;   // TODO exercise 13
    }

    // ==================================================================
    // UNIT 14   CS II/III   Searching and sorting
    // ==================================================================

    /**
     * EXERCISE 14. Binary search a SORTED list of names.
     *
     * <p>Return the index of {@code target}, or -1 if it is not there.
     *
     * <p>HINT: keep {@code low} and {@code high}. Look at the middle. If the middle
     * is too small, move {@code low} past it; if it is too big, move {@code high}
     * before it. Use {@code names.get(middle).compareTo(target)}, which returns a
     * negative number, zero, or a positive number.
     */
    public static int binarySearch(List<String> sortedNames, String target) {
        return -1;   // TODO exercise 14
    }

    /**
     * EXERCISE 15. Sort a party from highest to lowest in one stat.
     *
     * <p>Sort the list IN PLACE (change the list you were given). Write the sort
     * yourself - selection sort or bubble sort - do not call
     * {@code Collections.sort}. The point is to write the algorithm once in your
     * life so you understand what the library is doing for you.
     *
     * <p>HINT: selection sort - for each position i, scan the rest of the list for
     * the biggest remaining value, then swap it into position i.
     */
    public static void sortByStatDescending(List<Pokemon> party, Stat stat) {
        // TODO exercise 15
    }

    // ==================================================================
    // UNIT 15   CS III   Data structures and graphs
    // ==================================================================

    /**
     * EXERCISE 16. List every area the player can reach from a starting area.
     *
     * <p>Include the starting area itself. The order does not matter, but each area
     * must appear only once.
     *
     * <p>HINT: this is breadth-first search. Use an {@code ArrayDeque<String>} as a
     * queue of areas to visit and a {@code List<String>} of the ones you have
     * already seen. Look at {@code WorldMap.shortestPath} for the shape of the code -
     * but do not just call it.
     *
     * <p>Get an area's neighbours with {@code map.get(id).getConnections()}.
     */
    public static List<String> reachableAreas(WorldMap map, String startId) {
        return new ArrayList<>();   // TODO exercise 16
    }

    /**
     * EXERCISE 17. Find the closest area from here that has a Pokemon Center.
     *
     * <p>Return its id, or null if none can be reached. "Closest" means the fewest
     * steps along the map's connections.
     *
     * <p>HINT: {@code map.shortestPath(from, to)} gives you the route between two
     * areas, and its {@code size()} tells you how long that route is. Loop over
     * {@code map.all()}, skip areas without a Center ({@code area.hasPokemonCenter()}),
     * and keep the shortest non-empty route. This is the "find the minimum" pattern
     * from Unit 5 applied to whole routes instead of numbers.
     */
    public static String nearestPokemonCenter(WorldMap map, String fromId) {
        return null;   // TODO exercise 17
    }

    // ==================================================================
    // A helper you may find useful. It is already written for you.
    // ==================================================================

    /** @return true when every character of the two strings matches. */
    protected static boolean sameText(String a, String b) {
        return a != null && a.equals(b);
    }

    /** Unused import guard - keeps the compiler quiet about Move and Area. */
    @SuppressWarnings("unused")
    private static void referenceTypes(Move move, Area area) {
    }
}
