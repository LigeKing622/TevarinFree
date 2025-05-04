/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party.ui.hyt.germ.component.impl;

import net.netease2.party.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.awt.*;

public class GermPartyRequest
        implements GermComponent {
    private final String playerName;
    private final GermModIconButton accept;
    private final GermModIconButton deny;

    public GermPartyRequest(final String playerName) {
        this.playerName = playerName;
        this.accept = new GermModIconButton("btok", "tevarin/hyt/page/accept.png", 12, 12) {

            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_request_list@bt_accept").writeString("{\"player_name\":\"" + playerName + "\"}")));
            }
        };
        this.deny = new GermModIconButton("btno", "tevarin/hyt/page/deny.png", 12, 12) {

            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_request_list@bt_deny").writeString("{\"player_name\":\"" + playerName + "\"}")));
            }
        };
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        int startX = x - 90;
        int endX = x + 90;
        int startY = y - 10;
        int endY = y + 10;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, (float) startX + 0.5f, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, (float) endY - 0.5f, (float) endX - 0.5f, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect((float) endX - 0.5f, startY, endX, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect((float) startX + 0.5f, startY, (float) endX - 0.5f, (float) startY + 0.5f, new Color(216, 216, 216, 102).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(this.playerName, x - 80, y - 4, new Color(216, 216, 216).getRGB());
        this.accept.drawComponent(parentUuid, x + 40, y, mouseX, mouseY);
        this.deny.drawComponent(parentUuid, x + 68, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(String parentUuid) {
        this.accept.mouseClicked(parentUuid);
        this.deny.mouseClicked(parentUuid);
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getSeparation() {
        return 12;
    }
}

