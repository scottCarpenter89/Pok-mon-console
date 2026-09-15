package pokemon.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Every area in the game, and the roads between them.
 *
 * <p>TEACHING NOTE (CS III - graph traversal):
 * {@link #shortestPath(String, String)} is a real breadth-first search, written in
 * about twenty lines, working on data students can see in a CSV file. BFS is much
 * easier to sell when the answer it produces is "Pallet Town, Route 1, Viridian
 * City" instead of "node 4, node 7".
 */
public class WorldMap {

    private final Map<String, Area> areas = new LinkedHashMap<>();

    public void add(Area area) {
        areas.put(area.getId(), area);
    }

    public Area get(String id) {
        return areas.get(id);
    }

    public boolean contains(String id) {
        return areas.containsKey(id);
    }

    public List<Area> all() {
        return new ArrayList<>(areas.values());
    }

    /** The areas you can walk to from here, as Area objects rather than IDs. */
    public List<Area> neighboursOf(Area area) {
        List<Area> neighbours = new ArrayList<>();
        for (String id : area.getConnections()) {
            Area neighbour = areas.get(id);
            if (neighbour != null) {
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    /**
     * Breadth-first search: the fewest-steps route from one area to another.
     *
     * <p>BFS explores everything one step away, then everything two steps away, and
     * so on - which is exactly why the first route it finds is the shortest. The
     * {@link Deque} used as a queue is the engine that makes that happen.
     *
     * @return the areas to walk through, starting with {@code fromId} and ending
     *         with {@code toId}, or an empty list when there is no route
     */
    public List<String> shortestPath(String fromId, String toId) {
        List<String> path = new ArrayList<>();
        if (!areas.containsKey(fromId) || !areas.containsKey(toId)) {
            return path;
        }
        Deque<String> queue = new ArrayDeque<>();
        Map<String, String> cameFrom = new LinkedHashMap<>();
        queue.add(fromId);
        cameFrom.put(fromId, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(toId)) {
                break;
            }
            for (String next : areas.get(current).getConnections()) {
                if (areas.containsKey(next) && !cameFrom.containsKey(next)) {
                    cameFrom.put(next, current);
                    queue.add(next);
                }
            }
        }
        if (!cameFrom.containsKey(toId)) {
            return path;    // No route exists.
        }
        // Walk the trail of breadcrumbs backwards, then flip it around.
        for (String at = toId; at != null; at = cameFrom.get(at)) {
            path.add(0, at);
        }
        return path;
    }

    public int size() {
        return areas.size();
    }
}
