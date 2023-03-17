import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Shrek extends Fairy {

    public Shrek(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> shrekTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(DudeNotFull.class, DudeFull.class)));

        if (shrekTarget.isPresent()) {
            Point tgtPos = shrekTarget.get().getPosition();

            if (this.moveTo(world, shrekTarget.get(), scheduler)) {

                /* turn to donkey */
                Animated sapling = Factory.createSapling(EntityParsing.getSaplingKey() + "_" + shrekTarget.get().getId(), tgtPos, imageStore.getImageList(EntityParsing.getSaplingKey()));

                world.addEntity(sapling);

                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }
}


