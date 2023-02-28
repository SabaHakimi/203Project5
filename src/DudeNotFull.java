import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class DudeNotFull extends Active implements Moving {

    private final int resourceLimit;
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Animated dude = Factory.createDudeFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.resourceLimit, this.getImages());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean uniqueIf(WorldModel world, Point newPos, int horizOrVert) {
        return (horizOrVert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {

        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            this.resourceCount = this.resourceCount + 1;
            if (target instanceof Plant p) {
                p.decrementHealth();
            }

            return true;
        }
        else {
            return Moving.super.moveTo(world, target, scheduler);
        }
    }

}
