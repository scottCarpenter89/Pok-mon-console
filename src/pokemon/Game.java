package pokemon;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import pokemon.battle.Battle;
import pokemon.battle.BattleResult;
import pokemon.battle.RandomAI;
import pokemon.data.DataException;
import pokemon.data.GameData;
import pokemon.data.SaveManager;
import pokemon.model.Item;
import pokemon.model.MoveSlot;
import pokemon.model.NpcTrainer;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stat;
import pokemon.model.Status;
import pokemon.model.Trainer;
import pokemon.school.TrainerSchool;
import pokemon.ui.ConsoleUI;
import pokemon.world.Area;
import pokemon.world.Encounter;
import pokemon.world.TrainerTemplate;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/**
 * The overworld: menus, travel, shops, and everything that is not a battle.
 *
 * <p>TEACHING NOTE (CS I - the main loop; CS II - decomposition):
 * {@link #adventureLoop()} is a handful of lines because every menu option is its
 * own method. A student can read the loop and understand the WHOLE game in thirty
 * seconds, then dive into whichever method they care about. This is what "break the
 * problem into pieces" looks like once the program gets big.
 */
public class Game {

    private final GameData data;
    private final ConsoleUI ui;
    private final RandomSource rng;
    private final long seed;
    private final Path savePath = Paths.get(SaveManager.DEFAULT_FILE);

    private Player player;
    private boolean running = true;

    /** Chance of meeting a wild Pokemon when exploring tall grass. */
    private static final int ENCOUNTER_CHANCE_PERCENT = 80;

    public Game(GameData data, ConsoleUI ui, RandomSource rng, long seed) {
        this.data = data;
        this.ui = ui;
        this.rng = rng;
        this.seed = seed;
    }

    // ==================================================================
    // Start-up
    // ==================================================================

    public void run() {
        titleScreen();
        if (player == null) {
            return;     // The player quit from the title screen.
        }
        adventureLoop();
    }

    private void titleScreen() {
        ui.header("Pokemon Console RPG");
        ui.println("   A Java teaching project for Computer Science I, II and III");
        ui.println("   Loaded: " + data.summary());
        ui.println("   Random seed: " + seed + "   (run with --seed " + seed
                + " to replay this exact adventure)");
        ui.blank();

        while (player == null && running) {
            List<String> options = new ArrayList<>();
            options.add("New game");
            if (SaveManager.saveExists(savePath)) {
                options.add("Continue from " + savePath);
            }
            options.add("Trainer School (computer science lessons)");
            options.add("Quit");

            int choice = ui.chooseFromList("Main menu:", options, null);
            String picked = options.get(choice);

            if (picked.startsWith("New game")) {
                startNewGame();
            } else if (picked.startsWith("Continue")) {
                loadGame();
            } else if (picked.startsWith("Trainer School")) {
                new TrainerSchool(data, ui, rng).run();
            } else {
                running = false;
            }
        }
    }

    private void startNewGame() {
        ui.header("A new adventure");
        ui.message("PROF. OAK: Hello there! Welcome to the world of Pokemon!");
        String name = ui.prompt("First, what is your name?");
        if (name.isBlank()) {
            name = "Red";
        }
        player = new Player(name, "pallet_town");

        ui.message("PROF. OAK: " + name + "! Your very own Pokemon story is about to unfold.");
        chooseStarter();

        player.getBag().add(data.item("pokeball"), 5);
        player.getBag().add(data.item("potion"), 3);
        player.getBag().add(data.item("town_map"), 1);
        ui.message("You received 5 Poke Balls, 3 Potions and a Town Map!");
        ui.pause();
    }

    private void chooseStarter() {
        List<Species> starters = data.starters();
        List<String> labels = new ArrayList<>();
        for (Species species : starters) {
            labels.add(Text.pad(species.getName(), 12) + Text.pad(species.typeLine(), 16)
                    + species.getDexEntry());
        }
        int choice = ui.chooseFromList("Choose your first partner:", labels, null);
        Species chosen = starters.get(choice);

        Pokemon starter = Pokemon.create(chosen, 5, rng);
        if (ui.confirm("Would you like to give " + starter.getNickname() + " a nickname?")) {
            starter.setNickname(ui.prompt("Nickname:"));
        }
        player.addToParty(starter);
        player.recordCaught(chosen);
        ui.message("You received " + starter.getNickname() + "! Take good care of it.");
    }

