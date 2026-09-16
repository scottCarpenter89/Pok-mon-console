package pokemon.model;

/**
 * How a move deals damage.
 *
 * <p>PHYSICAL moves use Attack vs Defense (tackling, biting, punching).
 * SPECIAL moves use Sp. Attack vs Sp. Defense (beams, flames, psychic energy).
 * STATUS moves deal no damage at all - they change the battle in another way.
 */
public enum Category {
    PHYSICAL, SPECIAL, STATUS;

    public static Category parse(String raw) {
        return valueOf(raw.trim().toUpperCase());
    }
}
