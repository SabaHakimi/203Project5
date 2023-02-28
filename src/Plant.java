import processing.core.PImage;

import java.util.List;

public abstract class Plant extends Active {

    public Plant(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    abstract void decrementHealth();

    abstract int getHealth();

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Factory.createStump(EntityParsing.getStumpKey() + "_" + this.getId(), this.getPosition(), imageStore.getImageList(EntityParsing.getStumpKey()));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }
        return false;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

}