    private void loadGame() {
        try {
            player = SaveManager.load(savePath, data);
            ui.message("Welcome back, " + player.getName() + "!");
            ui.pause();
        } catch (DataException e) {
            ui.println("  Could not load the save file: " + e.getMessage());
        }
    }

    // ==================================================================
    // The overworld
    // ==================================================================

    private void adventureLoop() {
        while (running) {
            Area area = currentArea();
            showAreaHeader(area);

            List<String> options = new ArrayList<>();
            List<Runnable> actions = new ArrayList<>();

            if (area.hasWildPokemon()) {
                options.add("Search the tall grass");
                actions.add(this::searchForWildPokemon);
            }
            List<TrainerTemplate> waiting = trainersWaitingIn(area);
            if (!waiting.isEmpty()) {
                options.add("Battle a trainer (" + waiting.size() + " waiting)");
                actions.add(() -> chooseTrainerBattle(waiting));
            }
            options.add("Travel");
            actions.add(this::travel);
            if (area.hasPokemonCenter()) {
                options.add("Pokemon Center (heal your team)");
                actions.add(this::visitCenter);
            }
            if (area.hasShop()) {
                options.add("Poke Mart (buy items)");
                actions.add(this::visitShop);
            }
            options.add("Party");
            actions.add(this::partyMenu);
            options.add("Bag");
            actions.add(this::showBag);
            options.add("Pokedex");
            actions.add(this::showPokedex);
            options.add("Trainer School (computer science lessons)");
            actions.add(() -> new TrainerSchool(data, ui, rng).run());
            options.add("Save game");
            actions.add(this::saveGame);
            options.add("Quit");
            actions.add(this::quit);

            int choice = ui.chooseFromList("What would you like to do?", options, null);
            actions.get(choice).run();
        }
    }

    private Area currentArea() {
        Area area = data.getWorldMap().get(player.getCurrentAreaId());
        if (area == null) {
            // Defensive programming: never let a bad save file strand the player.
            area = data.getWorldMap().all().get(0);
            player.setCurrentAreaId(area.getId());
        }
        return area;
    }

    private void showAreaHeader(Area area) {
        ui.header(area.getName());
        ui.println("  " + area.getDescription());
        Pokemon lead = player.getParty().get(0);
        ui.println("  " + Text.pad(player.getName(), 12) + "$" + player.getMoney()
                + "    Party lead: " + ui.pokemonLine(lead));
    }

    private List<TrainerTemplate> trainersWaitingIn(Area area) {
        List<TrainerTemplate> waiting = new ArrayList<>();
        for (String id : area.getTrainerIds()) {
            if (!player.hasDefeated(id)) {
                waiting.add(data.trainer(id));
            }
        }
        return waiting;
    }

    // ------------------------------------------------------------------
    // Wild battles
    // ------------------------------------------------------------------

    private void searchForWildPokemon() {
        Area area = currentArea();
        ui.message("You wade into the tall grass...");
        if (!rng.chance(ENCOUNTER_CHANCE_PERCENT)) {
            ui.message("Nothing stirred this time.");
            return;
        }
        Encounter encounter = area.rollEncounter(rng);
        int level = rng.between(encounter.minLevel(), encounter.maxLevel());
        Pokemon wild = Pokemon.create(encounter.species(), level, rng);
        NpcTrainer opponent = NpcTrainer.wild(wild, new RandomAI());

        BattleResult result = new Battle(player, opponent, ui, rng).start();
        afterBattle(result);
    }

    private void chooseTrainerBattle(List<TrainerTemplate> waiting) {
        List<String> labels = new ArrayList<>();
        for (TrainerTemplate template : waiting) {
            labels.add(Text.pad(template.title(), 24) + "team of " + template.getTeam().size()
                    + ", up to Lv." + template.topLevel());
        }
        int choice = ui.chooseFromList("Who will you challenge?", labels, "Walk away");
        if (choice < 0) {
            return;
        }
        NpcTrainer opponent = waiting.get(choice).build(rng);
        BattleResult result = new Battle(player, opponent, ui, rng).start();
        afterBattle(result);
    }

