package dev.tevarin.utils;


import dev.tevarin.module.impl.combat.Gapple;
import dev.tevarin.module.impl.player.Blink;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.concurrent.LinkedBlockingQueue;

@UtilityClass
public class BlinkUtils implements IMinecraft {
    @Getter
    private boolean blinking = false;
    @Setter
    private boolean cantSlowRelease = false;
    public static LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    public boolean isBlinkPacket(Packet<?> packet) {
        return packet instanceof C03PacketPlayer ||
                packet instanceof C0FPacketConfirmTransaction ||
                packet instanceof C0APacketAnimation ||
                packet instanceof C08PacketPlayerBlockPlacement ||
                packet instanceof C02PacketUseEntity ||
                packet instanceof C09PacketHeldItemChange ||
                packet instanceof C0EPacketClickWindow ||
                packet instanceof C0DPacketCloseWindow ||
                packet instanceof C07PacketPlayerDigging ||
                packet instanceof C0BPacketEntityAction;
    }
    public void startBlink() {
        packets.clear();
        blinking = true;
    }
    public void stopBlink() {
        blinking = false;
        releaseAll();
    }
    public boolean isPacketShouldDelay(Packet<?> packet) {
        if (!blinking) return false;
        if (isBlinkPacket(packet)) {
            packets.add(packet);
            if (packet instanceof C03PacketPlayer) {
                Gapple.s++;
            }
            return true;
        }
        return false;
    }
    public void releasePacketByAmount(int amount) {
        if (cantSlowRelease) {
            return;
        }
        for (int i = 0; i < amount; i++) {
            Packet<?> packet = packets.poll();
            PacketUtil.sendPacketNoEvent(packet);
            if (packet instanceof C03PacketPlayer c03) {
                Gapple.s--;
                if (Blink.getFakePlayer() != null) {
                    Blink.getFakePlayer().serverPosX = (int) c03.getX();
                    Blink.getFakePlayer().serverPosY = (int) c03.getY();
                    Blink.getFakePlayer().serverPosZ = (int) c03.getZ();
                    double d0 = Blink.getFakePlayer().serverPosX;
                    double d1 = Blink.getFakePlayer().serverPosY;
                    double d2 = Blink.getFakePlayer().serverPosZ;
                    float f = c03.rotating ? c03.getYaw() : Blink.getFakePlayer().rotationYaw;
                    float f1 = c03.rotating ? c03.getPitch() : Blink.getFakePlayer().rotationPitch;
                    Blink.getFakePlayer().setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
                    Blink.getFakePlayer().onGround = c03.isOnGround();
                }
            }
        }
    }
    public void releaseC03(int amount) {
        if (cantSlowRelease) {
            return;
        }
        int i = 0;
        for (int j = 0; j < packets.size(); j++) {
            Packet<?> packet = packets.poll();
            PacketUtil.sendPacketNoEvent(packet);
            if (packet instanceof C03PacketPlayer) {
                Gapple.s--;
                i++;
            }
            if (i >= amount) {
                break;
            }
        }
    }
    public void releaseC03render(int amount) {
        if (cantSlowRelease) {
            return;
        }
        int i = 0;
        for (int j = 0; j < packets.size(); j++) {
            Packet<?> packet = packets.poll();
            PacketUtil.sendPacketNoEvent(packet);
            if (packet instanceof C03PacketPlayer c03) {
                Gapple.s--;
                i++;
                Gapple.s--;
                if (Blink.getFakePlayer() != null) {
                    Blink.getFakePlayer().serverPosX = (int) c03.getX();
                    Blink.getFakePlayer().serverPosY = (int) c03.getY();
                    Blink.getFakePlayer().serverPosZ = (int) c03.getZ();
                    double d0 = Blink.getFakePlayer().serverPosX;
                    double d1 = Blink.getFakePlayer().serverPosY;
                    double d2 = Blink.getFakePlayer().serverPosZ;
                    float f = c03.rotating ? c03.getYaw() : Blink.getFakePlayer().rotationYaw;
                    float f1 = c03.rotating ? c03.getPitch() : Blink.getFakePlayer().rotationPitch;
                    Blink.getFakePlayer().setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
                    Blink.getFakePlayer().onGround = c03.isOnGround();
                }
            }
            if (i >= amount) {
                break;
            }
        }
    }
    private void releaseAll() {
        for (Packet<?> packet : packets) {
            PacketUtil.sendPacketNoEvent(packet);
        }
        packets.clear();
    }
}
