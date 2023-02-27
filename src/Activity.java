/**
 * An action that can be taken by an entity
 */
public final class Activity implements Action {

    private final Active entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Active entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        entity.executeActivity(this.world, this.imageStore, scheduler);
    }
}
