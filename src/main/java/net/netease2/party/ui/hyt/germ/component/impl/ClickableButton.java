/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.party.ui.hyt.germ.component.impl;


import dev.tevarin.ui.font.FontManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public abstract class ClickableButton {
    private int x;
    private int y;
    private int width;
    private int height;
    private String text;

    public ClickableButton(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void drawScreen() {
        GlStateManager.disableBlend();
        int startX = this.x - this.width / 2;
        int endX = this.x + this.width / 2;
        int startY = this.y - this.height / 2;
        int endY = this.y + this.height / 2;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, (float) startX + 0.5f, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, (float) endY - 0.5f, (float) endX - 0.5f, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) endX - 0.5f, startY, endX, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, startY, (float) endX - 0.5f, (float) startY + 0.5f, new Color(44, 44, 44, 102).getRGB());
        FontManager.font18.drawCenteredString(this.text, this.x, startY + (this.height == 20 ? 6 : 2), new Color(216, 216, 216).getRGB());
        GlStateManager.enableBlend();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int endY;
        int endX;
        int startY;
        int startX;
        if (mouseButton == 0 && ClickableButton.isHovered(startX = this.x - this.width / 2, startY = this.y - this.height / 2, endX = this.x + this.width / 2, endY = this.y + this.height / 2, mouseX, mouseY)) {
            this.clicked();
        }
    }

    public abstract void clicked();

    public static boolean isHovered(float x, float y, float x1, float y1, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x1 && (float) mouseY >= y && (float) mouseY <= y1;
    }
}

