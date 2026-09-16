package pokemon.school;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pokemon.data.GameData;
import pokemon.model.Species;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;

/** CS II to CS III - searching, sorting, and the beginning of Big-O. */
public class SearchSortLesson implements Lesson {

    @Override
    public String title() {
        return "Searching and sorting";
    }

    @Override
    public String courseLevel() {
        return "CS II/III";
    }

    @Override
    public String concept() {
        return "linear vs binary, Big-O";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        List<String> names = new ArrayList<>();
        for (Species species : data.allSpecies()) {
            names.add(species.getName());
        }
        Collections.sort(names);

        ui.println("  The Pokedex holds " + names.size() + " species, sorted alphabetically.");
        ui.println("  Two ways to find one, counting how many names each method looks at:");
        ui.blank();

        int index = rng.nextInt(names.size());
        String target = names.get(index);

        int linearSteps = linearSearch(names, target);
        int binarySteps = binarySearch(names, target);

        ui.box("Searching for \"" + target + "\"", List.of(
                Text.pad("Linear search", 18) + linearSteps + " comparisons",
                Text.pad("Binary search", 18) + binarySteps + " comparisons"));
        ui.blank();
        ui.println("  Binary search throws away HALF the list with every comparison.");
        ui.println("  It only works because the list is sorted - that is the trade:");
        ui.println("  sort once (slow), then search many times (fast).");
        ui.blank();

        ui.box("How the work grows as n grows", List.of(
                Text.pad("n", 16) + Text.pad("linear O(n)", 18) + "binary O(log n)",
                Text.pad("50", 16) + Text.pad("50", 18) + "6",
                Text.pad("1,000", 16) + Text.pad("1,000", 18) + "10",
                Text.pad("1,000,000", 16) + Text.pad("1,000,000", 18) + "20",
                Text.pad("1,000,000,000", 16) + Text.pad("1,000,000,000", 18) + "30"));
        ui.blank();
        ui.println("  A million-name list: 1,000,000 steps versus 20. Same computer.");
        ui.println("  Better algorithm. This is the single biggest idea in CS III.");
        ui.blank();

        ui.println("  SORTING: here is selection sort putting six species in order of");
        ui.println("  base-stat total, printing the list after every pass.");
        ui.blank();

        List<Species> sample = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            sample.add(data.allSpecies().get(rng.nextInt(data.allSpecies().size())));
        }
        selectionSortWithLog(sample, ui);
        ui.blank();
        ui.println("  Selection sort makes n*(n-1)/2 comparisons - O(n^2). For "
                + sample.size() + " items that is only "
                + (sample.size() * (sample.size() - 1) / 2) + ",");
        ui.println("  so at this size nobody cares. Scale it up and they do:");
        ui.blank();
        ui.box("Comparisons to sort n items", List.of(
                Text.pad("n", 16) + Text.pad("selection O(n^2)", 20) + "merge O(n log n)",
                Text.pad("10", 16) + Text.pad("45", 20) + "33",
                Text.pad("1,000", 16) + Text.pad("499,500", 20) + "9,966",
                Text.pad("100,000", 16) + Text.pad("~5,000,000,000", 20) + "~1,700,000"));
        ui.blank();
        ui.println("  Same answer, same computer, 3,000 times less work. Java's built-in");
        ui.println("  Collections.sort() is the O(n log n) one - use it unless you are");
        ui.println("  learning how sorting works, which is exactly what you are doing now.");
    }

    /** Checks every item from the start. Returns how many it checked. */
    private int linearSearch(List<String> names, String target) {
        int comparisons = 0;
        for (String name : names) {
            comparisons++;
            if (name.equals(target)) {
                return comparisons;
            }
        }
        return comparisons;
    }

    /** Halves the search range each time. Returns how many it checked. */
    private int binarySearch(List<String> names, String target) {
        int low = 0;
        int high = names.size() - 1;
        int comparisons = 0;
        while (low <= high) {
            comparisons++;
            int middle = (low + high) / 2;
            int direction = names.get(middle).compareTo(target);
            if (direction == 0) {
                return comparisons;
            }
            if (direction < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return comparisons;
    }

    /** Selection sort, written out the long way so students can watch it work. */
    private void selectionSortWithLog(List<Species> list, ConsoleUI ui) {
        for (int i = 0; i < list.size() - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getBaseStats().total() < list.get(smallest).getBaseStats().total()) {
                    smallest = j;
                }
            }
            Collections.swap(list, i, smallest);

            StringBuilder line = new StringBuilder("    pass " + (i + 1) + ": ");
            for (Species species : list) {
                line.append(species.getName()).append("(")
                        .append(species.getBaseStats().total()).append(") ");
            }
            ui.println(line.toString());
        }
    }
}
