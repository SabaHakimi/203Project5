/**
 * An action that can be taken by an entity
 */
public final class Action {
    private final ActionKind kind;
    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    private void executeActivityAction(EventScheduler scheduler) {
        switch (this.entity.getKind()) {
            case SAPLING -> this.entity.executeSaplingActivity(this.world, this.imageStore, scheduler);
            case TREE -> this.entity.executeTreeActivity(this.world, this.imageStore, scheduler);
            case FAIRY -> this.entity.executeFairyActivity(this.world, this.imageStore, scheduler);
            case DUDE_NOT_FULL -> this.entity.executeDudeNotFullActivity(this.world, this.imageStore, scheduler);
            case DUDE_FULL -> this.entity.executeDudeFullActivity(this.world, this.imageStore, scheduler);
            default ->
                    throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", this.entity.getKind()));
        }
    }

    private void executeAnimationAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, Factory.createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }

    public void executeAction(EventScheduler scheduler) {
        switch (this.kind) {
            case ACTIVITY -> this.executeActivityAction(scheduler);
            case ANIMATION -> this.executeAnimationAction(scheduler);
        }
    }
}
