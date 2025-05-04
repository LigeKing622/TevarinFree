/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.animation;

public class Animation {
    private long duration = 0L;
    private long startTime = 0L;
    private double start = 0.0;
    public double value = 0.0;
    public double end = 0.0;
    private Type type = Type.LINEAR;
    private boolean started = false;

    public void start(double start, double end, float duration, Type type) {
        if (!(this.started || start == this.start && end == this.end && (long) (duration * 1000.0f) == this.duration && type == this.type)) {
            this.duration = (long) (duration * 1000.0f);
            this.start = start;
            this.startTime = System.currentTimeMillis();
            this.value = start;
            this.end = end;
            this.type = type;
            this.started = true;
        }
    }

    public void update() {
        if (!this.started) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - this.startTime;

        switch (this.type) {
            case LINEAR:
                this.value = AnimationUtils.linear(this.startTime, this.duration, this.start, this.end);
                break;
            case EASE_IN_QUAD:
                this.value = AnimationUtils.easeInQuad(this.startTime, this.duration, this.start, this.end);
                break;
            case EASE_OUT_QUAD:
                this.value = AnimationUtils.easeOutQuad(this.startTime, this.duration, this.start, this.end);
                break;
            case EASE_IN_OUT_QUAD:
                this.value = AnimationUtils.easeInOutQuad(this.startTime, this.duration, this.start, this.end);
                break;
            case EASE_IN_ELASTIC:
                this.value = AnimationUtils.easeInElastic(elapsedTime, this.start, this.end - this.start, this.duration);
                break;
            case EASE_OUT_ELASTIC:
                this.value = AnimationUtils.easeOutElastic(elapsedTime, this.start, this.end - this.start, this.duration);
                break;
            case EASE_IN_OUT_ELASTIC:
                this.value = AnimationUtils.easeInOutElastic(elapsedTime, this.start, this.end - this.start, this.duration);
                break;
            case EASE_IN_BACK:
                this.value = AnimationUtils.easeInBack(elapsedTime, this.start, this.end - this.start, this.duration);
                break;
            case EASE_OUT_BACK:
                this.value = AnimationUtils.easeOutBack(elapsedTime, this.start, this.end - this.start, this.duration);
                break;
            default:
                this.value = this.value;
        }

        if (currentTime - this.startTime > this.duration) {
            this.started = false;
            this.value = this.end;
        }
    }


    public void reset() {
        this.value = 0.0;
        this.start = 0.0;
        this.end = 0.0;
        this.startTime = System.currentTimeMillis();
        this.started = false;
    }

    public void fstart(double start, double end, float duration, Type type) {
        this.started = false;
        this.start(start, end, duration, type);
    }

    public double getValue() {
        return this.value;
    }

    public boolean isStarted() {
        return this.started;
    }
}

