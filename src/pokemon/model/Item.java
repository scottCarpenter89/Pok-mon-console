package pokemon.model;

/**
 * One kind of item. Like {@link Species}, there is one Item object per kind and
 * the player's bag just counts how many they hold.
 */
public class Item {

    private final String id;
    private final String name;
    private final ItemKind kind;
    /** Meaning depends on the kind: HP restored, or a Poke Ball's catch bonus x10. */
    private final int power;
    private final int price;
    private final String description;

    public Item(String id, String name, ItemKind kind, int power, int price, String description) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.power = power;
        this.price = price;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemKind getKind() {
        return kind;
    }

    public int getPower() {
        return power;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    /** A Poke Ball's multiplier: stored as 10, 15, 20 so the data file holds no decimals. */
    public double ballBonus() {
        return power / 10.0;
    }

    public boolean usableInBattle() {
        return kind != ItemKind.KEY;
    }

    public boolean usableOutsideBattle() {
        return kind == ItemKind.POTION || kind == ItemKind.STATUS_HEAL || kind == ItemKind.REVIVE;
    }

    @Override
    public String toString() {
        return name;
    }
}
