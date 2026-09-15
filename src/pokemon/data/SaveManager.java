package pokemon.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pokemon.model.Item;
import pokemon.model.Move;
import pokemon.model.MoveSlot;
import pokemon.model.Player;
import pokemon.model.Pokemon;
import pokemon.model.Species;
import pokemon.model.Stats;
import pokemon.model.Status;

/**
 * Writes the player's adventure to a text file and reads it back.
 *
 * <p>TEACHING NOTE (CS II - file output and serialization):
 * The save file is deliberately plain text that students can open in Notepad:
 *
 * <pre>
 *   money=2400
 *   party=charmander|Blaze|14|2744|41|NONE|20,7,31,15,2,28|ember:20;scratch:34
 * </pre>
 *
 * Reading it teaches three separate ideas: serializing an object graph to text,
 * choosing a separator that cannot appear in the data, and version numbers for
 * forwards compatibility. It also makes cheating easy, which is a perfectly good
 * lesson about why real games do not trust the client.
 */
public final class SaveManager {

    public static final String DEFAULT_FILE = "savegame.txt";
    public static final int FORMAT_VERSION = 1;

    private SaveManager() {
    }

    // ==================================================================
    // Saving
    // ==================================================================

    public static void save(Player player, Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("# Pokemon Console RPG save file - safe to read and edit\n");
            writer.write("version=" + FORMAT_VERSION + "\n");
            writer.write("player=" + player.getName() + "\n");
            writer.write("money=" + player.getMoney() + "\n");
            writer.write("area=" + player.getCurrentAreaId() + "\n");
            writer.write("center=" + player.getLastCenterAreaId() + "\n");
            writer.write("wins=" + player.getBattlesWon() + "\n");
            writer.write("seen=" + String.join(",", player.getSeenSpecies()) + "\n");
            writer.write("caught=" + String.join(",", player.getCaughtSpecies()) + "\n");
            writer.write("defeated=" + String.join(",", player.getDefeatedTrainers()) + "\n");

            for (Map.Entry<Item, Integer> entry : player.getBag().asMap().entrySet()) {
                writer.write("item=" + entry.getKey().getId() + ":" + entry.getValue() + "\n");
            }
            for (Pokemon pokemon : player.getParty()) {
                writer.write("party=" + encode(pokemon) + "\n");
            }
            for (Pokemon pokemon : player.getStorageBox()) {
                writer.write("box=" + encode(pokemon) + "\n");
            }
        } catch (IOException e) {
            throw new DataException("Could not save to " + file + ": " + e.getMessage(), e);
        }
    }

    /** One Pokemon as one line of text. */
    private static String encode(Pokemon pokemon) {
        StringBuilder moves = new StringBuilder();
        for (MoveSlot slot : pokemon.getMoves()) {
            if (moves.length() > 0) {
                moves.append(';');
            }
            moves.append(slot.getMove().getId()).append(':').append(slot.getCurrentPp());
        }
        Stats ivs = pokemon.getIndividualValues();
        String ivText = ivs.hp() + "," + ivs.attack() + "," + ivs.defense() + ","
                + ivs.spAttack() + "," + ivs.spDefense() + "," + ivs.speed();

        return String.join("|",
                pokemon.getSpecies().getId(),
                pokemon.getNickname(),
                String.valueOf(pokemon.getLevel()),
                String.valueOf(pokemon.getExperience()),
                String.valueOf(pokemon.getCurrentHp()),
                pokemon.getStatus().name(),
                ivText,
                moves.toString());
    }

    // ==================================================================
    // Loading
    // ==================================================================

    public static boolean saveExists(Path file) {
        return Files.exists(file);
    }

    public static Player load(Path file, GameData data) {
        if (!Files.exists(file)) {
            throw new DataException("There is no save file at " + file.toAbsolutePath());
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataException("Could not read " + file + ": " + e.getMessage(), e);
        }

        String name = "Trainer";
        String area = "pallet_town";
        String center = "pallet_town";
        int money = 0;
        int wins = 0;
        List<String> partyLines = new ArrayList<>();
        List<String> boxLines = new ArrayList<>();
        List<String> itemLines = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        List<String> caught = new ArrayList<>();
        List<String> defeated = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int equals = line.indexOf('=');
            if (equals < 0) {
                continue;
            }
            String key = line.substring(0, equals);
            String value = line.substring(equals + 1);
            switch (key) {
                case "player": name = value; break;
                case "money": money = parseIntOr(value, 0); break;
                case "area": area = value; break;
                case "center": center = value; break;
                case "wins": wins = parseIntOr(value, 0); break;
                case "seen": addAll(seen, value); break;
                case "caught": addAll(caught, value); break;
                case "defeated": addAll(defeated, value); break;
                case "item": itemLines.add(value); break;
                case "party": partyLines.add(value); break;
                case "box": boxLines.add(value); break;
                default: break;   // Unknown keys are ignored, so old saves still load.
            }
        }

        Player player = new Player(name, area);
        player.setMoney(money);
        player.setLastCenterAreaId(center);
        player.setBattlesWon(wins);
        for (String id : seen) {
            if (data.hasSpecies(id)) {
                player.recordSeen(data.species(id));
            }
        }
        for (String id : caught) {
            if (data.hasSpecies(id)) {
                player.recordCaught(data.species(id));
            }
        }
        for (String id : defeated) {
            player.markDefeated(id);
        }
        for (String itemLine : itemLines) {
            String[] parts = itemLine.split(":");
            if (parts.length == 2) {
                player.getBag().add(data.item(parts[0]), parseIntOr(parts[1], 1));
            }
        }
        for (String partyLine : partyLines) {
            player.addToParty(decode(partyLine, data));
        }
        for (String boxLine : boxLines) {
            player.getStorageBox().add(decode(boxLine, data));
        }
        if (player.getParty().isEmpty()) {
            throw new DataException("Save file has no Pokemon in the party - it may be corrupt.");
        }
        return player;
    }

    private static void addAll(List<String> target, String commaSeparated) {
        for (String part : commaSeparated.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }

    private static int parseIntOr(String raw, int fallback) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** Turns one saved line back into a Pokemon object. */
    private static Pokemon decode(String line, GameData data) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            throw new DataException("Corrupt Pokemon entry in save file: " + line);
        }
        Species species = data.species(parts[0]);
        int level = parseIntOr(parts[2], 5);
        String[] ivParts = parts[6].split(",");
        Stats ivs = ivParts.length == 6
                ? new Stats(parseIntOr(ivParts[0], 0), parseIntOr(ivParts[1], 0),
                            parseIntOr(ivParts[2], 0), parseIntOr(ivParts[3], 0),
                            parseIntOr(ivParts[4], 0), parseIntOr(ivParts[5], 0))
                : new Stats(0, 0, 0, 0, 0, 0);

        Pokemon pokemon = new Pokemon(species, level, ivs);
        pokemon.setNickname(parts[1]);

        Status status;
        try {
            status = Status.valueOf(parts[5]);
        } catch (IllegalArgumentException e) {
            status = Status.NONE;
        }
        pokemon.restoreState(level, parseIntOr(parts[3], 0), parseIntOr(parts[4], 1), status);

        pokemon.getMoves().clear();
        for (String moveText : parts[7].split(";")) {
            if (moveText.isBlank()) {
                continue;
            }
            String[] moveParts = moveText.split(":");
            Move move = data.move(moveParts[0]);
            int pp = moveParts.length > 1 ? parseIntOr(moveParts[1], move.getMaxPp()) : move.getMaxPp();
            pokemon.getMoves().add(new MoveSlot(move, pp));
        }
        if (pokemon.getMoves().isEmpty()) {
            for (Move move : species.movesKnownAt(level)) {
                pokemon.learnMove(move);
            }
        }
        return pokemon;
    }
}
