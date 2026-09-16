package pokemon.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The player's item bag.
 *
 * <p>TEACHING NOTE (CS II - the Map interface):
 * A bag is the textbook use for a {@code Map}: look something up by a key (the
 * item) and get a value (how many you own). Ask students how they would write this
 * with two parallel arrays ({@code String[] names} and {@code int[] counts}) and
 * what happens when they have to delete the item in the middle. The Map version is
 * shorter AND faster - a rare combination worth pointing out.
 *
 * <p>{@link LinkedHashMap} keeps items in the order they were first added, so the
 * bag menu does not shuffle itself between turns. Choosing the right Map
 * implementation is a CS III discussion.
 */
public class Bag {

    private final Map<Item, Integer> contents = new LinkedHashMap<>();

    public void add(Item item, int count) {
        if (count <= 0) {
            return;
        }
        contents.merge(item, count, Integer::sum);
    }

    /** @return true if the item was there to remove. */
    public boolean remove(Item item, int count) {
        Integer held = contents.get(item);
        if (held == null || held < count) {
            return false;
        }
        if (held == count) {
            contents.remove(item);
        } else {
            contents.put(item, held - count);
        }
        return true;
    }

    public boolean useOne(Item item) {
        return remove(item, 1);
    }

    public int countOf(Item item) {
        return contents.getOrDefault(item, 0);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public List<Item> items() {
        return new ArrayList<>(contents.keySet());
    }

    /** Only the items that make sense right now. */
    public List<Item> itemsUsableInBattle(boolean wildBattle) {
        List<Item> usable = new ArrayList<>();
        for (Item item : contents.keySet()) {
            if (!item.usableInBattle()) {
                continue;
            }
            if (item.getKind() == ItemKind.BALL && !wildBattle) {
                continue;   // You cannot catch another trainer's Pokemon!
            }
            usable.add(item);
        }
        return usable;
    }

    public Map<Item, Integer> asMap() {
        return contents;
    }
}
