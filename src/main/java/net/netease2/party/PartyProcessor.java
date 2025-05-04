/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party;

import dev.tevarin.Client;
import dev.tevarin.utils.HelperUtil;
import net.netease2.party.ui.hyt.germ.GuiComponentPage;
import net.netease2.party.ui.hyt.germ.GuiGermScreen;
import net.netease2.party.ui.hyt.germ.component.GermComponent;
import net.netease2.party.ui.hyt.germ.component.impl.GermModButton;
import net.netease2.party.ui.hyt.germ.component.impl.GermPartyKick;
import net.netease2.party.ui.hyt.germ.component.impl.GermPartyRequest;
import net.netease2.party.ui.hyt.germ.component.impl.GermText;
import net.netease2.party.ui.hyt.party.GameButton;
import net.netease2.party.ui.hyt.party.GuiInput;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class PartyProcessor {
    private static String lastScreen;
    private static String prevGuiUuid;

    private static String getText(String key) {
        switch (key) {
            case "subject_bedwar": {
                return "\u8d77\u5e8a\u6218\u4e89";
            }
            case "subject_skywar": {
                return "\u7a7a\u5c9b\u6218\u4e89";
            }
            case "subject_leisure": {
                return "\u4f11\u95f2\u6e38\u620f";
            }
            case "subject_fight": {
                return "\u7ade\u6280\u6e38\u620f";
            }
            case "subject_survive": {
                return "\u751f\u5b58";
            }
            case "subject_fight_team": {
                return "\u6218\u4e89";
            }
        }
        return "";
    }

    private static ArrayList<GermComponent> openGui(Set<String> keys, String uuid) {
        ArrayList<GermComponent> buttons = new ArrayList<GermComponent>();
        for (String k1 : keys) {
            String buttonText = PartyProcessor.getText(k1);
            if (buttonText.isEmpty()) continue;
            buttons.add(new GameButton("\u81ea\u9002\u5e94\u80cc\u666f$\u4e3b\u5206\u7c7b$" + k1, buttonText, k1));
        }
        Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(uuid).writeString(uuid).writeString(uuid)));
        return buttons;
    }

    public static void process(PacketBuffer packetBuffer1, int id) {
        String identity = packetBuffer1.readStringFromBuffer(Short.MAX_VALUE);
        if (identity.equalsIgnoreCase("gui")) {
            String guiUuid = packetBuffer1.readStringFromBuffer(Short.MAX_VALUE);
            Yaml yaml = new Yaml();
            String yml = packetBuffer1.readStringFromBuffer(9999999);
            Map objectMap = (Map) yaml.load(yml);
            if (objectMap == null) {
                return;
            }
            lastScreen = guiUuid;
            if ((objectMap = (Map) objectMap.get(guiUuid)) == null) {
                return;
            }
            if (guiUuid.equalsIgnoreCase("mainmenu")) {
                for (Object keyObj : objectMap.keySet()) {
                    String key = (String) keyObj;
                    ArrayList<GermComponent> buttons;
                    Map context;
                    if (yml.contains("\u53ea\u6709\u961f\u957f\u624d\u53ef\u4ee5\u52a0\u5165\u6e38\u620f\u54e6")) {
                        ArrayList<GermComponent> components = new ArrayList<GermComponent>();
                        components.add(new GermText("\u53ea\u6709\u961f\u957f\u624d\u53ef\u4ee5\u52a0\u5165\u6e38\u620f\u634f"));
                        components.add(new GermModButton("\u81ea\u9002\u5e94\u80cc\u666f$\u786e\u8ba4", "\u786e\u8ba4"));
                        Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                        Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, components));
                        continue;
                    }
                    if (key.equalsIgnoreCase("options") || key.endsWith("_bg") || !(context = (Map) objectMap.get(key)).containsKey("relativeParts") || !(context = (Map) context.get("relativeParts")).containsKey("\u4e3b\u5206\u7c7b") || !(context = (Map) context.get("\u4e3b\u5206\u7c7b")).containsKey("relativeParts") || (buttons = PartyProcessor.openGui((context = (Map) context.get("relativeParts")).keySet(), guiUuid)).isEmpty())
                        continue;
                    Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons));
                }
                prevGuiUuid = guiUuid;
            } else if (guiUuid.startsWith("team_") && Client.mc.thePlayer.ticksExisted > 6) {
                if (guiUuid.equals("team_create")) {
                    ArrayList<GermComponent> buttons = new ArrayList<GermComponent>();
                    buttons.add(new GermModButton("create", "\u521b\u5efa\u961f\u4f0d") {

                        @Override
                        protected void whenClick() {
                            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_create@create").writeString("{\"null\":null}")));
                        }
                    });
                    buttons.add(new GermModButton("join", "\u52a0\u5165\u961f\u4f0d") {

                        @Override
                        protected void whenClick() {
                            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_create@join").writeString("{\"null\":null}")));
                        }
                    });
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                    Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons).title("\u82b1\u96e8\u5ead\u7ec4\u961f\u7cfb\u7edf"));
                } else if (guiUuid.equals("team_list")) {
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(guiUuid).writeString("input").writeInt(2))));
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(guiUuid).writeString("input").writeInt(0))));
                    Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_list@input").writeString("{\"null\":null}")));
                } else if (guiUuid.equals("team_input")) {
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(Client.mc.currentScreen == null ? prevGuiUuid : ((GuiGermScreen) Client.mc.currentScreen).getUUID()).writeString(prevGuiUuid).writeString(guiUuid)));
                    Client.mc.displayGuiScreen(new GuiInput((GuiGermScreen) Client.mc.currentScreen, guiUuid, prevGuiUuid));
                } else if (guiUuid.equals("team_main")) {
                    Map context = (Map) objectMap.get("buttons");
                    context = (Map) context.get("relativeParts");
                    ArrayList<GermComponent> buttons = new ArrayList<GermComponent>();
                    for (Object key : context.keySet()) {
                        Map buttonMap = (Map) context.get(key);
                        String postRequest = buttonMap.get("clickScript").toString().trim();
                        postRequest = postRequest.substring(16, postRequest.length() - 3);
                        String postAction = postRequest.split(",\\{")[0];
                        final String finalPostAction = postAction = postAction.substring(0, postAction.length() - 1);
                        buttons.add(new GermModButton("buttons$" + key, (String) ((ArrayList) buttonMap.get("texts")).get(0)) {

                            @Override
                            protected void whenClick() {
                                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_main@" + finalPostAction).writeString("{\"null\":null}")));
                            }

                            @Override
                            protected boolean doesCloseOnClickButton() {
                                return false;
                            }
                        });
                    }
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                    Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons).title((String) ((ArrayList) ((Map) objectMap.get("title")).get("texts")).get(0)));
                } else if (guiUuid.equals("team_request_list")) {
                    objectMap = (Map) objectMap.get("scroll");
                    if ((objectMap = (Map) objectMap.get("scrollableParts")) == null) {
                        HelperUtil.sendMessage("\u00a7a\u00a7l\u6ca1\u6709\u4eba\u7533\u8bf7\u4f60\u7684\u7ec4\u961f");
                    } else {
                        ArrayList<GermComponent> components = new ArrayList<GermComponent>();
                        for (Object keyObj : objectMap.keySet()) {
                            String keyEntry = (String) keyObj;
                            Map childEntry = (Map) objectMap.get(keyEntry);
                            childEntry = (Map) childEntry.get("relativeParts");
                            String textName = (String) ((ArrayList) ((Map) childEntry.get("name")).get("texts")).get(0);
                            Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                            components.add(new GermPartyRequest(keyEntry.substring(6)));
                        }
                        Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, components).title("\u7533\u8bf7\u5217\u8868"));
                    }
                } else if (guiUuid.equals("team_invite_list")) {
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(guiUuid).writeString("input").writeInt(2))));
                    Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(guiUuid).writeString("input").writeInt(0))));
                    Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$team_invite_list@input").writeString("{\"null\":null}")));
                } else if (guiUuid.equals("team_kick_list")) {
                    objectMap = (Map) objectMap.get("scroll");
                    if ((objectMap = (Map) objectMap.get("scrollableParts")) == null) {
                        HelperUtil.sendMessage("\u00a7a\u00a7l\u4f60\u7684\u961f\u4f0d\u53ea\u5269\u4f60\u81ea\u5df1\u4e86");
                    } else {
                        ArrayList<GermComponent> components = new ArrayList<GermComponent>();
                        for (Object keyObj : objectMap.keySet()) {
                            String keyEntry = (String) keyObj;
                            Map childEntry = (Map) objectMap.get(keyEntry);
                            childEntry = (Map) childEntry.get("relativeParts");
                            String textName = (String) ((ArrayList) ((Map) childEntry.get("name")).get("texts")).get(0);
                            Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
                            components.add(new GermPartyKick(keyEntry.substring(6)));
                        }
                        Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, components).title("\u8e22\u51fa\u961f\u5458"));
                    }
                }
                prevGuiUuid = guiUuid;
            } else {
                for (Object keyObj : objectMap.keySet()) {
                    String key = (String) keyObj;
                    if (key.equalsIgnoreCase("options") || key.endsWith("_bg")) continue;
                    Map context = (Map) objectMap.get(key);
                    PartyProcessor.openOldGui(context, key, guiUuid);
                }
                prevGuiUuid = guiUuid;
            }
        }
    }

    private static void openOldGui(Map<String, Object> context, String key, String guiUuid) {
        ArrayList<GermComponent> buttons = new ArrayList<GermComponent>();
        for (String k : context.keySet()) {
            if (!k.equalsIgnoreCase("scrollableParts")) continue;
            context = (Map) context.get("scrollableParts");
            block1:
            for (String uuid : context.keySet()) {
                Map scrollableSubMap = (Map) context.get(uuid);
                if (!scrollableSubMap.containsKey("relativeParts")) continue;
                scrollableSubMap = (Map) scrollableSubMap.get("relativeParts");
                for (Object k1 : scrollableSubMap.keySet()) {
                    if ((scrollableSubMap = (Map) scrollableSubMap.get(k1)) == null) {
                        return;
                    }
                    if (!scrollableSubMap.containsKey("texts")) continue;
                    String buttonText = (String) ((ArrayList) scrollableSubMap.get("texts")).get(0);
                    buttons.add(new GermModButton(key + "$" + uuid + "$" + k1, buttonText));
                    continue block1;
                }
            }
        }
        if (!buttons.isEmpty()) {
            Client.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(4).writeInt(0).writeInt(0)).writeString(guiUuid).writeString(guiUuid).writeString(guiUuid)));
            Client.mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons));
        }
    }
}

