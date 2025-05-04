/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.netease2.games;

import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.StopWatch;
import dev.tevarin.utils.render.RoundedUtil;
import net.netease2.Interface;
import net.netease2.animation.Animation;
import net.netease2.animation.ColorAnimation;
import net.netease2.animation.Type;
import net.netease2.game.GermMenuItem;
import net.netease2.game.GermModProcessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HYTSelector
        extends GuiScreen {
    public static int x = -1;
    public static int y = -1;
    public static float width = 0.0f;
    public static float height = 0.0f;
    private boolean drag;
    private float dragX;
    private float dragY;
    private boolean sizeDrag = false;
    private float sizeDragX;
    private float sizeDragY;
    Animation scaleAnimation = new Animation();
    float selection = 0.0f;
    public StopWatch timeInCategory = new StopWatch();
    ColorAnimation sizeDragBorder = new ColorAnimation(255, 255, 255, 0);
    private boolean close;
    String current = "\u751f\u5b58\u6e38\u620f";


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(this.mc);
        if (!Mouse.isButtonDown(0)) {
            this.drag = false;
            this.sizeDrag = false;
        }
        if (this.drag) {
            x = (int) ((float) mouseX - this.dragX);
            y = mouseY -= (int) this.dragY;
        }
        x = (int) Math.max(Math.min((float) x, (float) sr.getScaledWidth() - width - 5.0f), 5.0f);
        y = (int) Math.max(Math.min((float) y, (float) sr.getScaledHeight() - height - 5.0f), 5.0f);
        float w = Math.min((float) mouseX + this.sizeDragX, (float) (sr.getScaledWidth() - 5)) - (float) x;
        float h = Math.min((float) mouseY + this.sizeDragY, (float) (sr.getScaledHeight() - 5)) - (float) y;
        if (this.sizeDrag && (width > 400.0f || w > width)) {
            width = w;
        }
        if (this.sizeDrag && (height > 200.0f || h > height)) {
            height = h;
        }
        width = Math.max(Math.min(width, (float) (sr.getScaledWidth() - 10)), 400.0f);
        height = Math.max(Math.min(height, (float) (sr.getScaledHeight() - 10)), 220.0f);
        if (this.close) {
            if (this.scaleAnimation.value <= 0.5) {
                this.mc.displayGuiScreen(null);
                if (this.mc.currentScreen == null) {
                    this.mc.setIngameFocus();
                }
            }
        } else {
            this.scaleAnimation.start(this.scaleAnimation.value, 1.0, 0.2f, Type.EASE_OUT_BACK);
        }
        this.scaleAnimation.update();
        GlStateManager.translate((double) sr.getScaledWidth() / 2.0, (double) sr.getScaledHeight() / 2.0, 0.0);
        GL11.glScaled((double) this.scaleAnimation.value, (double) this.scaleAnimation.value, (double) 1.0);
        GlStateManager.translate((double) (-sr.getScaledWidth()) / 2.0, (double) (-sr.getScaledHeight()) / 2.0, 0.0);
        RoundedUtil.drawRound((float) (x - 1 + 17), (float) (y - 1 + 10), width - 118.0f, height - 10, 4, this.sizeDragBorder.getColor());
        RoundedUtil.drawRound((float) x + 17, (float) y + 10, width - 120, height - 12, 7, new Color(22, 22, 22, 252));
        RoundedUtil.drawRound((float) x + 101, (float) y + 10, width - 204, height - 12, 7, new Color(17, 17, 17, 252));
        FontManager.font20.drawCenteredString("花雨庭菜单", (float) x + 57.0f, y + 25, -1);
        FontManager.font16.drawStringWithShadow("1.1", (float) x + 28.0f + FontManager.font20.getStringWidth("花雨庭菜单"), y + 21,
                HUD.mainColor.getColor());

        float cY = y + 50;
        float cY2 = y + 30;
        int num = 0;
        for (Map.Entry<String, List<GermMenuItem>> entry : GermModProcessor.hytMainMenuItems.entrySet()) {
            String key = entry.getKey();
            List<GermMenuItem> value = entry.getValue();
            boolean isSelected = Objects.equals(this.current, key);
            final int opacity2 = 255 - (int) Math.min(255, timeInCategory.getElapsedTime() * 2);
            if (Interface.isHovered(x + 15, cY - 6.0f, 75.0f, 18.0f, mouseX, mouseY)) {
                if (Mouse.isButtonDown(0)) {
                    this.current = key;
                }
            }
            if (isSelected) {
                RoundedUtil.drawRound((float) (x + 28), cY - 6.0f, 60.0f, 18.0f, 4, HUD.mainColor.getColorC());
            }
            FontManager.font18.drawCenteredString(key, (float) x + 58.0f, cY, new Color(234, 234, 234).getRGB());
            cY += 20.0f;
            for (GermMenuItem item : value) {
                if (isSelected) {
                    if (Interface.isHovered((float) x + 105.0f + 5.0f, cY2 - 6.0f, width - 394.0f + FontManager.font18.getStringWidth(item.getName()), 18.0f, mouseX, mouseY)) {
                        if (Mouse.isButtonDown(0)) {
                            GermModProcessor.sendJoin(num, item.getId());
                            Minecraft.getMinecraft().displayGuiScreen(null);
                        }
                        RoundedUtil.drawRound((float) x + 108.0f + 5.0f, cY2 - 6.0f, FontManager.font18.getStringWidth(item.getName()) + 22,
                                18.0f, 4,
                                HUD.mainColor.getColorC());
                    }
                    FontManager.font18.drawString(item.getName(), (float) x + 110.0f + 15.0f, cY2, new Color(234, 234, 234).getRGB());
                    cY2 += 20.0f;
                }
                ++num;
            }
        }
        if (this.drag || this.sizeDrag) {
            this.sizeDragBorder.start(new Color(255, 255, 255, 100), new Color(255, 255, 255), 0.15f, Type.EASE_IN_OUT_QUAD);
        } else {
            this.sizeDragBorder.start(this.sizeDragBorder.getColor(), new Color(255, 255, 255, 0), 0.2f, Type.EASE_IN_OUT_QUAD);
        }
        this.sizeDragBorder.update();
        if (Interface.isHovered((float) x + width - 112.0f, (float) y + height - 10.0f, 12.0f, 12.0f, mouseX, mouseY)) {
            RoundedUtil.drawRound((float) x + width - 112, (float) y + height - 10.0f, 5.0f, 5.0f, 6,
                    HUD.mainColor.getColorC());
        } else {
            RoundedUtil.drawRound((float) x + width - 112.0f, (float) y + height - 10.0f, 5.0f, 5.0f, 6,
                    HUD.mainColor.getColorC());
        }


    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ScaledResolution sr = new ScaledResolution(this.mc);
        if ((float) sr.getScaledWidth() < width) {
            x = 5;
            width = sr.getScaledWidth() - 10;
        }
        if ((float) sr.getScaledHeight() < height) {
            y = 5;
            height = sr.getScaledHeight() - 10;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.scaleAnimation.value = 0.6;
        this.close = false;
        if (width == 0.0f || height == 0.0f) {
            width = (float) sr.getScaledWidth() / 2.0f;
            height = (float) sr.getScaledHeight() / 2.0f;
        }
        if (x == -1 || y == -1) {
            x = (int) (((float) sr.getScaledWidth() - width) / 2.0f);
            y = (int) (((float) sr.getScaledHeight() - height) / 2.0f);
        }
        this.selection = y + 70;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (this.scaleAnimation.end != 0.1) {
                this.close = true;
                this.scaleAnimation.fstart(this.scaleAnimation.getValue(), 0.1, 0.2f, Type.EASE_IN_BACK);
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && Interface.isHovered(x, y, 110.0f, 34.0f, mouseX, mouseY)) {
            this.drag = true;
            this.dragX = mouseX - x;
            this.dragY = mouseY - y;
        }
        if (mouseButton == 0 && Interface.isHovered((float) x + width - 114.0f, (float) y + height - 20.0f, 20.0f, 20.0f, mouseX, mouseY)) {
            ScaledResolution sr = new ScaledResolution(this.mc);
            this.sizeDrag = true;
            this.sizeDragX = (float) x + width - (float) Math.min(mouseX, sr.getScaledWidth());
            this.sizeDragY = (float) y + height - (float) Math.min(mouseY, sr.getScaledHeight());
        }
    }
}

