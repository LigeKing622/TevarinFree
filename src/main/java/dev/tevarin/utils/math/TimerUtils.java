//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tevarin.utils.math;

public final class TimerUtils {
    public long lastMs;
    public long lastMS = System.currentTimeMillis();
    public long time;
    private long lastTime;

    public TimerUtils() {
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }

            return true;
        } else {
            return false;
        }
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        if (milliseconds == 0.0) {
            return true;
        } else {
            return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
        }
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public boolean hasTimeElapsed(long time) {
        this.time = time;
        return System.currentTimeMillis() - this.lastMS > this.time;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setTime(long time) {
        this.lastMS = time;
    }

    public boolean hasTimePassed(long time) {
        return System.currentTimeMillis() - this.lastTime >= time;
    }

    public boolean delay(double nextDelay) {
        return (double) (System.currentTimeMillis() - this.lastMs) >= nextDelay;
    }

    public long passed() {
        return this.getCurrentMS() - this.lastMS;
    }
}
