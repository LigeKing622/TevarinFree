/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2;

public class Interface {
    public static boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x + width && (float) mouseY >= y && (float) mouseY <= y + height;
    }
}

