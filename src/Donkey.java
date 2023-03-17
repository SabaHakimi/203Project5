import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Donkey extends Animated implements Moving {

    public Donkey(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images, animationPeriod);
    }

        public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
            if (Point.adjacent(this.getPosition(), target.getPosition())) {
                //Idle animation here
                return true;
            } else {
                return Moving.super.moveTo(world, target, scheduler);
            }
        }

        @Override
        public boolean uniqueIf(WorldModel world, Point newPos) {
            return Moving.super.uniqueIf(world, newPos) && !world.isOccupied(newPos);
        }
    }
