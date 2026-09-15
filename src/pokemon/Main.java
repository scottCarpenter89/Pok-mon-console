package pokemon;

import java.nio.file.Path;
import java.nio.file.Paths;
import pokemon.data.DataException;
import pokemon.data.DataLoader;
import pokemon.data.GameData;
import pokemon.ui.ConsoleUI;
import pokemon.ui.ExitGameException;
import pokemon.util.RandomSource;
import pokemon.util.SeededRandom;

/**
 * Where the program starts.
 *
 * <p>TEACHING NOTE (CS I - the main method; CS II - command-line arguments):
 * Every Java program starts at {@code main}. This one does four things and then
 * gets out of the way: read the arguments, load the data, build the game, run it.
 * A short {@code main} that delegates is a habit worth copying.
 *
 * <p>Command line options (great for classroom demos):
 * <pre>
 *   java -cp out pokemon.Main                 normal game
 *   java -cp out pokemon.Main --seed 12345    the same "random" adventure every time
 *   java -cp out pokemon.Main --fast          no dramatic pauses, for quick demos
 *   java -cp out pokemon.Main --data mydata   load a different folder of CSV files
 * </pre>
 */
public final class Main {

    public static void main(String[] args) {
        long seed = System.nanoTime();
        boolean fast = false;
        Path dataDirectory = null;

        // TEACHING NOTE (CS I - arrays and loops): args is just an array of Strings.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--seed":
                    if (i + 1 < args.length) {
                        try {
                            seed = Long.parseLong(args[++i]);
                        } catch (NumberFormatException e) {
                            System.out.println("Ignoring bad seed: " + args[i]);
                        }
                    }
                    break;
                case "--fast":
                    fast = true;
                    break;
                case "--data":
                    if (i + 1 < args.length) {
                        dataDirectory = Paths.get(args[++i]);
                    }
                    break;
                case "--help":
                    printUsage();
                    return;
                default:
                    System.out.println("Unknown option: " + args[i]);
                    printUsage();
                    return;
            }
        }

        ConsoleUI ui = new ConsoleUI();
        ui.setFastMode(fast);

        try {
            GameData data = dataDirectory == null
                    ? DataLoader.loadDefault()
                    : new DataLoader(dataDirectory).load();
            RandomSource rng = new SeededRandom(seed);
            new Game(data, ui, rng, seed).run();
        } catch (DataException e) {
            // A broken CSV file should produce a clear message, not a stack trace.
            System.out.println();
            System.out.println("The game data could not be loaded:");
            System.out.println("  " + e.getMessage());
            System.out.println("Fix the file above and run the game again.");
        } catch (ExitGameException e) {
            System.out.println();
            System.out.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -cp out pokemon.Main [--seed N] [--fast] [--data DIR]");
    }
}
