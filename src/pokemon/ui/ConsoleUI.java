package pokemon.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import pokemon.model.Pokemon;
import pokemon.util.Text;

/**
 * Everything the game prints and everything it reads. No game rules live here.
 *
 * <p>TEACHING NOTE (CS II/III - separation of concerns):
 * The rules of the game ({@code pokemon.battle}, {@code pokemon.model}) never call
 * {@code System.out.println} and never read the keyboard. All of that is funnelled
 * through this one class. Two payoffs worth naming out loud in class:
 * <ol>
 *   <li>You can test the rules without a human typing answers.</li>
 *   <li>If someone later wants a graphical version, they replace this class and
 *       nothing else.</li>
 * </ol>
 */
public class ConsoleUI {

    public static final int WIDTH = 64;

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    /** When true, "press Enter to continue" prompts are skipped (used by demos and tests). */
    private boolean fastMode = false;

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    // ------------------------------------------------------------------
    // Output
    // ------------------------------------------------------------------

    public void print(String text) {
        System.out.print(text);
    }

    public void println(String text) {
        System.out.println(text);
    }

    public void blank() {
        System.out.println();
    }

    public void rule() {
        System.out.println(Text.repeat('-', WIDTH));
    }

    /** A banner such as ===== PALLET TOWN ===== */
    public void header(String title) {
        blank();
        System.out.println(Text.repeat('=', WIDTH));
        int padding = Math.max(0, (WIDTH - title.length()) / 2);
        System.out.println(Text.repeat(' ', padding) + title.toUpperCase());
        System.out.println(Text.repeat('=', WIDTH));
    }

    /** A framed box of lines - used for stat screens and Pokedex entries. */
    public void box(String title, List<String> lines) {
        System.out.println("+" + Text.repeat('-', WIDTH - 2) + "+");
        if (title != null && !title.isEmpty()) {
            System.out.println("| " + Text.pad(title, WIDTH - 4) + " |");
            System.out.println("|" + Text.repeat('-', WIDTH - 2) + "|");
        }
        for (String line : lines) {
            System.out.println("| " + Text.pad(line, WIDTH - 4) + " |");
        }
        System.out.println("+" + Text.repeat('-', WIDTH - 2) + "+");
    }

    /** One line of battle narration, with a short pause so text is readable. */
    public void message(String text) {
        System.out.println("  " + text);
        pauseBriefly();
    }

    private void pauseBriefly() {
        if (fastMode) {
            return;
        }
        try {
            Thread.sleep(220);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** A drawn HP bar: [##########----------] 52/104 */
    public String hpBar(Pokemon pokemon, int barWidth) {
        int max = pokemon.getMaxHp();
        int current = pokemon.getCurrentHp();
        int filled = max == 0 ? 0 : (int) Math.round((double) current / max * barWidth);
        filled = Math.max(current > 0 ? 1 : 0, Math.min(barWidth, filled));
        return "[" + Text.repeat('#', filled) + Text.repeat('.', barWidth - filled) + "] "
                + current + "/" + max;
    }

    /** "Pikachu   Lv.12  [########..] 31/40  PAR" */
    public String pokemonLine(Pokemon pokemon) {
        String status = pokemon.getStatus().tag().trim();
        return Text.pad(pokemon.getNickname(), 12)
                + " Lv." + Text.pad(String.valueOf(pokemon.getLevel()), 4)
                + hpBar(pokemon, 10)
                + (status.isEmpty() ? "" : "  " + status);
    }

    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------

    /** @throws ExitGameException when standard input has run out. */
    public String readLine() {
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new ExitGameException("Input ended - closing the game.");
            }
            return line.trim();
        } catch (IOException e) {
            throw new ExitGameException("Could not read input: " + e.getMessage());
        }
    }

    public String prompt(String question) {
        System.out.print(question + " ");
        return readLine();
    }

    /** Keeps asking until the answer is a number in range. */
    public int promptInt(String question, int min, int max) {
        while (true) {
            String answer = prompt(question + " (" + min + "-" + max + "):");
            try {
                int value = Integer.parseInt(answer);
                if (value >= min && value <= max) {
                    return value;
                }
                println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                // TEACHING NOTE (CS II - exception handling): this catch block is
                // why the game never crashes when a student types "banana" at a
                // menu. Input validation is the #1 real-world use of try/catch.
                println("  '" + answer + "' is not a number. Try again.");
            }
        }
    }

    public boolean confirm(String question) {
        while (true) {
            String answer = prompt(question + " (y/n)").toLowerCase();
            if (answer.startsWith("y")) {
                return true;
            }
            if (answer.startsWith("n")) {
                return false;
            }
            println("  Please answer y or n.");
        }
    }

    /**
     * Shows a numbered menu and returns the chosen index.
     *
     * @return the index into {@code options}, or -1 when the player cancels
     */
    public int chooseFromList(String title, List<String> options, String cancelLabel) {
        if (options.isEmpty()) {
            println("  (nothing to choose)");
            return -1;
        }
        blank();
        if (title != null && !title.isEmpty()) {
            println(title);
        }
        for (int i = 0; i < options.size(); i++) {
            println("  " + (i + 1) + ") " + options.get(i));
        }
        boolean cancellable = cancelLabel != null;
        if (cancellable) {
            println("  0) " + cancelLabel);
        }
        int choice = promptInt(">", cancellable ? 0 : 1, options.size());
        return choice == 0 ? -1 : choice - 1;
    }

    public void pause() {
        if (fastMode) {
            return;
        }
        System.out.print("  -- press Enter to continue --");
        readLine();
    }

    /** Convenience for menus built from an array of labels. */
    public int menu(String title, String... options) {
        List<String> list = new ArrayList<>();
        for (String option : options) {
            list.add(option);
        }
        return chooseFromList(title, list, null);
    }
}
