package dev.tevarin.event.impl.events;

import dev.tevarin.event.impl.Event;
import lombok.Getter;
import lombok.Setter;
import org.lwjglx.util.vector.Vector2f;

@Getter
@Setter
public class EventLook
        implements Event {
    private Vector2f rotation;

    public EventLook(Vector2f rotation) {
        this.rotation = rotation;
    }

}

