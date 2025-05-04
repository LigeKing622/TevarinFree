/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 */
package net.netease2.game;

import com.google.common.base.Charsets;
import net.netease2.CustomPacket;
import net.netease2.HYTProvider;
import net.netease2.party.PartyProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class GermModProcessor
        implements CustomPacket {
    public static Map<String, List<GermMenuItem>> hytMainMenuItems = new HashMap<String, List<GermMenuItem>>();
    GermData data = new GermData();
    private static final byte[] joinGame1 = new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47};
    private static final byte[] openGUI = new byte[]{0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117};

    @Override
    public String getChannel() {
        return "germplugin-netease";
    }

    @Override
    public void process(ByteBuf byteBuf) {
        PacketBuffer packetBuffer1 = new PacketBuffer(byteBuf);
        int id = packetBuffer1.readInt();
        switch (id) {
            case -1: {
                this.data.deserializeGermData(byteBuf.copy());
                break;
            }
            case 73: {
                PartyProcessor.process(packetBuffer1, id);
                break;
            }
            case 76: {
                String string = byteBuf.toString(Charsets.UTF_8);
                if (!string.contains("mainmenu")) break;
                HYTProvider.openUI();
                break;
            }
            case 67: {
                break;
            }
            case 72: {
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                byte[] data = new byte[]{50, -72, 2, 86, 84, 74, 73, 82, 106, 90, 88, 86, 70, 74, 79, 77, 108, 99, 121, 87, 108, 90, 89, 81, 107, 112, 72, 86, 108, 89, 49, 85, 69, 104, 67, 84, 85, 112, 75, 85, 48, 111, 51, 85, 48, 86, 80, 86, 49, 86, 76, 87, 68, 99, 50, 87, 68, 90, 87, 85, 69, 120, 66, 84, 48, 104, 90, 83, 108, 65, 122, 82, 107, 82, 70, 84, 86, 77, 48, 78, 106, 86, 90, 82, 69, 100, 68, 83, 70, 65, 50, 81, 106, 90, 87, 82, 49, 82, 76, 83, 48, 49, 68, 77, 106, 78, 70, 83, 49, 70, 86, 83, 107, 48, 51, 82, 84, 78, 90, 82, 48, 86, 66, 85, 108, 70, 66, 81, 122, 82, 82, 86, 69, 56, 122, 84, 86, 69, 51, 81, 122, 90, 83, 78, 69, 70, 77, 78, 86, 69, 50, 78, 68, 82, 76, 82, 70, 85, 122, 87, 106, 85, 49, 86, 106, 82, 77, 84, 69, 82, 84, 84, 108, 86, 84, 85, 106, 90, 89, 77, 48, 57, 77, 87, 107, 86, 88, 83, 84, 86, 66, 81, 85, 90, 67, 86, 70, 78, 68, 77, 122, 86, 84, 83, 48, 90, 77, 87, 86, 104, 72, 84, 106, 90, 73, 87, 68, 100, 83, 78, 69, 86, 72, 78, 106, 100, 89, 86, 48, 100, 66, 85, 122, 86, 72, 78, 85, 104, 87, 87, 70, 66, 82, 81, 107, 104, 84, 83, 69, 57, 73, 86, 108, 112, 71, 84, 48, 112, 86, 83, 108, 107, 122, 77, 48, 100, 71, 84, 85, 69, 49, 87, 86, 100, 84, 83, 108, 108, 67, 83, 70, 78, 73, 84, 48, 104, 87, 87, 107, 90, 80, 83, 108, 86, 75, 87, 84, 77, 122, 82, 48, 90, 78, 81, 84, 86, 90, 86, 49, 78, 75, 87, 81, 61, 61};
                buffer.writeInt(16);
                buffer.writeString("3.4.2");
                buffer.writeString(Base64.getEncoder().encodeToString("sb123".getBytes(StandardCharsets.UTF_8)));
                GermModProcessor.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", buffer));
                System.out.println("send hwid");
            }
        }
    }

    public static byte[] buildJoinGamePacket(int entry, String sid) {
        sid = "{\"entry\":" + entry + ",\"sid\":\"" + (String) sid + "\"}";
        byte[] bytes = new byte[joinGame1.length + ((String) sid).getBytes().length + 2];
        System.arraycopy(joinGame1, 0, bytes, 0, joinGame1.length);
        bytes[GermModProcessor.joinGame1.length] = (byte) (48 + entry);
        bytes[GermModProcessor.joinGame1.length + 1] = (byte) ((String) sid).length();
        System.arraycopy(((String) sid).getBytes(), 0, bytes, joinGame1.length + 2, ((String) sid).getBytes().length);
        return bytes;
    }

    public static void sendJoin(int num, String sid) {
        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes(openGUI);
        C17PacketCustomPayload packetIn1 = new C17PacketCustomPayload("germmod-netease", new PacketBuffer(buf2));
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packetIn1);
        ByteBuf buf1 = Unpooled.buffer();
        byte[] bytes = GermModProcessor.buildJoinGamePacket(num, sid);
        buf1.writeBytes(bytes);
        C17PacketCustomPayload packetIn = new C17PacketCustomPayload("germmod-netease", new PacketBuffer(buf1));
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packetIn);
        ++num;
    }

    static {
        ArrayList<GermMenuItem> germMenuItemList1 = new ArrayList<GermMenuItem>();
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-dalu", "\u7ec3\u4e60\u573a"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-solo", "8\u961f\u5355\u4eba \u7edd\u6740\u6a21\u5f0f"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-double", "8\u961f\u53cc\u4eba \u7edd\u6740\u6a21\u5f0f"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bw-team", "4\u961f4\u4eba \u7edd\u6740\u6a21\u5f0f"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bwxp16new", "\u65e0\u9650\u706b\u529b16"));
        germMenuItemList1.add(new GermMenuItem("BEDWAR/bwxp-32", "\u65e0\u9650\u706b\u529b32"));
        hytMainMenuItems.put("\u8d77\u5e8a\u6218\u4e89", germMenuItemList1);
        ArrayList<GermMenuItem> germMenuItemList2 = new ArrayList<GermMenuItem>();
        germMenuItemList2.add(new GermMenuItem("SKYWAR/nskywar", "\u7a7a\u5c9b\u6218\u4e89 \u5355\u4eba"));
        germMenuItemList2.add(new GermMenuItem("SKYWAR/nskywar-double", "\u7a7a\u5c9b\u6218\u4e89 \u53cc\u4eba"));
        hytMainMenuItems.put("\u7a7a\u5c9b\u6218\u4e89", germMenuItemList2);
        ArrayList<GermMenuItem> germMenuItemList3 = new ArrayList<GermMenuItem>();
        germMenuItemList3.add(new GermMenuItem("FIGHT/bihusuo", "\u5e9f\u571f"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/pubg-kit", "\u5403\u9e21\u8352\u91ce"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/kb-game", "\u804c\u4e1a\u6218\u4e89"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/arenaPVP", "\u7ade\u6280\u573a\uff08\u7b49\u7ea7\u9650\u5236\uff09"));
        germMenuItemList3.add(new GermMenuItem("FIGHT/the-pit", "\u5929\u5751\u4e4b\u6218"));
        hytMainMenuItems.put("\u4e2a\u4eba\u7ade\u6280", germMenuItemList3);
        ArrayList<GermMenuItem> germMenuItemList4 = new ArrayList<GermMenuItem>();
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/csbwxp-32", "\u67aa\u68b0\u8d77\u5e8a"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/bwkitxp-32", "\u804c\u4e1a\u65e0\u9650\u706b\u529b\u8d77\u5e8a"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/anni", "\u6838\u5fc3\u6218\u4e89"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/battlewalls", "\u6218\u5899"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/skygiants", "\u5de8\u4eba\u6218\u4e89"));
        germMenuItemList4.add(new GermMenuItem("TEAM_FIGHT/pubg-solo", "\u5403\u9e21\u5355\u4eba"));
        hytMainMenuItems.put("\u56e2\u961f\u7ade\u6280", germMenuItemList4);
        ArrayList<GermMenuItem> germMenuItemList5 = new ArrayList<GermMenuItem>();
        germMenuItemList5.add(new GermMenuItem("SURVIVE/oneblock", "\u5355\u65b9\u5757"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zskyblock", "\u7a7a\u5c9b"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zjyfy", "\u76d1\u72f1\u98ce\u4e91"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/xianjing", "\u4ed9\u5883"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zuanshi", "\u94bb\u77f3\u5927\u9646"));
        germMenuItemList5.add(new GermMenuItem("SURVIVE/zlysc", "\u9f99\u57df\u751f\u5b58"));
        hytMainMenuItems.put("\u751f\u5b58\u6e38\u620f", germMenuItemList5);
        ArrayList<GermMenuItem> germMenuItemList6 = new ArrayList<GermMenuItem>();
        germMenuItemList6.add(new GermMenuItem("LEISURE/tower", "\u5b88\u536b\u6c34\u6676"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/mg-game", "\u5c0f\u6e38\u620f\u6d3e\u5bf9"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/sq-team", "\u62a2\u7f8a\u5927\u4f5c\u6218"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/stackgame", "\u53e0\u53e0\u4e50"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/hp-game", "\u70eb\u624b\u5c71\u828b"));
        germMenuItemList6.add(new GermMenuItem("LEISURE/ww-game", "\u72fc\u4eba\u6740"));
        hytMainMenuItems.put("\u4f11\u95f2\u6e38\u620f", germMenuItemList6);
    }
}

