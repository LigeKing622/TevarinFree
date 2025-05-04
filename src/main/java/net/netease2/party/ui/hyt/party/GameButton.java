/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party.ui.hyt.party;

import net.netease2.party.ui.hyt.germ.component.impl.GermModButton;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class GameButton
        extends GermModButton {
    private final String key;

    public GameButton(String path, String text, String key) {
        super(path, text);
        this.key = key;
    }

    @Override
    protected void whenClick() {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$mainmenu@subject/" + this.key.substring(8)).writeString("{\"click\":\"1\"}")));
    }

    @Override
    protected boolean doesCloseOnClickButton() {
        return false;
    }
}

