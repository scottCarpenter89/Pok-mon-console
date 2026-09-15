package pokemon.model;

import pokemon.battle.BattleAI;

/**
 * A trainer the computer controls.
 *
 * <p>TEACHING NOTE (CS III - the Strategy pattern):
 * An NPC does not contain any "how do I choose a move" code. Instead it HAS-A
 * {@link BattleAI}, and the AI object decides. Swapping a route trainer's
 * {@code RandomAI} for a gym leader's {@code TypeSmartAI} changes the difficulty
 * without editing a single line of the battle engine. Composition ("has-a") is
 * often a better tool than inheritance ("is-a"), and this class is the example to
 * use when you teach that.
 */
public class NpcTrainer extends Trainer {

    private final String id;
    private final String trainerClass;   // "Bug Catcher", "Gym Leader", ...
    private final int rewardMoney;
    private final String dialogueBefore;
    private final String dialogueAfter;
    private final BattleAI ai;
    private final boolean wild;

    public NpcTrainer(String id, String trainerClass, String name, int rewardMoney,
                      String dialogueBefore, String dialogueAfter, BattleAI ai) {
        super(name);
        this.id = id;
        this.trainerClass = trainerClass;
        this.rewardMoney = rewardMoney;
        this.dialogueBefore = dialogueBefore;
        this.dialogueAfter = dialogueAfter;
        this.ai = ai;
        this.wild = false;
    }

    /** A stand-in "trainer" that owns a single wild Pokemon. */
    public static NpcTrainer wild(Pokemon pokemon, BattleAI ai) {
        NpcTrainer trainer = new NpcTrainer("wild", "Wild", "Wild " + pokemon.getNickname(),
                0, "", "", ai, true);
        trainer.addToParty(pokemon);
        return trainer;
    }

    private NpcTrainer(String id, String trainerClass, String name, int rewardMoney,
                       String dialogueBefore, String dialogueAfter, BattleAI ai, boolean wild) {
        super(name);
        this.id = id;
        this.trainerClass = trainerClass;
        this.rewardMoney = rewardMoney;
        this.dialogueBefore = dialogueBefore;
        this.dialogueAfter = dialogueAfter;
        this.ai = ai;
        this.wild = wild;
    }

    public String getId() {
        return id;
    }

    public String getTrainerClass() {
        return trainerClass;
    }

    public int getRewardMoney() {
        return rewardMoney;
    }

    public String getDialogueBefore() {
        return dialogueBefore;
    }

    public String getDialogueAfter() {
        return dialogueAfter;
    }

    public BattleAI getAi() {
        return ai;
    }

    public boolean isWild() {
        return wild;
    }

    /** Overrides the version in {@link Trainer} to add the trainer class. */
    @Override
    public String battleTitle() {
        if (wild) {
            return getName();
        }
        return trainerClass + " " + getName();
    }
}
