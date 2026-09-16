package pokemon.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pokemon.model.Item;
import pokemon.model.Move;
import pokemon.model.Species;
import pokemon.world.TrainerTemplate;
import pokemon.world.WorldMap;

/**
 * Everything the game loaded from {@code data/}: the species, moves, items, map and
 * trainers, all in one place.
 *
 * <p>TEACHING NOTE (CS II - Maps as lookup tables):
 * Each registry is a {@code Map<String, Something>} keyed by ID. Looking up
 * "charmander" in a Map of 40 species takes the same time as looking it up in a Map
 * of 40,000 - that is the promise of a hash table, and it is worth timing in class
 * against a linear search through an ArrayList (see the Trainer School lesson on
 * searching).
 */
public class GameData {

    private final Map<String, Species> species = new LinkedHashMap<>();
    private final Map<String, Move> moves = new LinkedHashMap<>();
    private final Map<String, Item> items = new LinkedHashMap<>();
    private final Map<String, TrainerTemplate> trainers = new LinkedHashMap<>();
    private final WorldMap worldMap = new WorldMap();
    private final List<String> starterIds = new ArrayList<>();

    // ---- registration (used by the loader) ----

    public void addSpecies(Species value) {
        species.put(value.getId(), value);
    }

    public void addMove(Move value) {
        moves.put(value.getId(), value);
    }

    public void addItem(Item value) {
        items.put(value.getId(), value);
    }

    public void addTrainer(TrainerTemplate value) {
        trainers.put(value.getId(), value);
    }

    public void addStarter(String speciesId) {
        starterIds.add(speciesId);
    }

    // ---- lookup ----

    /**
     * @throws DataException when the ID is unknown - which catches typos in the data
     *         files at start-up instead of halfway through a battle.
     */
    public Species species(String id) {
        Species found = species.get(id);
        if (found == null) {
            throw new DataException("No species with id '" + id + "'");
        }
        return found;
    }

    public Move move(String id) {
        Move found = moves.get(id);
        if (found == null) {
            throw new DataException("No move with id '" + id + "'");
        }
        return found;
    }

    public Item item(String id) {
        Item found = items.get(id);
        if (found == null) {
            throw new DataException("No item with id '" + id + "'");
        }
        return found;
    }

    public TrainerTemplate trainer(String id) {
        TrainerTemplate found = trainers.get(id);
        if (found == null) {
            throw new DataException("No trainer with id '" + id + "'");
        }
        return found;
    }

    public boolean hasSpecies(String id) {
        return species.containsKey(id);
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public List<Species> allSpecies() {
        return new ArrayList<>(species.values());
    }

    public List<Move> allMoves() {
        return new ArrayList<>(moves.values());
    }

    public List<Item> allItems() {
        return new ArrayList<>(items.values());
    }

    public List<TrainerTemplate> allTrainers() {
        return new ArrayList<>(trainers.values());
    }

    public List<String> getStarterIds() {
        return starterIds;
    }

    public List<Species> starters() {
        List<Species> list = new ArrayList<>();
        for (String id : starterIds) {
            list.add(species(id));
        }
        return list;
    }

    /** A one-line summary printed at start-up so a teacher can see the data loaded. */
    public String summary() {
        return species.size() + " species, " + moves.size() + " moves, "
                + items.size() + " items, " + worldMap.size() + " areas, "
                + trainers.size() + " trainers";
    }
}
