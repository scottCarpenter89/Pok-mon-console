package pokemon.world;

import java.util.ArrayList;
import java.util.List;
import pokemon.util.RandomSource;

/**
 * One place on the map: a town, a route or a cave.
 *
 * <p>TEACHING NOTE (CS III - graphs):
 * Each Area stores the IDs of the areas it connects to. Together, the areas form a
 * <b>graph</b>: nodes joined by edges. Drawing the world map on the board and then
 * showing the same map as {@code data/areas.csv} is a memorable way to introduce
 * graphs long before students meet them in a textbook.
 */
public class Area {

    private final String id;
    private final String name;
    private final String description;
    private final boolean hasPokemonCenter;
    private final boolean hasShop;
    private final List<String> connections = new ArrayList<>();
    private final List<Encounter> encounters = new ArrayList<>();
    private final List<String> trainerIds = new ArrayList<>();
    private final List<String> shopItemIds = new ArrayList<>();

    public Area(String id, String name, String description,
                boolean hasPokemonCenter, boolean hasShop) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hasPokemonCenter = hasPokemonCenter;
        this.hasShop = hasShop;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasPokemonCenter() {
        return hasPokemonCenter;
    }

    public boolean hasShop() {
        return hasShop;
    }

    public List<String> getConnections() {
        return connections;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public List<String> getTrainerIds() {
        return trainerIds;
    }

    public List<String> getShopItemIds() {
        return shopItemIds;
    }

    public boolean hasWildPokemon() {
        return !encounters.isEmpty();
    }

    /**
     * Picks a random wild Pokemon for this area, respecting the weights.
     *
     * <p>THE ALGORITHM: add up every weight, pick a random number below that total,
     * then walk the list subtracting as you go. Whichever entry drops the number to
     * zero or below is the winner. Students can act this out with index cards.
     *
     * @return the chosen encounter, or null if this area has no wild Pokemon
     */
    public Encounter rollEncounter(RandomSource rng) {
        if (encounters.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (Encounter encounter : encounters) {
            totalWeight += encounter.weight();
        }
        int ticket = rng.nextInt(Math.max(1, totalWeight));
        for (Encounter encounter : encounters) {
            ticket -= encounter.weight();
            if (ticket < 0) {
                return encounter;
            }
        }
        return encounters.get(encounters.size() - 1);   // Should never happen.
    }

    @Override
    public String toString() {
        return name;
    }
}
