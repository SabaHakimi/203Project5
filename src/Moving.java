import java.util.List;
import java.util.function.Predicate;

public interface Moving {

    Point getPosition();

    default boolean uniqueIf(WorldModel world, Point newPos/*, int horizOrVert*/) {
        return (world.withinBounds(newPos));
    }

    default Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                getPosition(),
                destPos,
                // lambda that takes in a point and returns boolean based on the rule for the type
                (p) -> uniqueIf(world, p),
                // lambda that takes in 2 points and returns if they are adjacent
                (p1, p2) -> Point.adjacent(p1, p2),
                PathingStrategy.CARDINAL_NEIGHBORS

        );
        // add a check for if path is null / empty -> current pos if so
        if (path.size() != 0) {
            //System.out.println(path.get(0));
            return path.get(0);
        }
        else {
            return getPosition();
        }
    }

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        Point nextPos = this.nextPosition(world, target.getPosition());
        if (!this.getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, (Entity) this, nextPos);
        }
        return false;
    }
}
