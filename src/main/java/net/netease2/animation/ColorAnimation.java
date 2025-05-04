/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.animation;


import dev.tevarin.utils.render.RenderUtil;

import java.awt.*;

public class ColorAnimation {
    Animation r = new Animation();
    Animation g = new Animation();
    Animation b = new Animation();
    Animation a = new Animation();
    boolean first = true;
    Color end;

    public ColorAnimation() {
    }

    public ColorAnimation(Color color) {
        this.setColor(color);
    }

    public ColorAnimation(int red, int green, int blue, int alpha) {
        this.setColor(new Color(red, green, blue, alpha));
    }

    public void start(Color start, Color end, float duration, Type type) {
        this.end = end;
        this.r.start(start.getRed(), end.getRed(), duration, type);
        this.g.start(start.getGreen(), end.getGreen(), duration, type);
        this.b.start(start.getBlue(), end.getBlue(), duration, type);
        this.a.start(start.getAlpha(), end.getAlpha(), duration, type);
    }

    public void update() {
        if (this.end != null) {
            if (this.first) {
                this.setColor(this.end);
                this.first = false;
                return;
            }
            this.r.update();
            this.g.update();
            this.b.update();
            this.a.update();
        }
    }

    public void reset() {
        this.r.reset();
        this.g.reset();
        this.b.reset();
        this.a.reset();
    }

    public Color getColor() {
        return new Color(RenderUtil.limit(this.r.getValue()), RenderUtil.limit(this.g.getValue()), RenderUtil.limit(this.b.getValue()), RenderUtil.limit(this.a.getValue()));
    }

    public void fstart(Color color, Color color1, float duration, Type type) {
        this.end = color1;
        this.r.fstart(color.getRed(), color1.getRed(), duration, type);
        this.g.fstart(color.getGreen(), color1.getGreen(), duration, type);
        this.b.fstart(color.getBlue(), color1.getBlue(), duration, type);
        this.a.fstart(color.getAlpha(), color1.getAlpha(), duration, type);
    }

    public void setColor(Color color) {
        this.r.value = color.getRed();
        this.g.value = color.getGreen();
        this.b.value = color.getBlue();
        this.a.value = color.getAlpha();
    }

    public void base(Color color) {
        this.r.value = AnimationUtils.base(this.r.value, color.getRed(), 0.1f);
        this.g.value = AnimationUtils.base(this.g.value, color.getGreen(), 0.1f);
        this.b.value = AnimationUtils.base(this.b.value, color.getBlue(), 0.1f);
        this.a.value = AnimationUtils.base(this.a.value, color.getAlpha(), 0.1f);
    }
}

