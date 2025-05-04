/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package dev.tevarin.utils;


import java.util.concurrent.CopyOnWriteArrayList;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.PacketSendEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class PacketBlinkHandler
        implements IMinecraft {
    private boolean blinking;
    private boolean clearedPackets;
    private final CopyOnWriteArrayList<Packet> packetsQueue = new CopyOnWriteArrayList();

    public PacketBlinkHandler() {
        Client.instance.getEventManager().register(this);
    }

    @EventTarget
    public void onSend(PacketSendEvent event) {
        if (mc.isSingleplayer()) {
            return;
        }
        if (PacketBlinkHandler.mc.thePlayer == null || PacketBlinkHandler.mc.thePlayer.ticksExisted < 5) {
            if (!this.clearedPackets) {
                this.packetsQueue.clear();
                this.stopBlinking();
                this.clearedPackets = true;
            }
        } else {
            this.clearedPackets = false;
        }
        if (!event.isCancelled() && this.blinking) {
            event.setCancelled(true);
            this.packetsQueue.add((Packet)event.getPacket());
        }
    }

    public void startBlinking() {
        this.blinking = true;
    }

    public void stopBlinking() {
        this.blinking = false;
        this.releasePackets();
    }

    public void releasePackets() {
        if (!this.packetsQueue.isEmpty()) {
            for (Packet p : this.packetsQueue) {
                mc.getNetHandler().getNetworkManager().sendPacketFinal(p);
            }
            this.packetsQueue.clear();
        }
    }

    public void releaseWithPingPacketsFirst() {
        if (!this.packetsQueue.isEmpty()) {
            for (Packet p : this.packetsQueue) {
                if (!(p instanceof C0FPacketConfirmTransaction) && !(p instanceof C00PacketKeepAlive)) continue;
                mc.getNetHandler().getNetworkManager().sendPacketFinal(p);
            }
            for (Packet p : this.packetsQueue) {
                if (p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive) continue;
                mc.getNetHandler().getNetworkManager().sendPacketFinal(p);
            }
            this.packetsQueue.clear();
        }
    }

    public void releaseWithPingPacketsLast() {
        if (!this.packetsQueue.isEmpty()) {
            for (Packet p : this.packetsQueue) {
                if (p instanceof C0FPacketConfirmTransaction || p instanceof C00PacketKeepAlive) continue;
                mc.getNetHandler().getNetworkManager().sendPacketFinal(p);
            }
            for (Packet p : this.packetsQueue) {
                if (!(p instanceof C0FPacketConfirmTransaction) && !(p instanceof C00PacketKeepAlive)) continue;
                mc.getNetHandler().getNetworkManager().sendPacketFinal(p);
            }
            this.packetsQueue.clear();
        }

    }

    public void clearPackets() {
        this.packetsQueue.clear();
    }

    public boolean isBlinking() {
        return this.blinking;
    }
}

