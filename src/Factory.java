import processing.core.PImage;

import java.util.List;

public class Factory {

    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;
    private static final String SHREK_KEY = "shrek";
    private static final double SHREK_ANIMATION_PERIOD = 1.00;
    private static final String DONKEY_KEY = "donkey";
    private static final double DONKEY_ANIMATION_PERIOD = 1.00;

    public static Action createAnimationAction(Animated entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public static Action createActivityAction(Active entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }

    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

    public static Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    public static Animated createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, animationPeriod, actionPeriod, health);
    }

    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Animated createSapling(String id, Point position, List<PImage> images) {
        return new Sapling(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, animationPeriod, actionPeriod);
    }

    // need resource count, though it always starts at 0
    public static Animated createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(id, position, images, animationPeriod, actionPeriod, resourceLimit, 0);
    }

    // don't technically need resource count ... full
    public static Animated createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }

    //create shrek bruh

    public static Animated createDonkey(Point position, List<PImage> images) {
        return new Donkey(DONKEY_KEY, position, images, DONKEY_ANIMATION_PERIOD);
    }


}
