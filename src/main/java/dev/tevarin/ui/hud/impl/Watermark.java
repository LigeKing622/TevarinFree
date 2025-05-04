package dev.tevarin.ui.hud.impl;

import dev.tevarin.Client;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.vialoadingbase.ViaLoadingBase;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static dev.tevarin.module.impl.render.HUD.markTextValue;
import static dev.tevarin.utils.render.RenderUtil.bg;


public class Watermark extends HUD {
    public BoolValue mode = new BoolValue("MCFont", true);

    public Watermark() {
        super(50, 20, "Watermark");
    }

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {
        String clientName = markTextValue.get();

        if (charIndex > clientName.length()) {
            if (clientName.isEmpty()) {
                charIndex = 0;
            } else {
                charIndex = clientName.length() - 1;
            }
        }

        if (clientName.isEmpty()) {
            return;
        }

        updateTick++;

        if (updateTick > 5) {
            if (charIndex > clientName.length() - 1) {
                backward = true;
            } else if (charIndex <= 0) {
                backward = false;
            }
            if (backward) {
                charIndex--;
            } else {
                charIndex++;
            }

            markStr = clientName.substring(0, charIndex);

            updateTick = 0;
        }
    }

    public double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }

    @Override
    public void predrawhud() {
    }

    int updateTick;
    int charIndex;
    boolean backward;
    String markStr;

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        RapeMasterFontManager font = FontManager.interSemiBold18;
        if (mode.get()) {
            mc.fontRendererObj.drawStringWithShadow("T", 4, 5, dev.tevarin.module.impl.render.HUD.color(1).getRGB());
            mc.fontRendererObj.drawStringWithShadow("evarin " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + ViaLoadingBase.getInstance().getTargetVersion().getName() + EnumChatFormatting.GRAY +
                    "] " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + Minecraft.getDebugFPS() + "FPS" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + hour + ":" + EnumChatFormatting.WHITE + minute + EnumChatFormatting.GRAY + "]", 4 + mc.fontRendererObj.getStringWidth("T"), 5, Color.WHITE.getRGB());
        } else {
            font.drawStringWithShadow("T", 4, 5, dev.tevarin.module.impl.render.HUD.color(1).getRGB());
            font.drawStringWithShadow("evarin " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + ViaLoadingBase.getInstance().getTargetVersion().getName() + EnumChatFormatting.GRAY +
                    "] " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + Minecraft.getDebugFPS() + "FPS" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + hour + ":" + EnumChatFormatting.WHITE + minute + EnumChatFormatting.GRAY + "]", 4 + font.getStringWidth("T"), 5, Color.WHITE.getRGB());
        }
    }
}