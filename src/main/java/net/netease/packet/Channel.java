package net.netease.packet;


import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import static dev.tevarin.Client.mc;

@Getter
public class Channel {
    private final String name;

    public void sendToServer(String name, PacketBuffer buffer) {
        mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload(name, buffer));
    }

    public Channel(String name) {
        this.name = name;
    }

}
