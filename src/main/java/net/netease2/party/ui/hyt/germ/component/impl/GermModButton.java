/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party.ui.hyt.germ.component.impl;

import dev.tevarin.ui.font.FontManager;
import net.netease2.party.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.awt.*;

public class GermModButton
        implements GermComponent {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final String path;
    protected final String text;
    protected boolean hovered;

    public GermModButton(String path, String text) {
        this.path = path;
        this.text = text;
        this.hovered = false;
    }

    public static boolean isHovered(float x, float y, float x1, float y1, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x1 && (float) mouseY >= y && (float) mouseY <= y1;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        if (GermModButton.isHovered(x - 50, y - 10, x + 50, y + 10, mouseX, mouseY)) {
            if (!this.hovered) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(parentUuid).writeString(this.path).writeInt(2))));
                this.hovered = true;
            }
        } else if (this.hovered) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(parentUuid).writeString(this.path).writeInt(3))));
            this.hovered = false;
        }
        int startX = x - 50;
        int endX = x + 50;
        int startY = y - 10;
        int endY = y + 10;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, (float) startX + 0.5f, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, (float) endY - 0.5f, (float) endX - 0.5f, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) endX - 0.5f, startY, endX, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, startY, (float) endX - 0.5f, (float) startY + 0.5f, new Color(44, 44, 44, 102).getRGB());
        FontManager.font18.drawCenteredString(this.text.replace("\u00a78", ""), x, startY + 6, new Color(216, 216, 216).getRGB());
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getSeparation() {
        return 20;
    }

    @Override
    public void mouseClicked(String parentUuid) {
        if (this.hovered) {
            this.beforeClick();
            this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(parentUuid).writeString(this.path).writeInt(0))));
            this.whenClick();
            if (this.doesCloseOnClickButton()) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11)).writeString(parentUuid)));
                this.mc.displayGuiScreen(null);
            }
        }
    }

    protected void beforeClick() {
    }

    protected void whenClick() {
    }

    protected boolean doesCloseOnClickButton() {
        return true;
    }
}

