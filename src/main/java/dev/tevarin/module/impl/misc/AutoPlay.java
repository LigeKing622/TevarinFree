package dev.tevarin.module.impl.misc;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventPacket;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.ModuleManager;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.module.impl.player.ChestStealer;
import dev.tevarin.module.impl.player.InvCleaner;
import dev.tevarin.module.impl.world.PlayerTracker;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.ui.hud.notification.NotificationType;
import dev.tevarin.utils.HYTUtils;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.utils.math.MathUtils;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S02PacketChat;
import net.netease.GsonUtil;
import net.netease.PacketProcessor;
import net.netease.gui.GermGameGui;
import net.netease.packet.impl.Packet04;
import net.netease.packet.impl.Packet26;

import java.util.HashMap;
import java.util.regex.Pattern;

import static dev.tevarin.module.impl.world.PlayerTracker.flaggedEntity;

public class AutoPlay
        extends Module {
    public static final BoolValue swValue = new BoolValue("SkyWars", true);
    public static final BoolValue autoKit = new BoolValue("AutoKit", true);
    public static final BoolValue bwValue = new BoolValue("BedWars", true);
    public static final BoolValue toggleModule = new BoolValue("Toggle Module", true);
    public static final NumberValue delayValue = new NumberValue("Delay", 3.0, 1.0, 10.0, 0.1);
    public boolean display = false;
    private final TimerUtil timer = new TimerUtil();
    private boolean waiting = false;
    private boolean waiting2 = false;
    public static String name;
    public static boolean regen = false;
    public static boolean strength = false;
    public static boolean gapple = false;
    public static boolean godaxe = false;
    public static boolean kbball = false;
    private static final Pattern PATTERN_BEHAVIOR_EXCEPTION = Pattern.compile("\u73a9\u5bb6(.*?)\u5728\u672c\u5c40\u6e38\u620f\u4e2d\u884c\u4e3a\u5f02\u5e38");
    private static final Pattern PATTERN_WIN_MESSAGE = Pattern.compile("\u4f60\u5728\u5730\u56fe(.*?)\u4e2d\u8d62\u5f97\u4e86(.*?)");
    private static final String TEXT_LIKE_OPTIONS = "      \u559c\u6b22      \u4e00\u822c      \u4e0d\u559c\u6b22";
    private static final String TEXT_BEDWARS_GAME_END = "[\u8d77\u5e8a\u6218\u4e89] Game \u7ed3\u675f\uff01\u611f\u8c22\u60a8\u7684\u53c2\u4e0e\uff01";
    private static final String TEXT_COUNTDOWN = "\u5f00\u59cb\u5012\u8ba1\u65f6: 1 \u79d2";
    public int ban = 0;
    public int win = 0;

    public AutoPlay() {
        super("AutoPlay", Category.Misc);
    }

    @EventTarget
    public void onEventWorldLoad(EventWorldLoad event) {
        flaggedEntity.clear();
        strength = false;
        regen = false;
        godaxe = false;
        gapple = false;
        kbball = false;
    }

    @Override
    public void onEnable() {
        ban = 0;
        win = 0;

        super.onEnable();
    }

    @Override
    public void onDisable() {
        ban = 0;
        win = 0;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        ItemStack itemStack;
        if (event.isPost()) {
            return;
        }
        if (this.waiting && this.waiting2) {
            AutoPlay.mc.thePlayer.swingItem();
            HashMap<String, Integer> data = new HashMap<String, Integer>();
            data.put("click", 1);
            String json = GsonUtil.toJson(data);
            String message = new StringBuilder().insert(0, "GUI$").append("mainmenu").append("@").append("subject/skywar").toString();
            PacketProcessor.INSTANCE.sendPacket(new Packet04("mainmenu"));
            PacketProcessor.INSTANCE.sendPacket(new Packet26(message, json));
            HashMap<String, Object> data2 = new HashMap<String, Object>();
            data2.put("entry", GermGameGui.INSTANCE.getCurrentElement().getSubElements().get(0).getIndex());
            data2.put("sid", GermGameGui.INSTANCE.getCurrentElement().getSubElements().get(0).getSid());
            String json2 = GsonUtil.toJson(data2);
            String message2 = new StringBuilder().insert(0, "GUI$").append("mainmenu").append("@").append("entry/").append(0).toString();
            PacketProcessor.INSTANCE.sendPacket(new Packet04("mainmenu"));
            PacketProcessor.INSTANCE.sendPacket(new Packet26(message2, json2));
            this.waiting = false;
            this.waiting2 = false;
        }
        if ((itemStack = AutoPlay.mc.thePlayer.inventoryContainer.getSlot(44).getStack()) == null || itemStack.getDisplayName() == null) {
            return;
        }
        if (itemStack.getDisplayName().contains("\u6e38\u620f\u6307\u5357")) {
            this.waiting2 = true;
        }
        if (!itemStack.getDisplayName().contains("\u9000\u51fa\u89c2\u6218")) {
            return;
        }
        if (itemStack.getItem().equals(Items.iron_door) && this.swValue.getValue().booleanValue() || itemStack.getItem().equals(Items.chest_minecart) && this.bwValue.getValue().booleanValue()) {
            this.timer.reset();
            this.waiting = true;
        }
    }

    @EventTarget
    public void onEventTick(EventTick event) {
        if (isNull()) return;
        if (mc.theWorld == null || mc.theWorld.loadedEntityList.isEmpty()) {
            strength = false;
            regen = false;
            godaxe = false;
            gapple = false;
            kbball = false;
            return;
        }
        if (HYTUtils.isInLobby()) {
            strength = false;
            regen = false;
            godaxe = false;
            gapple = false;
            kbball = false;
            return;
        }
        if (autoKit.get()) {
            if (mc.currentScreen != null) {
                if (mc.currentScreen instanceof GuiChest chest) {
                    if (chest.lowerChestInventory.getDisplayName().toString().contains("职业"))
                        mc.playerController.windowClick(chest.inventorySlots.windowId, 6, 0, 0, mc.thePlayer);
                }
            }
        }
        if (mc.thePlayer.ticksExisted % 6 == 0) {
            for (final Entity ent : mc.theWorld.loadedEntityList) {
                if (ent instanceof EntityPlayer && ent != mc.thePlayer) {
                    final EntityPlayer player = (EntityPlayer) ent;
                    if (HYTUtils.isStrength(player) > 0 && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);

                        name = player.getCommandSenderName();
                        strength = true;
                    }
                    if (HYTUtils.isRegen(player) > 0 && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);

                        name = player.getCommandSenderName();
                        regen = true;
                    }
                    if (HYTUtils.isHoldingGodAxe(player) && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);

                        name = player.getCommandSenderName();
                        godaxe = true;
                    }
                    if (HYTUtils.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);

                        name = player.getCommandSenderName();
                        kbball = true;
                    }
                    if (HYTUtils.hasEatenGoldenApple(player) <= 0 || flaggedEntity.contains(player)) {
                        continue;
                    }
                    name = player.getCommandSenderName();
                    gapple = true;
                    flaggedEntity.add(player);

                }
            }
        }
    }

    @EventTarget
    public void onPacketReceiveEvent(EventPacket event) {
        if (AutoPlay.mc.thePlayer == null || AutoPlay.mc.theWorld == null) {
            return;
        }
        Packet packet = event.getPacket();
        String text = ((S02PacketChat) packet).getChatComponent().getUnformattedText();

        if (packet instanceof S02PacketChat) {
            if (PATTERN_BEHAVIOR_EXCEPTION.matcher(text).find()) {
                NotificationManager.post(NotificationType.WARNING, "BanChecker", "A player was banned.", 5.0f);
                ++ban;
            } else if (PATTERN_WIN_MESSAGE.matcher(text).find() || AutoPlay.mc.thePlayer.isSpectator() && this.toggleModule.getValue().booleanValue()) {
                this.toggleOffensiveModules(false);
                NotificationManager.post(NotificationType.SUCCESS, "Game Ending", "Sending you to next game in " + this.delayValue.getValue() + "s", 5.0f);
            } else if (text.contains(TEXT_LIKE_OPTIONS) || text.contains(TEXT_BEDWARS_GAME_END)) {
                NotificationManager.post(NotificationType.SUCCESS, "Game Ending", "Your Health: " + MathUtils.DF_1.format(AutoPlay.mc.thePlayer.getHealth()), 5.0f);
            } else if (text.contains(TEXT_COUNTDOWN)) {
                this.checkAndTogglePlayerTracker();
            }
        }

        if (text.contains("你在地图") && text.contains("赢得了")) {
            ++win;
        } else if (text.contains("[起床战争] Game 结束！感谢您的参与！") || text.contains("喜欢 一般 不喜欢")) {
            ++win;
        }
        if (text.contains("开始倒计时: 5 秒") && autoKit.get()) {
            int slot = 0;

            int nslot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
            mc.rightClickMouse();
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(nslot));
            System.out.println(slot);
            System.out.println(nslot);
        }
    }

    private void toggleOffensiveModules(boolean state) {
        ModuleManager moduleManager = Client.instance.moduleManager;
        moduleManager.getModule(InvCleaner.class).setState(state);
        moduleManager.getModule(ChestStealer.class).setState(state);
        moduleManager.getModule(KillAura.class).setState(state);
    }

    private void checkAndTogglePlayerTracker() {
        ModuleManager moduleManager = Client.instance.moduleManager;
        if (!moduleManager.getModule(PlayerTracker.class).getState()) {
            NotificationManager.post(NotificationType.WARNING, "Skywars Warning (Wait 15s)", "Please enable PlayerTracker.", 15.0f);
        } else if (this.toggleModule.getValue().booleanValue()) {
            this.toggleOffensiveModules(true);
        }
    }

    public void drop(int slot) {
        AutoPlay.mc.playerController.windowClick(AutoPlay.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, AutoPlay.mc.thePlayer);
    }
}

