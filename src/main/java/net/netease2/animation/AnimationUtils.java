/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.animation;


import dev.tevarin.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public class AnimationUtils
        extends Util {
    private static float getDebugFPS() {
        return Minecraft.getDebugFPS() <= 5 ? 60.0f : (float) Minecraft.getDebugFPS();
    }

    public static float lstransition(float now, float desired, double speed) {
        final double dif = Math.abs(desired - now);
        float a = (float) Math.abs((desired - (desired - (Math.abs(desired - now)))) / (100 - (speed * 10)));
        float x = now;

        if (dif > 0) {
            if (now < desired)
                x += a * RenderUtil.deltaTime;
            else if (now > desired)
                x -= a * RenderUtil.deltaTime;
        } else
            x = desired;

        if (Math.abs(desired - x) < 10.0E-3 && x != desired)
            x = desired;

        return x;
    }

    public static double base(double current, double target, double speed) {
        return Double.isNaN(current + (target - current) * speed / (double) (AnimationUtils.getDebugFPS() / 60.0f)) ? 0.0 : current + (target - current) * speed / (double) (AnimationUtils.getDebugFPS() / 60.0f);
    }

    public static float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
        boolean larger = end > current;
        if (smoothSpeed < 0.0f) {
            smoothSpeed = 0.0f;
        } else if (smoothSpeed > 1.0f) {
            smoothSpeed = 1.0f;
        }
        if (minSpeed < 0.0f) {
            minSpeed = 0.0f;
        } else if (minSpeed > 1.0f) {
            minSpeed = 1.0f;
        }
        float movement = (end - current) * smoothSpeed;
        if (movement > 0) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        if (larger) {
            if (end <= current + movement) {
                return end;
            }
        } else {
            if (end >= current + movement) {
                return end;
            }
        }
        return current + movement;
    }

    public static float animate(float target, float current, float speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0F) {
            speed = 0.0F;
        } else if (speed > 1.0F) {
            speed = 1.0F;
        }

        double dif = Math.max(target, (double) current) - Math.min(target, (double) current);
        double factor = dif * (double) speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += (float) factor;
            if (current >= target) current = target;
        } else if (target < current) {
            current -= (float) factor;
            if (current <= target) current = target;
        }

        return current;
    }

    public static double linear(long startTime, long duration, double start, double end) {
        return (end - start) * ((double) (System.currentTimeMillis() - startTime) * 1.0 / (double) duration) + start;
    }

    public static double easeInQuad(long startTime, long duration, double start, double end) {
        return (end - start) * Math.pow((double) (System.currentTimeMillis() - startTime) * 1.0 / (double) duration, 2.0) + start;
    }

    public static double easeOutQuad(long startTime, long duration, double start, double end) {
        float x = (float) (System.currentTimeMillis() - startTime) * 1.0f / (float) duration;
        float y = -x * x + 2.0f * x;
        return start + (end - start) * (double) y;
    }

    public static double easeInOutQuad(long startTime, long duration, double start, double end) {
        float t = (float) (System.currentTimeMillis() - startTime) * 1.0f / (float) duration;
        if ((t *= 2.0f) < 1.0f) {
            return (end - start) / 2.0 * (double) t * (double) t + start;
        }
        return -(end - start) / 2.0 * (double) ((t -= 1.0f) * (t - 2.0f) - 1.0f) + start;
    }

    public static double easeInElastic(double t, double b, double c, double d) {
        double s = 1.70158;
        double p = 0.0;
        double a = c;
        if (t == 0.0) {
            return b;
        }
        if ((t /= d) == 1.0) {
            return b + c;
        }
        p = d * 0.3;
        if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0;
        } else {
            s = p / (Math.PI * 2) * Math.asin(c / a);
        }
        return -(a * Math.pow(2.0, 10.0 * (t -= 1.0)) * Math.sin((t * d - s) * (Math.PI * 2) / p)) + b;
    }

    public static double easeOutElastic(double t, double b, double c, double d) {
        double s = 1.70158;
        double p = 0.0;
        double a = c;
        if (t == 0.0) {
            return b;
        }
        if ((t /= d) == 1.0) {
            return b + c;
        }
        p = d * 0.3;
        if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0;
        } else {
            s = p / (Math.PI * 2) * Math.asin(c / a);
        }
        return a * Math.pow(2.0, -10.0 * t) * Math.sin((t * d - s) * (Math.PI * 2) / p) + c + b;
    }

    public static double easeInOutElastic(double t, double b, double c, double d) {
        double s = 1.70158;
        double p = 0.0;
        double a = c;
        if (t == 0.0) {
            return b;
        }
        if ((t /= d / 2.0) == 2.0) {
            return b + c;
        }
        p = d * 0.44999999999999996;
        if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0;
        } else {
            s = p / (Math.PI * 2) * Math.asin(c / a);
        }
        if (t < 1.0) {
            return -0.5 * (a * Math.pow(2.0, 10.0 * (t -= 1.0)) * Math.sin((t * d - s) * (Math.PI * 2) / p)) + b;
        }
        return a * Math.pow(2.0, -10.0 * (t -= 1.0)) * Math.sin((t * d - s) * (Math.PI * 2) / p) * 0.5 + c + b;
    }

    public static double easeInBack(double t, double b, double c, double d) {
        double s = 1.70158;
        return c * (t /= d) * t * ((s + 1.0) * t - s) + b;
    }

    public static double easeOutBack(double t, double b, double c, double d) {
        double s = 1.70158;
        t = t / d - 1.0;
        return c * (t * t * ((s + 1.0) * t + s) + 1.0) + b;
    }
}

