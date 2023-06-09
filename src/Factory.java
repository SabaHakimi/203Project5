import processing.core.PImage;

import java.util.List;

public class Factory {

    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;
    private static final String SHREK_KEY = "shrek";
    private static final double SHREK_ANIMATION_PERIOD = 0.250;
    private static final double SHREK_ACTION_PERIOD = 1.00;
    private static final String DONKEY_KEY = "donkey";
    private static final double DONKEY_ANIMATION_PERIOD = 0.15;

    private static final double DONKEY_ACTION_PERIOD = 0.6;
    private static final String CAPYBARA_KEY = "capybara";
    private static final double CAPYBARA_ANIMATION_PERIOD = 0.20;
    private static final double CAPYBARA_ACTION_PERIOD = 1.00;

    private static final String HUT_KEY = "hut";


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
        return new DudeNotFull(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }
    //same constructor but is creating capybara

    public static Animated createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }

    public static Animated createShrek( Point position, List<PImage> images) {
        return new Shrek(SHREK_KEY, position, images, SHREK_ANIMATION_PERIOD, SHREK_ACTION_PERIOD);
    }
    public static Animated createCapybara( Point position, List<PImage> images) {
        return new Capybara(CAPYBARA_KEY, position, images, CAPYBARA_ANIMATION_PERIOD, CAPYBARA_ACTION_PERIOD, Integer.MAX_VALUE);
    }

    public static Animated createDonkey(Point position, List<PImage> images) {
        return new Donkey(DONKEY_KEY, position, images, DONKEY_ANIMATION_PERIOD, DONKEY_ACTION_PERIOD);
    }
    public static Entity createHut(Point position, List<PImage> images) {
        return new Hut(HUT_KEY, position, images);
    }
}