    /** Handles the consequences of a battle: fainting, blacking out, or a reward. */
    private void afterBattle(BattleResult result) {
        ui.pause();
        if (result == BattleResult.LOST) {
            blackOut();
        }
    }

    private void blackOut() {
        int lost = player.loseHalfMoney();
        ui.header("Blacked out");
        ui.message("You scrambled back to the nearest Pokemon Center...");
        if (lost > 0) {
            ui.message("You dropped $" + lost + " on the way.");
        }
        player.setCurrentAreaId(player.getLastCenterAreaId());
        player.healParty();
        ui.message("Your Pokemon were healed to full health.");
        ui.pause();
    }

    // ------------------------------------------------------------------
    // Towns
    // ------------------------------------------------------------------

    private void visitCenter() {
        player.healParty();
        player.setLastCenterAreaId(player.getCurrentAreaId());
        ui.message("NURSE JOY: We've restored your Pokemon to full health!");
        ui.message("NURSE JOY: We hope to see you again.");
        ui.pause();
    }

    private void visitShop() {
        Area area = currentArea();
        while (true) {
            List<String> labels = new ArrayList<>();
            List<Item> stock = new ArrayList<>();
            for (String itemId : area.getShopItemIds()) {
                Item item = data.item(itemId);
                stock.add(item);
                labels.add(Text.pad(item.getName(), 16) + Text.padLeft("$" + item.getPrice(), 6)
                        + "   " + item.getDescription());
            }
            ui.println("  Your money: $" + player.getMoney());
            int choice = ui.chooseFromList("CLERK: What can I get you?", labels, "Leave");
            if (choice < 0) {
                return;
            }
            Item item = stock.get(choice);
            int affordable = item.getPrice() == 0 ? 1 : player.getMoney() / item.getPrice();
            if (affordable == 0) {
                ui.message("CLERK: You don't have enough money for that.");
                continue;
            }
            int quantity = ui.promptInt("How many " + item.getName() + "s?", 0,
                    Math.min(affordable, 20));
            if (quantity == 0) {
                continue;
            }
            int cost = quantity * item.getPrice();
            if (player.spend(cost)) {
                player.getBag().add(item, quantity);
                ui.message("Bought " + quantity + " x " + item.getName() + " for $" + cost + ".");
            }
        }
    }

    // ------------------------------------------------------------------
    // Travel
    // ------------------------------------------------------------------

    private void travel() {
        Area area = currentArea();
        List<Area> neighbours = data.getWorldMap().neighboursOf(area);
        List<String> labels = new ArrayList<>();
        for (Area neighbour : neighbours) {
            String tags = (neighbour.hasPokemonCenter() ? " [Center]" : "")
                    + (neighbour.hasShop() ? " [Mart]" : "")
                    + (neighbour.hasWildPokemon() ? " [Wild Pokemon]" : "");
            labels.add(Text.pad(neighbour.getName(), 20) + tags);
        }
        int choice = ui.chooseFromList("Where to?", labels, "Stay here");
        if (choice < 0) {
            return;
        }
        Area destination = neighbours.get(choice);
        player.setCurrentAreaId(destination.getId());
        ui.message("You travel to " + destination.getName() + ".");
    }

    // ------------------------------------------------------------------
    // Party, bag, Pokedex
    // ------------------------------------------------------------------

    private void partyMenu() {
        while (true) {
            List<String> labels = new ArrayList<>();
            for (Pokemon pokemon : player.getParty()) {
                labels.add(ui.pokemonLine(pokemon) + "  " + pokemon.getSpecies().typeLine());
            }
            if (!player.getStorageBox().isEmpty()) {
                labels.add("-- Storage box (" + player.getStorageBox().size()
                        + " waiting) --");
            }
            int choice = ui.chooseFromList("Your party:", labels, "Back");
            if (choice < 0) {
                return;
            }
            if (choice >= player.getParty().size()) {
                storageBoxMenu();
            } else {
                pokemonDetail(player.getParty().get(choice), choice);
            }
        }
    }

