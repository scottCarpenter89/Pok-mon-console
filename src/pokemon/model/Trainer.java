package pokemon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Anything that can battle: the player, a rival, a route trainer.
 *
 * <p>TEACHING NOTE (CS II - inheritance and abstract classes):
 * This is the superclass of {@link Player} and {@link NpcTrainer}. Everything that
 * is true of EVERY battler lives here: a name and a party of up to six Pokemon.
 * Everything that differs - money and a bag for the player, dialogue and an AI for
 * an NPC - lives in the subclass.
 *
 * <p>The class is {@code abstract}: "Trainer" is a category, not a thing you can
 * point at. {@code new Trainer("Ash")} is a compiler error, and that is the point.
 * The battle engine, meanwhile, only ever talks to {@code Trainer}, so it works
 * with both subclasses without a single {@code if}. That is polymorphism doing
 * real work rather than a textbook Shape/Circle/Square example.
 */
public abstract class Trainer {

    public static final int MAX_PARTY = 6;

    private String name;
    protected final List<Pokemon> party = new ArrayList<>();

    protected Trainer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pokemon> getParty() {
        return party;
    }

    /** @return false when the party is already full. */
    public boolean addToParty(Pokemon pokemon) {
        if (party.size() >= MAX_PARTY) {
            return false;
        }
        party.add(pokemon);
        return true;
    }

    /** The first Pokemon that is still standing, or null if the trainer has lost. */
    public Pokemon getActivePokemon() {
        for (Pokemon pokemon : party) {
            if (!pokemon.isFainted()) {
                return pokemon;
            }
        }
        return null;
    }

    public boolean hasUsablePokemon() {
        return getActivePokemon() != null;
    }

    /** Moves the chosen Pokemon to the front of the party - this is "switching". */
    public void sendOut(Pokemon pokemon) {
        if (party.remove(pokemon)) {
            party.add(0, pokemon);
        }
    }

    public void healParty() {
        for (Pokemon pokemon : party) {
            pokemon.fullyRestore();
        }
    }

    /**
     * How this trainer is announced in battle.
     *
     * <p>TEACHING NOTE (CS II - polymorphism): subclasses OVERRIDE this method to
     * change the wording. The battle engine calls {@code trainer.battleTitle()} and
     * Java picks the right version at run time.
     */
    public String battleTitle() {
        return name;
    }
}
