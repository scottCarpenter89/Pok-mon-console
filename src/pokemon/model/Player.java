package pokemon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The human player.
 *
 * <p>TEACHING NOTE (CS II - extending a class):
 * {@code Player} IS-A {@link Trainer} - it inherits the party, the healing and the
 * switching - and then ADDS what only a player has: money, a bag, a location, a
 * Pokedex and a storage box. "Inherit what is shared, add what is special" is the
 * whole idea of inheritance in one sentence.
 */
public class Player extends Trainer {

    private int money;
    private String currentAreaId;
    private String lastCenterAreaId;

    private final Bag bag = new Bag();

    /** Species the player has SEEN, and species the player has CAUGHT. */
    private final List<String> seenSpecies = new ArrayList<>();
    private final List<String> caughtSpecies = new ArrayList<>();

    /** Overflow storage once the party of six is full. */
    private final List<Pokemon> storageBox = new ArrayList<>();

    /** Trainers already defeated, so they do not re-battle forever. */
    private final List<String> defeatedTrainers = new ArrayList<>();

    private int battlesWon;

    public Player(String name, String startingAreaId) {
        super(name);
        this.currentAreaId = startingAreaId;
        this.lastCenterAreaId = startingAreaId;
        this.money = 1500;
    }

    public int getMoney() {
        return money;
    }

    public void earn(int amount) {
        money += Math.max(0, amount);
    }

    /** @return false if the player cannot afford it, leaving the money untouched. */
    public boolean spend(int amount) {
        if (amount > money) {
            return false;
        }
        money -= amount;
        return true;
    }

    /** Blacking out costs half of the player's money (rounded down). */
    public int loseHalfMoney() {
        int lost = money / 2;
        money -= lost;
        return lost;
    }

    public Bag getBag() {
        return bag;
    }

    public String getCurrentAreaId() {
        return currentAreaId;
    }

    public void setCurrentAreaId(String currentAreaId) {
        this.currentAreaId = currentAreaId;
    }

    public String getLastCenterAreaId() {
        return lastCenterAreaId;
    }

    public void setLastCenterAreaId(String lastCenterAreaId) {
        this.lastCenterAreaId = lastCenterAreaId;
    }

    public void recordSeen(Species species) {
        if (!seenSpecies.contains(species.getId())) {
            seenSpecies.add(species.getId());
        }
    }

    public void recordCaught(Species species) {
        recordSeen(species);
        if (!caughtSpecies.contains(species.getId())) {
            caughtSpecies.add(species.getId());
        }
    }

    public boolean hasSeen(Species species) {
        return seenSpecies.contains(species.getId());
    }

    public boolean hasCaught(Species species) {
        return caughtSpecies.contains(species.getId());
    }

    public List<String> getSeenSpecies() {
        return seenSpecies;
    }

    public List<String> getCaughtSpecies() {
        return caughtSpecies;
    }

    public List<Pokemon> getStorageBox() {
        return storageBox;
    }

    /** Adds to the party if there is room, otherwise sends it to the box. */
    public boolean receive(Pokemon pokemon) {
        if (addToParty(pokemon)) {
            return true;
        }
        storageBox.add(pokemon);
        return false;
    }

    public void markDefeated(String trainerId) {
        if (!defeatedTrainers.contains(trainerId)) {
            defeatedTrainers.add(trainerId);
        }
    }

    public boolean hasDefeated(String trainerId) {
        return defeatedTrainers.contains(trainerId);
    }

    public List<String> getDefeatedTrainers() {
        return defeatedTrainers;
    }

    public int getBattlesWon() {
        return battlesWon;
    }

    public void addWin() {
        battlesWon++;
    }

    public void setBattlesWon(int battlesWon) {
        this.battlesWon = battlesWon;
    }

    public void setMoney(int money) {
        this.money = Math.max(0, money);
    }
}
