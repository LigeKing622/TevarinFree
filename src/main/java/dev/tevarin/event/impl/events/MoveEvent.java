package dev.tevarin.event.impl.events;

import dev.tevarin.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/8/9 下午9:08
 */
@AllArgsConstructor
@Setter
public class MoveEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;
}
