import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public double actionPeriod;
    public double animationPeriod;
    public int health;
    public int healthLimit;

    public Entity(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Functions.createDudeNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

        Functions.removeEntity(world, scheduler, this);

        Functions.addEntity(world, dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Entity dude = Functions.createDudeFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

            Functions.removeEntity(world, scheduler, this);
            Functions.unscheduleAllEvents(scheduler, this);

            Functions.addEntity(world, dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (this.kind) {
            case DUDE_FULL:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case DUDE_NOT_FULL:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case OBSTACLE:
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case FAIRY:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case SAPLING:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case TREE:
                Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                Functions.scheduleEvent(scheduler, this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            default:
        }
    }

    public void executeDudeFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, this.position, new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && Functions.moveToFull(this, world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeDudeNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !Functions.moveToNotFull(this, world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, this.position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (Functions.moveToFairy(this, world, fairyTarget.get(), scheduler)) {

                Entity sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, Functions.getImageList(imageStore, Functions.SAPLING_KEY), 0);

                Functions.addEntity(world, sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!Functions.transformPlant(this, world, scheduler, imageStore)) {

            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!Functions.transformPlant(this, world, scheduler, imageStore)) {
            Functions.scheduleEvent(scheduler, this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public double getAnimationPeriod() {
        switch (this.kind) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return this.animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", this.kind));
        }
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }
}
