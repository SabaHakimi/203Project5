import processing.core.PImage;

import java.util.List;

public abstract class Transforms extends Active {

    public Transforms(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

}
