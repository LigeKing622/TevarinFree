package dev.tevarin.ui.hud;

import dev.tevarin.Client;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.ui.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.input.Mouse;

import java.awt.*;

public abstract class HUD extends Gui {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    public int height;
    public int width;
    public boolean drag = false;
    public Module m;
    public boolean noRect = false;
    public int alpha = 100;
    private int posX = 0;
    private int posY = 0;
    private float dragX;
    private float dragY;

    public HUD(int width, int height, String name) {
        Client.instance.eventManager.register(this);
        this.height = height;
        this.width = width;
        this.name = name;
        this.m = new Module(name, Category.HUD);
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.posX && mouseX <= this.posX + this.width && mouseY >= this.posY && mouseY <= this.posY + this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public abstract void drawShader();

    public abstract void drawHUD(int var1, int var2, float var3);

    public abstract void predrawhud();

    public abstract void onTick();

    public void doDrag(int mouseX, int mouseY) {
        if (this.drag && this.m.getState()) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.posX = (int) ((float) mouseX - this.dragX);
            this.posY = (int) ((float) mouseY - this.dragY);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (mouseX > this.posX && mouseX < this.posX + this.width && mouseY > this.posY - 2 && mouseY < this.posY + this.height && this.m.getState()) {
            if (button == 1) {
                this.m.toggle();
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.posX;
                this.dragY = mouseY - this.posY;
            }
        }
    }

    public void draw(float partialTicks) {
        GlStateManager.resetColor();
        this.drawHUD(this.posX, this.posY, partialTicks);
    }

    public void predraw() {
        GlStateManager.resetColor();
        this.predrawhud();
    }

    public void renderTag() {
        if (!this.m.getState()) {
            return;
        }
        FontManager.font16.drawString(this.name, this.posX, this.posY - 2 - FontManager.font16.getHeight(), new Color(255, 255, 255, this.alpha).getRGB());
    }

    public boolean isDrag() {
        return this.drag;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return this.name;
    }
}
