import processing.core.PImage;

import java.util.List;

public abstract class Active extends Animated {

    private final double actionPeriod;

    public Active(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.actionPeriod);
        super.scheduleActions(scheduler, world, imageStore);
    }

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

}
