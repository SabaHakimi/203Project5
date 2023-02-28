import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Tree extends Plant {

    private int health;

    public Tree(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, int health) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    public int getHealth() { return this.health; }

    public void decrementHealth() { this.health -= 1; }

}
