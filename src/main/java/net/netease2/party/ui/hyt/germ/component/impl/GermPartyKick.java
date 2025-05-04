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
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.awt.*;

public class GermPartyKick
        implements GermComponent {
    private final String playerName;
    private final GermModIconButton kick;

    public GermPartyKick(final String playerName) {
        this.playerName = playerName;
        this.kick = new GermModIconButton("bt", "tevarin/hyt/page/kick.png", 12, 12) {

            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_request_list@bt_kick").writeString("{\"player_name\":\"" + playerName + "\"}")));
            }
        };
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        FontManager.font18.drawString(this.playerName, (float) (x - 80), y - 4, new Color(216, 216, 216).getRGB());
        this.kick.drawComponent(parentUuid, x + 68, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(String parentUuid) {
        this.kick.mouseClicked(parentUuid);
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

