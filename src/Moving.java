import java.util.List;
import java.util.function.Predicate;

public interface Moving {

    Point getPosition();

    default boolean uniqueIf(WorldModel world, Point newPos/*, int horizOrVert*/) {
        return (world.withinBounds(newPos));
    }

    default Point nextPosition(WorldModel world, Point destPos) {
        //PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();
        //Predicate<Point> canPassThrough = (p) -> uniqueIf(world, p);
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

        /*
        Old Code:

        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (uniqueIf(world, newPos, horiz)) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (uniqueIf(world, newPos, vert)) {
                newPos = this.getPosition();
            }
        }

        return newPos;

        */
    }

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        Point nextPos = this.nextPosition(world, target.getPosition());

        if (!this.getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, (Entity) this, nextPos);
        }
        return false;

    }

}
