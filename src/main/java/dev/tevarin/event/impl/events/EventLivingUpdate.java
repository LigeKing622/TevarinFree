package dev.tevarin.event.impl.events;


import dev.tevarin.event.impl.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate implements Event {
    private final Entity entity;

    public EventLivingUpdate(Entity entity) {
        this.entity = entity;

    }

    public Entity getEntity() {
        return this.entity;
    }
}
