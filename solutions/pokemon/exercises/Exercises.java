package pokemon.exercises;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
 * TEACHER'S COPY - the completed workbook.
 *
 * <p>Do not put this file in front of students until they have had a real attempt.
 * To mark a class set, copy this over {@code src/pokemon/exercises/Exercises.java},
 * run {@code ./exercises.sh}, and confirm everything reports DONE (that is exactly
 * what {@code ./check-solutions.sh} does).
 *
 * <p>Each solution below is written the way a strong student would write it, with a
 * note on the mistakes to expect and what to say about them.
 */
public class Exercises {

    // ==================================================================
    // UNIT 2-3   CS I
    // ==================================================================

    /** COMMON ERROR: returning only the last stat, or forgetting one of the six. */
    public static int totalBaseStats(Stats stats) {
        return stats.hp() + stats.attack() + stats.defense()
                + stats.spAttack() + stats.spDefense() + stats.speed();
    }

    /**
     * COMMON ERROR: {@code currentHp / maxHp * 100}, which is 0 every time because
     * the integer division happens first. This is THE integer-division lesson.
     */
    public static int healthPercent(int currentHp, int maxHp) {
        if (maxHp <= 0) {
            return 0;
        }
        return currentHp * 100 / maxHp;
    }

    // ==================================================================
    // UNIT 4   CS I
    // ==================================================================

    /** COMMON ERROR: writing {@code if (x > 1) return true; else return false;} */
    public static boolean isSuperEffective(Type attacking, Type defending) {
        return Type.effectiveness(attacking, defending) > 1.0;
    }

    /** COMMON ERROR: using {@code &&} instead of {@code ||}. */
    public static boolean needsHealing(Pokemon pokemon) {
        return pokemon.isFainted() || pokemon.getCurrentHp() < pokemon.getMaxHp() / 4;
    }

    // ==================================================================
    // UNIT 5-6   CS I
    // ==================================================================

    /** COMMON ERROR: forgetting the brackets, or an off-by-one in the empty part. */
    public static String hpBar(int currentHp, int maxHp, int width) {
        int filled = maxHp <= 0 ? 0 : (int) Math.round((double) currentHp / maxHp * width);
        filled = Math.max(0, Math.min(width, filled));

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? '#' : '.');
        }
        return bar.append(']').toString();
    }

    /** COMMON ERROR: declaring the counter inside the loop. */
    public static int countFainted(List<Pokemon> party) {
        int count = 0;
        for (Pokemon pokemon : party) {
            if (pokemon.isFainted()) {
                count++;
            }
        }
        return count;
    }

    /** COMMON ERROR: starting {@code best} at the first element without checking for empty. */
    public static Pokemon strongestAttacker(List<Pokemon> party) {
        Pokemon best = null;
        for (Pokemon pokemon : party) {
            if (best == null || pokemon.getStat(Stat.ATTACK) > best.getStat(Stat.ATTACK)) {
                best = pokemon;
            }
        }
        return best;
    }

    public static int usableMoveCount(Pokemon pokemon) {
        int count = 0;
        for (MoveSlot slot : pokemon.getMoves()) {
            if (slot.hasPp()) {
                count++;
            }
        }
        return count;
    }

    // ==================================================================
    // UNIT 7   CS I
    // ==================================================================

    /** COMMON ERROR: multiplying by 1.5 as a double and losing the int return type. */
    public static int experienceReward(Pokemon defeated, boolean trainerBattle) {
        int reward = defeated.getSpecies().getBaseExperience() * defeated.getLevel() / 7;
        if (trainerBattle) {
            reward = reward * 3 / 2;
        }
        return reward;
    }

    // ==================================================================
    // UNIT 11   CS II
    // ==================================================================

    public static Map<Type, Integer> countByPrimaryType(List<Species> allSpecies) {
        Map<Type, Integer> counts = new HashMap<>();
        for (Species species : allSpecies) {
            counts.merge(species.getPrimaryType(), 1, Integer::sum);
            // The long way, which is just as good:
            // Type key = species.getPrimaryType();
            // counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }

    public static List<String> moveNames(Pokemon pokemon) {
        List<String> names = new ArrayList<>();
        for (MoveSlot slot : pokemon.getMoves()) {
            names.add(slot.getMove().getName());
        }
        return names;
    }

    // ==================================================================
    // UNIT 13   CS II
    // ==================================================================

    /** COMMON ERROR: no base case, giving StackOverflowError. */
    public static int evolutionsRemaining(Species species) {
        if (species == null || species.getEvolvesInto() == null) {
            return 0;                                       // BASE CASE
        }
        return 1 + evolutionsRemaining(species.getEvolvesInto());
    }

    public static int sumTo(int n) {
        if (n <= 0) {
            return 0;                                       // BASE CASE
        }
        return n + sumTo(n - 1);
    }

    // ==================================================================
    // UNIT 14   CS II/III
    // ==================================================================

    /**
     * COMMON ERRORS: {@code while (low < high)} misses the last element, and
     * forgetting the {@code + 1} / {@code - 1} makes it loop forever.
     */
    public static int binarySearch(List<String> sortedNames, String target) {
        int low = 0;
        int high = sortedNames.size() - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            int direction = sortedNames.get(middle).compareTo(target);
            if (direction == 0) {
                return middle;
            }
            if (direction < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }

    /** Selection sort, biggest first. Bubble sort is equally acceptable. */
    public static void sortByStatDescending(List<Pokemon> party, Stat stat) {
        for (int i = 0; i < party.size() - 1; i++) {
            int biggest = i;
            for (int j = i + 1; j < party.size(); j++) {
                if (party.get(j).getStat(stat) > party.get(biggest).getStat(stat)) {
                    biggest = j;
                }
            }
            Pokemon temporary = party.get(i);
            party.set(i, party.get(biggest));
            party.set(biggest, temporary);
        }
    }

    // ==================================================================
    // UNIT 15   CS III
    // ==================================================================

    /** Breadth-first search. A stack instead of a queue would also visit everything. */
    public static List<String> reachableAreas(WorldMap map, String startId) {
        List<String> found = new ArrayList<>();
        if (map.get(startId) == null) {
            return found;
        }
        Deque<String> toVisit = new ArrayDeque<>();
        toVisit.add(startId);
        found.add(startId);

        while (!toVisit.isEmpty()) {
            String current = toVisit.poll();
            for (String next : map.get(current).getConnections()) {
                if (map.get(next) != null && !found.contains(next)) {
                    found.add(next);
                    toVisit.add(next);
                }
            }
        }
        return found;
    }

    /** "Find the minimum" applied to whole routes. */
    public static String nearestPokemonCenter(WorldMap map, String fromId) {
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Area area : map.all()) {
            if (!area.hasPokemonCenter()) {
                continue;
            }
            List<String> path = map.shortestPath(fromId, area.getId());
            if (!path.isEmpty() && path.size() < bestDistance) {
                bestDistance = path.size();
                best = area.getId();
            }
        }
        return best;
    }

    protected static boolean sameText(String a, String b) {
        return a != null && a.equals(b);
    }

    @SuppressWarnings("unused")
    private static void referenceTypes(Move move, Area area) {
    }
}
