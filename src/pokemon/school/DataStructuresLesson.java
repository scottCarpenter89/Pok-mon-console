package pokemon.school;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pokemon.data.GameData;
import pokemon.model.Species;
import pokemon.ui.ConsoleUI;
import pokemon.util.RandomSource;
import pokemon.util.Text;
import pokemon.world.Area;

/** CS III - stacks, queues, maps and graphs, all visible in this game. */
public class DataStructuresLesson implements Lesson {

    @Override
    public String title() {
        return "Data structures";
    }

    @Override
    public String courseLevel() {
        return "CS III";
    }

    @Override
    public String concept() {
        return "stack, queue, map, graph";
    }

    @Override
    public void teach(ConsoleUI ui, GameData data, RandomSource rng) {
        stackDemo(ui);
        queueDemo(ui);
        mapDemo(ui, data);
        graphDemo(ui, data);
    }

    /** LIFO - last in, first out. */
    private void stackDemo(ConsoleUI ui) {
        ui.println("  STACK (last in, first out) - like an undo history.");
        Deque<String> stack = new ArrayDeque<>();
        for (String action : List.of("Ember", "Switch to Pikachu", "Potion", "Thunderbolt")) {
            stack.push(action);
            ui.println("    push(\"" + action + "\")   stack is now " + stack);
        }
        ui.println("    pop() -> " + stack.pop() + "   (the most recent action undone first)");
        ui.println("    pop() -> " + stack.pop());
        ui.blank();
    }

    /** FIFO - first in, first out. */
    private void queueDemo(ConsoleUI ui) {
        ui.println("  QUEUE (first in, first out) - like the line at a Pokemon Center.");
        Deque<String> queue = new ArrayDeque<>();
        for (String trainer : List.of("Joey", "Janine", "Rick", "Sam")) {
            queue.add(trainer);
            ui.println("    add(\"" + trainer + "\")      queue is now " + queue);
        }
        ui.println("    poll() -> " + queue.poll() + "   (arrived first, served first)");
        ui.println("    poll() -> " + queue.poll());
        ui.blank();
        ui.println("  Same two operations, opposite order. Choosing the right one is the");
        ui.println("  entire lesson - and breadth-first search below depends on the queue.");
        ui.blank();
    }

    /** Hash maps: instant lookup by key. */
    private void mapDemo(ConsoleUI ui, GameData data) {
        ui.println("  MAP (key -> value) - how the game finds a species by its id.");
        Map<String, Species> index = new HashMap<>();
        for (Species species : data.allSpecies()) {
            index.put(species.getId(), species);
        }
        ui.println("    index.get(\"gyarados\") -> " + index.get("gyarados")
                + " (" + index.get("gyarados").typeLine() + ")");
        ui.println("    index.get(\"mewtwo\")   -> " + index.get("mewtwo")
                + "   <-- null means 'not in the map'");
        ui.blank();
        ui.println("  A HashMap finds an entry in roughly the same time whether it holds "
                + index.size());
        ui.println("  entries or ten million: O(1). Searching an ArrayList would be O(n).");
        ui.blank();
    }

    /** Graphs and breadth-first search. */
    private void graphDemo(ConsoleUI ui, GameData data) {
        ui.println("  GRAPH - the world map is nodes (areas) joined by edges (paths).");
        List<String> lines = new ArrayList<>();
        for (Area area : data.getWorldMap().all()) {
            lines.add(Text.pad(area.getName(), 20) + "-> " + String.join(", ",
                    area.getConnections()));
        }
        ui.box("The map as a graph", lines);

        List<Area> areas = data.getWorldMap().all();
        String from = areas.get(0).getId();
        String to = areas.get(areas.size() - 1).getId();
        List<String> path = data.getWorldMap().shortestPath(from, to);

        ui.blank();
        ui.println("  Breadth-first search from " + from + " to " + to + ":");
        ui.println("    " + String.join(" -> ", path));
        ui.println("    (" + Math.max(0, path.size() - 1) + " steps - and BFS guarantees");
        ui.println("     there is no shorter route.)");
        ui.blank();
        ui.println("  The code is in WorldMap.shortestPath(). It is the same algorithm that");
        ui.println("  powers maps apps, social-network 'degrees of separation' and puzzle");
        ui.println("  solvers. Twenty lines, using the queue you met a moment ago.");
    }
}
