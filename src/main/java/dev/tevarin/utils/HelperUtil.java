package dev.tevarin.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class HelperUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String message) {
        new ChatUtil.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
    }

    public static void sendWarmingMessage(String message) {
        new ChatUtil.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.RED).build().displayClientSided();
    }

    public static void sendMessageWithoutPrefix(String message) {
        new ChatUtil.ChatMessageBuilder(false, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
    }

    public static boolean onServer(String server) {
        return !mc.isSingleplayer() && HelperUtil.mc.getCurrentServerData().serverIP.toLowerCase().contains(server);
    }
}

