import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity {
    private EntityKind kind;
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private double actionPeriod;
    private double animationPeriod;
    private int health;
    private int healthLimit;

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

    public EntityKind getKind() {
        return kind;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        position = p;
    }

    public List<PImage> getImages() {
        return images;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public double getActionPeriod() {
        return actionPeriod;
    }

    public int getHealth() {
        return health;
    }

    public int getHealthLimit() {
        return healthLimit;
    }

    public Point nextPositionDude(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || Functions.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position);

            if (!this.position.equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            this.resourceCount = this.resourceCount + 1;
            target.health = target.health - 1;
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.position);

            if (!this.position.equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.position)) {
            Functions.removeEntity(world, scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.position);

            if (!this.position.equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.id, this.position, Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, stump);

            return true;
        } else if (this.health >= this.healthLimit) {
            Entity tree = Functions.createTree(Functions.TREE_KEY + "_" + this.id, this.position, Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), Functions.getImageList(imageStore, Functions.TREE_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.id, this.position, Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, stump);

            return true;
        }

        return false;
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.kind == EntityKind.TREE) {
            return this.transformTree(world, scheduler, imageStore);
        } else if (this.kind == EntityKind.SAPLING) {
            return this.transformSapling(world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", this));
        }
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Functions.createDudeNotFull(this.id, this.position, this.actionPeriod, this.getAnimationPeriod(), this.resourceLimit, this.images);

        Functions.removeEntity(world, scheduler, this);

        Functions.addEntity(world, dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            Entity dude = Functions.createDudeFull(this.id, this.position, this.actionPeriod, this.getAnimationPeriod(), this.resourceLimit, this.images);

            Functions.removeEntity(world, scheduler, this);
            scheduler.unscheduleAllEvents(this);

            Functions.addEntity(world, dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (this.kind) {
            case DUDE_FULL:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case DUDE_NOT_FULL:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case OBSTACLE:
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case FAIRY:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case SAPLING:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case TREE:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            default:
        }
    }

    public void executeDudeFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, this.position, new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && this.moveToFull(world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeDudeNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !this.moveToNotFull(world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, this.position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {

                Entity sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, Functions.getImageList(imageStore, Functions.SAPLING_KEY), 0);

                Functions.addEntity(world, sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
        }
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health = this.health + 1;
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.actionPeriod);
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