    /**
     * Swaps a Pokemon in the storage box with one in the party.
     *
     * <p>TEACHING NOTE (CS II - two Lists in step): a swap needs a temporary variable,
     * exactly like swapping two numbers. Get the order wrong and one of the two is
     * overwritten before it is saved - the same bug students hit when they write their
     * first sort in Unit 14.
     */
    private void storageBoxMenu() {
        while (!player.getStorageBox().isEmpty()) {
            List<String> boxLabels = new ArrayList<>();
            for (Pokemon pokemon : player.getStorageBox()) {
                boxLabels.add(ui.pokemonLine(pokemon) + "  "
                        + pokemon.getSpecies().typeLine());
            }
            int boxChoice = ui.chooseFromList("Storage box:", boxLabels, "Back");
            if (boxChoice < 0) {
                return;
            }
            Pokemon fromBox = player.getStorageBox().get(boxChoice);

            if (player.getParty().size() < Trainer.MAX_PARTY) {
                player.getStorageBox().remove(boxChoice);
                player.addToParty(fromBox);
                ui.message(fromBox.getNickname() + " joined your party.");
                continue;
            }

            List<String> partyLabels = new ArrayList<>();
            for (Pokemon pokemon : player.getParty()) {
                partyLabels.add(ui.pokemonLine(pokemon));
            }
            int partyChoice = ui.chooseFromList(
                    "Your party is full. Send which Pokemon to the box?",
                    partyLabels, "Cancel");
            if (partyChoice < 0) {
                continue;
            }
            Pokemon fromParty = player.getParty().get(partyChoice);
            player.getParty().set(partyChoice, fromBox);
            player.getStorageBox().set(boxChoice, fromParty);
            ui.message(fromParty.getNickname() + " was sent to the box, and "
                    + fromBox.getNickname() + " joined your party.");
        }
        ui.message("The storage box is empty.");
    }

    private void pokemonDetail(Pokemon pokemon, int partyIndex) {
        List<String> lines = new ArrayList<>();
        Species species = pokemon.getSpecies();
        lines.add("No." + species.getDexNumber() + "  " + species.getName()
                + "   " + species.typeLine());
        lines.add("Level " + pokemon.getLevel() + "   HP "
                + ui.hpBar(pokemon, 16));
        lines.add("EXP to next level: " + pokemon.experienceToNextLevel());
        lines.add("");
        for (Stat stat : Stat.values()) {
            if (stat == Stat.HP) {
                continue;
            }
            lines.add(Text.pad(stat.displayName(), 12)
                    + Text.padLeft(String.valueOf(pokemon.getStat(stat)), 4)
                    + "   (base " + species.getBaseStats().get(stat)
                    + ", IV " + pokemon.getIndividualValues().get(stat) + ")");
        }
        lines.add("");
        lines.add("Moves:");
        for (MoveSlot slot : pokemon.getMoves()) {
            lines.add("  " + Text.pad(slot.getMove().getName(), 16)
                    + Text.pad(slot.getMove().getType().displayName(), 10)
                    + "PP " + slot.getCurrentPp() + "/" + slot.getMaxPp());
        }
        if (species.getEvolvesInto() != null) {
            lines.add("");
            lines.add("Evolves into " + species.getEvolvesInto().getName()
                    + " at level " + species.getEvolutionLevel() + ".");
        }
        ui.box(pokemon.getNickname(), lines);

        int choice = ui.menu("Options:", "Back", "Rename", "Move to the front of the party");
        if (choice == 1) {
            pokemon.setNickname(ui.prompt("New nickname:"));
            ui.message("Now called " + pokemon.getNickname() + ".");
        } else if (choice == 2 && partyIndex > 0) {
            player.sendOut(pokemon);
            ui.message(pokemon.getNickname() + " will lead the party.");
        }
    }

