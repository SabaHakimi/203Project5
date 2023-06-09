import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Donkey extends Active implements Moving {

    public Donkey(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> shrek = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Shrek.class)));

        if (shrek.isPresent()) {
            if (this.moveTo(world, shrek.get(), scheduler)) {
                this.setImages(imageStore.getImageList("donkeyidle"));
            }
            else {
                this.setImages(imageStore.getImageList("donkey"));
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    @Override
    public boolean uniqueIf(WorldModel world, Point newPos) {
        return Moving.super.uniqueIf(world, newPos) && !world.isOccupied(newPos);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.nearby(this.getPosition(), target.getPosition())) {
            return true;
        } else {
            return Moving.super.moveTo(world, target, scheduler);
        }
    }
}
