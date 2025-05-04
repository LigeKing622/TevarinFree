package dev.tevarin.event.impl.events;


import dev.tevarin.event.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSlowDown extends CancellableEvent {

    private float strafeMultiplier;
    private float forwardMultiplier;

    public EventSlowDown(float strafeMultiplier, float forwardMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
        this.forwardMultiplier = forwardMultiplier;
    }
}
