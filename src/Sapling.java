import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Sapling extends Plant {

    private int health;
    private final int healthLimit;
    private static final double TREE_ANIMATION_MAX = 0.600;
    private static final double TREE_ANIMATION_MIN = 0.050;
    private static final double TREE_ACTION_MAX = 1.400;
    private static final double TREE_ACTION_MIN = 1.000;
    private static final int TREE_HEALTH_MAX = 3;
    private static final int TREE_HEALTH_MIN = 1;

    public Sapling(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health, int healthLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public int getHealth() { return this.health; }

    public void decrementHealth() { this.health -= 1; }

    private static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    private static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health = this.health + 1;
        super.executeActivity(world, imageStore, scheduler);
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (super.transform(world, scheduler, imageStore)) {
            return true;
        } else if (this.health >= this.healthLimit) {
            Animated tree = Factory.createTree(EntityParsing.getTreeKey() + "_" + this.getId(), this.getPosition(), getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(EntityParsing.getTreeKey()));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

}
