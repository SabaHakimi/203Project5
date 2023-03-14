import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        // Initializing Vars
        List<Point> path = new LinkedList<Point>();
        PriorityQueue<Node> openList = new PriorityQueue<>(10, Comparator.comparing(Node::getF).thenComparing(Node::getH));
        HashMap<Point, Node> openSet = new HashMap<>();
        HashSet<Point> closedList = new HashSet<>();
        Node startNode = new Node(start, calcDistance(start, end) , 0, calcDistance(start, end), null);
        openList.add(startNode);
        Node cur = startNode;

        while (!(withinReach.test(cur.getLocation(), end)) /*&& openList.size() > 0*/) {
            List<Point> neighbors = potentialNeighbors.apply(cur.getLocation()).filter(canPassThrough).filter((p) -> (!closedList.contains(p))).toList();
            for (Point p: neighbors) {
                Node n = null;
                boolean contains = false;
                int g = cur.getG() + 1;
                // If node already on open set:
                if (openSet.containsKey(p)) {
                    contains = true;
                    n = openSet.get(p);
                    if (g > n.getG())
                        // Continue
                        n.setG(g);
                    else { continue; }

                }

                // Estimate distance of neighbor to end point
                double h = calcDistance(p, end);
                // Get F value and Put in open list/set
                double f = g + h;
                Node next = new Node(p, f, g, h, cur);
                openSet.put(p, next);
                if (contains) {
                    openList.remove(n);
                }
                openList.add(next);
            }

            // Add current node to closed list
            openList.remove(cur);
            closedList.add(cur.getLocation());
            cur = openList.poll();
            if (cur == null) {
                return path;
            }
        }
        // Making Path Recursively
        while (cur.getPrev() != null) {
            path.add(0, cur.getLocation());
            cur = cur.getPrev();
        }
        //System.out.println(path);
        return path;
    }



    private static double calcDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p2.y, 2) + Math.pow(p2.y - p1.y, 2));
    }

    /*
    private static List<Point> getNeighbors(Node cur) {
        List<Point> neighbors = new ArrayList<>(4);
        neighbors.add(new Point(cur.getLocation().x + 1, cur.getLocation().y));
        neighbors.add(new Point(cur.getLocation().x - 1, cur.getLocation().y));
        neighbors.add(new Point(cur.getLocation().x, cur.getLocation().y + 1));
        neighbors.add(new Point(cur.getLocation().x, cur.getLocation().y - 1));
        return neighbors;
    }

    private static int manhattanDistance(Point p1, Point p2) {
        return (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y));
    }
    */
}
