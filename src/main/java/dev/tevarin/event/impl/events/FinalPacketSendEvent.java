/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package dev.tevarin.event.impl.events;


import dev.tevarin.event.impl.CancellableEvent;
import net.minecraft.network.Packet;

public class FinalPacketSendEvent
        extends CancellableEvent {
    private Packet packet;

    public <T extends Packet> T getPacket() {
        return (T)this.packet;
    }

    public FinalPacketSendEvent(Packet packet) {
        this.packet = packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

