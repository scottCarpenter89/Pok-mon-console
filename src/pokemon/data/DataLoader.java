package pokemon.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import pokemon.model.Category;
import pokemon.model.Item;
import pokemon.model.ItemKind;
import pokemon.model.Move;
import pokemon.model.MoveEffect;
import pokemon.model.Species;
import pokemon.model.Stats;
import pokemon.model.Type;
import pokemon.world.Area;
import pokemon.world.Encounter;
import pokemon.world.TrainerTemplate;

/**
 * Reads every {@code .csv} in the data folder and builds a {@link GameData}.
 *
 * <p>TEACHING NOTES:
 * <ul>
 *   <li>CS II - file input: {@link #readRows} shows try-with-resources, which closes
 *       the file even if an exception is thrown halfway through.</li>
 *   <li>CS II - exception handling: low-level {@link IOException}s become
 *       {@link DataException}s that name the file and line.</li>
 *   <li>CS III - data-driven design: not one species, move or route is hard-coded in
 *       Java. A teacher can add a new Pokemon by editing a spreadsheet, and students
 *       can extend the game on day one without understanding the engine.</li>
 * </ul>
 *
 * <p>NOTE ON LOAD ORDER: moves must exist before learnsets can point at them, and
 * every species must exist before evolutions can be linked. Getting this order
 * wrong is a great live-debugging demonstration.
 */
public final class DataLoader {

    private final Path directory;

    public DataLoader(Path directory) {
        this.directory = directory;
    }

