package pokemon.world;

import pokemon.model.Species;

/**
 * One line of a route's wild-encounter table: which species, how common, and at
 * what levels.
 *
 * <p>TEACHING NOTE (CS II - weighted random choice):
 * {@code weight} is how many "tickets" this species has in the draw. A species with
 * weight 30 shows up three times as often as one with weight 10. See
 * {@link Area#rollEncounter} for the algorithm - it is a beautiful, short example of
 * turning a real-world idea into a loop.
 */
public record Encounter(Species species, int weight, int minLevel, int maxLevel) {
}
