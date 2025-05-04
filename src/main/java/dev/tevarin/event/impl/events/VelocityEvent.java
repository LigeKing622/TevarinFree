package dev.tevarin.event.impl.events;


import dev.tevarin.event.impl.Event;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TG_format
 * @since 2024/6/1 13:04
 */
@Getter
@Setter
public class VelocityEvent implements Event {
    private double x;
    private double y;
    private double z;

    public VelocityEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
