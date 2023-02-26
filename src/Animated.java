import processing.core.PImage;

import java.util.List;

public abstract class Animated extends Entity {

    private final double animationPeriod;

    public Animated(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), this.animationPeriod);
    }
}
