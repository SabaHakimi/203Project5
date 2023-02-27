import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Fairy extends Active implements Moving {

    public Fairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Animated sapling = Factory.createSapling(EntityParsing.getSaplingKey() + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(EntityParsing.getSaplingKey()));

                world.addEntity(sapling);

                scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    public boolean uniqueIf(WorldModel world, Point newPos, int horizOrVert) {
        return (horizOrVert == 0 || world.isOccupied(newPos));
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            return Moving.super.moveTo(world, target, scheduler);
        }
    }

}