    /** Looks for the data folder in the usual places so the game runs from anywhere. */
    public static Path findDataDirectory() {
        String[] candidates = {"data", "../data", "../../data", "src/data"};
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.isDirectory(path) && Files.exists(path.resolve("species.csv"))) {
                return path;
            }
        }
        throw new DataException("Could not find the data folder. Run the game from the "
                + "project root, or pass the folder as the first argument.");
    }

    public static GameData loadDefault() {
        return new DataLoader(findDataDirectory()).load();
    }

    public GameData load() {
        GameData data = new GameData();
        loadMoves(data);
        loadSpecies(data);
        loadLearnsets(data);
        linkEvolutions(data);
        loadItems(data);
        loadAreas(data);
        loadEncounters(data);
        loadShops(data);
        loadTrainers(data);
        return data;
    }

    // ------------------------------------------------------------------
    // Reading a file into rows
    // ------------------------------------------------------------------

    /**
     * Reads one CSV file. Blank lines and lines starting with '#' are ignored, so
     * data files can carry comments for students.
     */
    private List<CsvRow> readRows(String fileName) {
        Path file = directory.resolve(fileName);
        if (!Files.exists(file)) {
            throw new DataException("Missing data file: " + file.toAbsolutePath());
        }
        List<CsvRow> rows = new ArrayList<>();
        // try-with-resources: the reader is closed automatically, even on an error.
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            List<String> headers = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                if (headers == null) {
                    headers = CsvRow.split(line);
                    continue;
                }
                rows.add(new CsvRow(fileName, lineNumber, headers, CsvRow.split(line)));
            }
        } catch (IOException e) {
            throw new DataException("Could not read " + file + ": " + e.getMessage(), e);
        }
        if (rows.isEmpty()) {
            throw new DataException(fileName + " contains no data rows.");
        }
        return rows;
    }

    // ------------------------------------------------------------------
    // One method per file - each is short enough to read in one sitting
    // ------------------------------------------------------------------

    private void loadMoves(GameData data) {
        for (CsvRow row : readRows("moves.csv")) {
            try {
                data.addMove(new Move(
                        row.getString("id"),
                        row.getString("name"),
                        Type.parse(row.getString("type")),
                        Category.parse(row.getString("category")),
                        row.getInt("power", 0),
                        row.getInt("accuracy", 100),
                        row.getInt("pp", 10),
                        row.getInt("priority", 0),
                        MoveEffect.parse(row.getString("effect", "NONE")),
                        row.getInt("effectChance", 0),
                        row.getString("description")));
            } catch (IllegalArgumentException e) {
                throw new DataException(row.where() + ": " + e.getMessage(), e);
            }
        }
    }

    private void loadSpecies(GameData data) {
        for (CsvRow row : readRows("species.csv")) {
            try {
                String secondaryRaw = row.getString("type2");
                Type secondary = secondaryRaw.isEmpty() || secondaryRaw.equalsIgnoreCase("none")
                        ? null : Type.parse(secondaryRaw);
                Stats baseStats = new Stats(
                        row.getInt("hp"), row.getInt("attack"), row.getInt("defense"),
                        row.getInt("spAttack"), row.getInt("spDefense"), row.getInt("speed"));
                Species species = new Species(
                        row.getInt("dex"),
                        row.getString("id"),
                        row.getString("name"),
                        Type.parse(row.getString("type1")),
                        secondary,
                        baseStats,
                        row.getInt("catchRate", 100),
                        row.getInt("baseExp", 60),
                        row.getString("dexEntry"));
                data.addSpecies(species);
                if (row.getBoolean("starter")) {
                    data.addStarter(species.getId());
                }
            } catch (IllegalArgumentException e) {
                throw new DataException(row.where() + ": " + e.getMessage(), e);
            }
        }
    }

    private void loadLearnsets(GameData data) {
        for (CsvRow row : readRows("learnsets.csv")) {
            Species species = data.species(row.getString("speciesId"));
            Move move = data.move(row.getString("moveId"));
            species.addLearnsetEntry(row.getInt("level"), move);
        }
        // A species with no moves at all would soft-lock a battle: check now, loudly.
        for (Species species : data.allSpecies()) {
            if (species.movesKnownAt(1).isEmpty() && species.getLearnset().isEmpty()) {
                throw new DataException("Species '" + species.getId()
                        + "' has no moves in learnsets.csv");
            }
        }
    }

    private void linkEvolutions(GameData data) {
        for (CsvRow row : readRows("species.csv")) {
            String evolvesInto = row.getString("evolvesInto");
            if (evolvesInto.isEmpty() || evolvesInto.equalsIgnoreCase("none")) {
                continue;
            }
            Species from = data.species(row.getString("id"));
            Species to = data.species(evolvesInto);
            from.setEvolution(to, row.getInt("evolveLevel", 100));
        }
    }

    private void loadItems(GameData data) {
        for (CsvRow row : readRows("items.csv")) {
            try {
                data.addItem(new Item(
                        row.getString("id"),
                        row.getString("name"),
                        ItemKind.parse(row.getString("kind")),
                        row.getInt("power", 0),
                        row.getInt("price", 0),
                        row.getString("description")));
            } catch (IllegalArgumentException e) {
                throw new DataException(row.where() + ": " + e.getMessage(), e);
            }
        }
    }

    private void loadAreas(GameData data) {
        for (CsvRow row : readRows("areas.csv")) {
            Area area = new Area(
                    row.getString("id"),
                    row.getString("name"),
                    row.getString("description"),
                    row.getBoolean("pokemonCenter"),
                    row.getBoolean("shop"));
            area.getConnections().addAll(row.getList("connections", "|"));
            data.getWorldMap().add(area);
        }
        // Every connection must point at a real area, and roads run both ways.
        for (Area area : data.getWorldMap().all()) {
            for (String id : area.getConnections()) {
                if (!data.getWorldMap().contains(id)) {
                    throw new DataException("Area '" + area.getId()
                            + "' connects to unknown area '" + id + "'");
                }
            }
        }
    }

    private void loadEncounters(GameData data) {
        for (CsvRow row : readRows("encounters.csv")) {
            Area area = data.getWorldMap().get(row.getString("areaId"));
            if (area == null) {
                throw new DataException(row.where() + ": unknown area '"
                        + row.getString("areaId") + "'");
            }
            area.getEncounters().add(new Encounter(
                    data.species(row.getString("speciesId")),
                    row.getInt("weight", 10),
                    row.getInt("minLevel"),
                    row.getInt("maxLevel")));
        }
    }

    private void loadShops(GameData data) {
        for (CsvRow row : readRows("shops.csv")) {
            Area area = data.getWorldMap().get(row.getString("areaId"));
            if (area == null) {
                throw new DataException(row.where() + ": unknown area '"
                        + row.getString("areaId") + "'");
            }
            String itemId = row.getString("itemId");
            data.item(itemId);   // Fails now rather than at the counter.
            area.getShopItemIds().add(itemId);
        }
    }

    private void loadTrainers(GameData data) {
        for (CsvRow row : readRows("trainers.csv")) {
            TrainerTemplate template = new TrainerTemplate(
                    row.getString("id"),
                    row.getString("areaId"),
                    row.getString("class"),
                    row.getString("name"),
                    row.getString("ai", "RANDOM"),
                    row.getInt("reward", 100),
                    row.getString("before"),
                    row.getString("after"));

            // The team column looks like "pidgey:5|rattata:6"
            for (String entry : row.getList("team", "|")) {
                String[] parts = entry.split(":");
                if (parts.length != 2) {
                    throw new DataException(row.where() + ": team entry '" + entry
                            + "' should look like speciesId:level");
                }
                try {
                    template.addTeamEntry(data.species(parts[0].trim()),
                            Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException e) {
                    throw new DataException(row.where() + ": '" + parts[1]
                            + "' is not a level number", e);
                }
            }
            if (template.getTeam().isEmpty()) {
                throw new DataException(row.where() + ": trainer '" + template.getId()
                        + "' has an empty team");
            }
            Area area = data.getWorldMap().get(template.getAreaId());
            if (area == null) {
                throw new DataException(row.where() + ": unknown area '"
                        + template.getAreaId() + "'");
            }
            area.getTrainerIds().add(template.getId());
            data.addTrainer(template);
        }
    }
}
