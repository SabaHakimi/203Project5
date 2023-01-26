import java.util.*;

/**
 * Keeps track of events that have been scheduled.
 */
public final class EventScheduler {
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double currentTime;

    public EventScheduler() {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.currentTime = 0;
    }

    public PriorityQueue<Event> getEventQueue() {
        return eventQueue;
    }

    public Map<Entity, List<Event>> getPendingEvents() {
        return pendingEvents;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double t) {
        currentTime = t;
    }

    public void removePendingEvent(Event event) {
        List<Event> pending = this.getPendingEvents().get(event.getEntity());

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void unscheduleAllEvents(Entity entity) {
        List<Event> pending = this.getPendingEvents().remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                this.getEventQueue().remove(event);
            }
        }
    }

    public void scheduleEvent(Entity entity, Action action, double afterPeriod) {
        double time = this.getCurrentTime() + afterPeriod;

        Event event = new Event(action, time, entity);

        this.getEventQueue().add(event);

        // update list of pending events for the given entity
        List<Event> pending = this.getPendingEvents().getOrDefault(entity, new LinkedList<>());
        pending.add(event);
        this.getPendingEvents().put(entity, pending);
    }


}
