package dev.tevarin.module.impl.misc;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.*;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.DebugUtil;
import dev.tevarin.utils.HelperUtil;
import dev.tevarin.utils.StopWatch;
import dev.tevarin.utils.math.Heypixel;
import dev.tevarin.value.impl.ModeValue;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.netease2.HYTProvider;

import java.nio.charset.StandardCharsets;


public class Protocol extends Module {
    public static final ModeValue mode = new ModeValue("Mode", new String[]{"HYT", "HYT2", "Heypixel"}, "HYT");
    private boolean isFirst = true;
    private final StopWatch stopWatch = new StopWatch();

    public Protocol() {
        super("Protocol", Category.Misc);
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        this.setSuffix(mode.getValue());
    }

    @EventTarget
    public void onPacketEvent(EventHigherPacketSend event) {
        Packet<?> packet = event.getPacket();
        if (mode.is("Heypixel")) {
            if (packet instanceof C15PacketClientSettings) {
                event.setCancelled(true);
                Heypixel.sendMinecraftRegister();
                Heypixel.sendForgeBrand();
                Heypixel.sendJoinHeypixelCheck();
                isFirst = true;
                stopWatch.reset();
            }
            if (packet instanceof C17PacketCustomPayload customPayload) {
                String channelName = customPayload.getChannelName();
                System.out.println("Channel: " + channelName);
                DebugUtil.print("Channel: " + channelName);

                PacketBuffer data = customPayload.getBufferData();
                if (data != null && data.readableBytes() > 0) {
                    byte[] payloadBytes = new byte[data.readableBytes()];
                    data.readBytes(payloadBytes);
                    String payloadContent = new String(payloadBytes, StandardCharsets.UTF_8);
                    System.out.println("Payload content: " + payloadContent);
                    DebugUtil.print("Payload content: " + payloadContent);
                }
            }
        }
    }

    @EventTarget
    public void onPacketCustom(EventPacketCustom event) {
            HYTProvider.onPacket(event);
    }

    @EventTarget
    public void onSendChatMessage(EventSendChatMessage e) {
        if (e.getMsg().contains("/kh")) {
            HelperUtil.sendMessage("打开组队页面");
            HYTProvider.sendOpenParty();
            e.setCancelled();
        }
    }

    @EventTarget
    public void onTick(EventUpdate event) {
        try {
            if (isFirst && isNull()) {
                Heypixel.sendJoinHeypixelCheck();
                stopWatch.reset();
            } else if (stopWatch.finished(5000)) {
                Heypixel.sendKeepAlive();
                stopWatch.reset();
            }
        } catch (Exception ex) {

        }
    }

}


//    @Listener
//    public void onPacketSend(PacketSendEvent event) {
//        Packet<?> packet = event.getPacket();
//        if (mode.is("Heypixel")) {
//            if (packet instanceof S01PacketJoinGame) {
//                event.setCancelled();
//            }
//            if (packet instanceof S3FPacketCustomPayload) {
//                if (((S3FPacketCustomPayload) packet).getChannelName().equals("MC|Brand")) {
//                    event.setCancelled();
//                }
//            }
//            if (packet instanceof C17PacketCustomPayload) {
//                if (((C17PacketCustomPayload) packet).getChannelName().equals("MC|Brand")) {
//                    event.setCancelled();
//                }
//            }
//        }
//    }

