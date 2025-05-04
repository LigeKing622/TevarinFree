package dev.tevarin.event.impl.events;

import dev.tevarin.event.impl.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class EventWorldLoad implements Event {

    private final WorldClient world;

    public EventWorldLoad(WorldClient world) {
        this.world = world;
    }

    public WorldClient getWorld() {
        return world;
    }
}
