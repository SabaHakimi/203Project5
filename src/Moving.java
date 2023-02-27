public interface Moving {

    Point getPosition();

    boolean uniqueIf(WorldModel world, Point newPos, int horizOrVert);

    default Point nextPosition(WorldModel world, Point destPos) {
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
    }

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        Point nextPos = this.nextPosition(world, target.getPosition());

        if (!this.getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, (Entity) this, nextPos);
        }
        return false;

    }

}