    private void showBag() {
        List<String> lines = new ArrayList<>();
        if (player.getBag().isEmpty()) {
            lines.add("(empty)");
        }
        for (Item item : player.getBag().items()) {
            lines.add(Text.pad(item.getName(), 16) + "x"
                    + Text.pad(String.valueOf(player.getBag().countOf(item)), 4)
                    + item.getDescription());
        }
        lines.add("");
        lines.add("Money: $" + player.getMoney());
        ui.box("BAG", lines);

        List<Item> usable = new ArrayList<>();
        for (Item item : player.getBag().items()) {
            if (item.usableOutsideBattle()) {
                usable.add(item);
            }
        }
        if (usable.isEmpty() || !ui.confirm("Use an item?")) {
            return;
        }
        List<String> labels = new ArrayList<>();
        for (Item item : usable) {
            labels.add(item.getName() + " x" + player.getBag().countOf(item));
        }
        int itemChoice = ui.chooseFromList("Use which item?", labels, "Back");
        if (itemChoice < 0) {
            return;
        }
        Item item = usable.get(itemChoice);

        List<String> memberLabels = new ArrayList<>();
        for (Pokemon pokemon : player.getParty()) {
            memberLabels.add(ui.pokemonLine(pokemon));
        }
        int target = ui.chooseFromList("On which Pokemon?", memberLabels, "Back");
        if (target < 0) {
            return;
        }
        applyItemOutsideBattle(item, player.getParty().get(target));
    }

    private void applyItemOutsideBattle(Item item, Pokemon target) {
        switch (item.getKind()) {
            case POTION: {
                if (target.isFainted()) {
                    ui.message("A Potion won't revive a fainted Pokemon.");
                    return;
                }
                int healed = target.heal(item.getPower());
                if (healed == 0) {
                    ui.message(target.getNickname() + " is already at full health.");
                    return;
                }
                player.getBag().useOne(item);
                ui.message(target.getNickname() + " recovered " + healed + " HP!");
                break;
            }
            case STATUS_HEAL: {
                if (target.getStatus() == Status.NONE) {
                    ui.message("There's nothing to cure.");
                    return;
                }
                player.getBag().useOne(item);
                target.clearStatus();
                ui.message(target.getNickname() + " was cured!");
                break;
            }
            case REVIVE: {
                if (!target.isFainted()) {
                    ui.message(target.getNickname() + " doesn't need reviving.");
                    return;
                }
                player.getBag().useOne(item);
                target.revive(0.5);
                ui.message(target.getNickname() + " was revived!");
                break;
            }
            default:
                ui.message("That can't be used here.");
        }
    }

    private void showPokedex() {
        List<String> lines = new ArrayList<>();
        for (Species species : data.allSpecies()) {
            boolean caught = player.hasCaught(species);
            boolean seen = player.hasSeen(species);
            if (!seen) {
                continue;
            }
            lines.add(Text.pad("No." + species.getDexNumber(), 7)
                    + Text.pad(species.getName(), 14)
                    + Text.pad(species.typeLine(), 16)
                    + (caught ? "OWN" : "seen"));
        }
        if (lines.isEmpty()) {
            lines.add("You haven't met any Pokemon yet. Go and explore!");
        }
        lines.add("");
        lines.add("Seen: " + player.getSeenSpecies().size()
                + "   Caught: " + player.getCaughtSpecies().size()
                + "   of " + data.allSpecies().size() + " species");
        ui.box("POKEDEX", lines);
        ui.pause();
    }

    // ------------------------------------------------------------------
    // Saving and quitting
    // ------------------------------------------------------------------

    private void saveGame() {
        try {
            SaveManager.save(player, savePath);
            ui.message("Game saved to " + savePath.toAbsolutePath());
        } catch (DataException e) {
            ui.println("  Could not save: " + e.getMessage());
        }
        ui.pause();
    }

    private void quit() {
        if (ui.confirm("Save before quitting?")) {
            saveGame();
        }
        ui.header("Thanks for playing!");
        ui.println("  Species seen: " + player.getSeenSpecies().size()
                + "   caught: " + player.getCaughtSpecies().size()
                + "   battles won: " + player.getBattlesWon());
        running = false;
    }
}
