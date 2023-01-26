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

    public Point nextPositionDude(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).getKind() != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).getKind() != EntityKind.STUMP) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    public Point nextPositionFairy(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || Functions.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    public boolean moveToFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            this.resourceCount = this.getResourceCount() + 1;
            target.health = target.getHealth() - 1;
            return true;
        } else {
            Point nextPos = this.nextPositionDude(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            Functions.removeEntity(world, scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Functions.moveEntity(world, scheduler, this, nextPos);
            }
            return false;
        }
    }

    public boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.getId(), this.getPosition(), Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, stump);

            return true;
        } else if (this.getHealth() >= this.getHealthLimit()) {
            Entity tree = Functions.createTree(Functions.TREE_KEY + "_" + this.getId(), this.getPosition(), Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), Functions.getImageList(imageStore, Functions.TREE_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.getId(), this.getPosition(), Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, scheduler, this);

            Functions.addEntity(world, stump);

            return true;
        }

        return false;
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getKind() == EntityKind.TREE) {
            return this.transformTree(world, scheduler, imageStore);
        } else if (this.getKind() == EntityKind.SAPLING) {
            return this.transformSapling(world, scheduler, imageStore);
        } else {
            throw new UnsupportedOperationException(String.format("transformPlant not supported for %s", this));
        }
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Entity dude = Functions.createDudeNotFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit(), this.getImages());

        Functions.removeEntity(world, scheduler, this);

        Functions.addEntity(world, dude);
        dude.scheduleActions(scheduler, world, imageStore);
    }

    public boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= this.getResourceLimit()) {
            Entity dude = Functions.createDudeFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit(), this.getImages());

            Functions.removeEntity(world, scheduler, this);
            scheduler.unscheduleAllEvents(this);

            Functions.addEntity(world, dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        switch (this.getKind()) {
            case DUDE_FULL:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case DUDE_NOT_FULL:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case OBSTACLE:
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case FAIRY:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case SAPLING:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            case TREE:
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent(this, Functions.createAnimationAction(this, 0), getAnimationPeriod());
                break;

            default:
        }
    }

    public void executeDudeFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = Functions.findNearest(world, this.getPosition(), new ArrayList<>(List.of(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && this.moveToFull(world, fullTarget.get(), scheduler)) {
            this.transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public void executeDudeNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, this.getPosition(), new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !this.moveToNotFull(world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public void executeFairyActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, this.getPosition(), new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveToFairy(world, fairyTarget.get(), scheduler)) {

                Entity sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, Functions.getImageList(imageStore, Functions.SAPLING_KEY), 0);

                Functions.addEntity(world, sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }

    public void executeTreeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    public void executeSaplingActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health = this.getHealth() + 1;
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().x, this.getPosition().y, this.getImageIndex());
    }

    public double getAnimationPeriod() {
        switch (this.getKind()) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return this.animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", this.getKind()));
        }
    }

    public void nextImage() {
        this.imageIndex = this.getImageIndex() + 1;
    }
}
