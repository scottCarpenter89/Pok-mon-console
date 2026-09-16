package pokemon.school;

import java.util.ArrayList;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Species;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;

/** CS II - recursion: a method that calls itself. */
public class RecursionLesson implements Lesson {

    @Override
    public String title() {
        return "Recursion";
    }

    @Override
    public String courseLevel() {
        return "CS II";
    }

    @Override
    public String concept() {
        return "base case + smaller problem";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        ui.println("  Every recursive method needs exactly two things:");
        ui.println("    1. a BASE CASE that stops the recursion");
        ui.println("    2. a call to itself on a SMALLER problem");
        ui.blank();
        ui.println("  Evolution chains are naturally recursive - 'the chain for Charmander");
        ui.println("  is Charmander, followed by the chain for Charmeleon':");
        ui.blank();

        for (String id : List.of("charmander", "caterpie", "magikarp", "onix")) {
            Species species = data.species(id);
            List<String> chain = new ArrayList<>();
            buildChain(species, chain);
            ui.println("    " + String.join("  ->  ", chain));
        }
        ui.blank();
        ui.println("    private void buildChain(Species species, List<String> chain) {");
        ui.println("        if (species == null) return;            // BASE CASE");
        ui.println("        chain.add(species.getName());");
        ui.println("        buildChain(species.getEvolvesInto(), chain);   // SMALLER PROBLEM");
        ui.println("    }");
        ui.blank();

        ui.println("  Counting how deep a chain goes, recursively:");
        for (String id : List.of("bulbasaur", "ivysaur", "venusaur")) {
            Species species = data.species(id);
            ui.println("    " + species.getName() + " has "
                    + stagesLeft(species) + " stage(s) left to evolve through.");
        }
        ui.blank();

        int n = ui.promptInt("Pick a number and watch a recursive countdown", 1, 10);
        ui.println("    " + countdown(n));
        ui.blank();
        ui.println("  WARNING WORTH SHOWING IN CLASS: delete the base case and you get");
        ui.println("  StackOverflowError - the computer runs out of room to remember all");
        ui.println("  those unfinished calls. Every recursive call is a promise to come back.");
    }

    /** Walks an evolution line by calling itself. */
    private void buildChain(Species species, List<String> chain) {
        if (species == null) {
            return;                                   // BASE CASE
        }
        chain.add(species.getName());
        buildChain(species.getEvolvesInto(), chain);  // SMALLER PROBLEM
    }

    /** Classic recursive counting. */
    private int stagesLeft(Species species) {
        if (species.getEvolvesInto() == null) {
            return 0;
        }
        return 1 + stagesLeft(species.getEvolvesInto());
    }

    private String countdown(int n) {
        if (n <= 0) {
            return "liftoff!";
        }
        return n + " ... " + countdown(n - 1);
    }
}
