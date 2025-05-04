package dev.tevarin.utils;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.WorldEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * @author TG_format
 * @since 2024/8/14 下午7:44
 */
public class BadPacketUComponent implements IMinecraft {
    private static boolean c03Check = false;
    private static boolean shouldFix = false;
    public static boolean onPacket(Packet<?> packet) {
        if (packet instanceof C0BPacketEntityAction c0b) {
            if (c0b.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                c03Check = true;
            }
            if (c0b.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING) && c03Check) {
                shouldFix = true;
                return true;
            }
        }
        if (packet instanceof C03PacketPlayer) {
            if (shouldFix) {
                shouldFix = false;
                mc.thePlayer.serverSprintState = true;
            }
            c03Check = false;
        }
        return false;
    }
    @EventTarget
    public void onWorld(WorldEvent event) {
        c03Check = false;
        shouldFix = false;
    }
}
