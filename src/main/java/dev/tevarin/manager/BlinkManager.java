package dev.tevarin.manager;

import lombok.Getter;
import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventPriority;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventPacket;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.utils.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class BlinkManager {

    public final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public boolean blinking, dispatch;
    public ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public TimerUtil exemptionWatch = new TimerUtil();

    public void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventTarget
    @EventPriority(8888)
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S01PacketJoinGame) {
            packets.clear();
            blinking = false;
        }
        if (event.getEventType() == EventPacket.EventState.SEND) {

            if (Client.mc.thePlayer == null) {
                packets.clear();
                exemptedPackets.clear();
                return;
            }

            if (Client.mc.thePlayer.isDead || Client.mc.isSingleplayer() || !Client.mc.getNetHandler().doneLoadingTerrain) {
                packets.forEach(PacketUtil::sendPacketNoEvent);
                packets.clear();
                blinking = false;
                exemptedPackets.clear();
                return;
            }

            final Packet<?> packet = event.getPacket();

            if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                    packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                    packet instanceof C01PacketEncryptionResponse) {
                return;
            }

            if (blinking && !dispatch) {
                if (exemptionWatch.delay(100)) {
                    exemptionWatch.reset();
                    exemptedPackets.clear();
                }

                if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                        packetClass == packet.getClass())) {
                    packets.add(packet);
                    event.setCancelled(true);
                }
            } else if (packet instanceof C03PacketPlayer) {
                packets.forEach(PacketUtil::sendPacketNoEvent);
                packets.clear();
                dispatch = false;
            }
        }
    }

    ;

    public void dispatch() {
        dispatch = true;
    }

    @EventTarget
    @EventPriority(8888)
    public void onWorld(EventWorldLoad e) {
        packets.clear();
        blinking = false;
    }

    ;


}