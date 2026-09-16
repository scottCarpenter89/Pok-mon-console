package pokemon.world;

import java.util.ArrayList;
import java.util.List;
import pokemon.battle.BattleAI;
import pokemon.model.NpcTrainer;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.util.RandomSource;

/**
 * The recipe for an NPC trainer, loaded once from {@code data/trainers.csv}.
 *
 * <p>TEACHING NOTE (CS III - the Factory pattern):
 * A template is not a trainer; it is instructions for BUILDING a trainer. Every
 * time you battle Bug Catcher Rick his Pokemon are freshly created at full health
 * from this recipe. Without the template you would have to remember to heal the
 * stored trainer after every battle - a bug that is easy to write and hard to find.
 */
public class TrainerTemplate {

    /** One line of the team spec: a species and the level to build it at. */
    public record TeamEntry(Species species, int level) {
    }

    private final String id;
    private final String areaId;
    private final String trainerClass;
    private final String name;
    private final String aiName;
    private final int rewardMoney;
    private final String dialogueBefore;
    private final String dialogueAfter;
    private final List<TeamEntry> team = new ArrayList<>();

    public TrainerTemplate(String id, String areaId, String trainerClass, String name,
                           String aiName, int rewardMoney,
                           String dialogueBefore, String dialogueAfter) {
        this.id = id;
        this.areaId = areaId;
        this.trainerClass = trainerClass;
        this.name = name;
        this.aiName = aiName;
        this.rewardMoney = rewardMoney;
        this.dialogueBefore = dialogueBefore;
        this.dialogueAfter = dialogueAfter;
    }

    public void addTeamEntry(Species species, int level) {
        team.add(new TeamEntry(species, level));
    }

    /** Builds a brand-new, fully healed trainer ready to battle. */
    public NpcTrainer build(RandomSource rng) {
        BattleAI ai = BattleAI.byName(aiName);
        NpcTrainer trainer = new NpcTrainer(id, trainerClass, name, rewardMoney,
                dialogueBefore, dialogueAfter, ai);
        for (TeamEntry entry : team) {
            trainer.addToParty(Pokemon.create(entry.species(), entry.level(), rng));
        }
        return trainer;
    }

    public String getId() {
        return id;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getTrainerClass() {
        return trainerClass;
    }

    public String getName() {
        return name;
    }

    public String getAiName() {
        return aiName;
    }

    public List<TeamEntry> getTeam() {
        return team;
    }

    public String title() {
        return trainerClass + " " + name;
    }

    /** The highest level on the team - used to warn the player before a fight. */
    public int topLevel() {
        int top = 0;
        for (TeamEntry entry : team) {
            top = Math.max(top, entry.level());
        }
        return top;
    }
}
