import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class DudeFull extends Transforms implements Moving {

    private final int resourceLimit;

    public DudeFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && this.moveToFull(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Animated dude = Factory.createDudeNotFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.resourceLimit, this.getImages());

        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);
        return false;
    }

    public boolean uniqueIf(WorldModel world, Point newPos, int horizOrVert) {
        return (horizOrVert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class);
    }

    private boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

}
