package pokemon.model;

import pokemon.util.Text;

/** The six statistics every Pokemon has. */
public enum Stat {
    HP, ATTACK, DEFENSE, SP_ATTACK, SP_DEFENSE, SPEED;

    public String displayName() {
        return Text.humanize(name()).replace("Sp ", "Sp. ");
    }
}
