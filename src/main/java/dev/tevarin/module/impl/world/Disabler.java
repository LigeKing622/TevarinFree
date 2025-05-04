package dev.tevarin.module.impl.world;

import dev.tevarin.event.annotations.EventPriority;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventHigherPacketSend;
import dev.tevarin.event.impl.events.EventPacket;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.move.NoSlow;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.ui.hud.notification.NotificationType;
import dev.tevarin.utils.DebugUtil;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.value.impl.BoolValue;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class Disabler
        extends Module {
    public static final BoolValue postValue = new BoolValue("Post", true);
    public final BoolValue oldPostValue = new BoolValue("OldPost", false);
    public final BoolValue digValue = new BoolValue("Digging", true);
    public final BoolValue blockValue = new BoolValue("Cancel Blocking Packet", false);
    private final BoolValue badPacketsA = new BoolValue("BadPacketsA", true);
    public static final BoolValue badPacketsF = new BoolValue("BadPacketsF", true);
    private final BoolValue fakePingValue = new BoolValue("FakePing", false);
    public final BoolValue fastBreak = new BoolValue("FastBreak", true);
    public final BoolValue debug = new BoolValue("Debug", true);
    private final HashMap<Packet<?>, Long> packetsMap = new HashMap();
    int lastSlot = -1;
    boolean lastSprinting;
    static Disabler INSTANCE;
    private boolean S08 = false;
    private NoSlow noSlow;

    @Override
    public void onEnable() {
        this.noSlow = this.getModule(NoSlow.class);
    }

    public Disabler() {
        super("Disabler", Category.Misc);
        INSTANCE = this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    @EventPriority(value=9)
    public void onUpdate(EventUpdate event) {
        if (this.fakePingValue.getValue().booleanValue()) {
            try {
                HashMap<Packet<?>, Long> hashMap = this.packetsMap;
                synchronized (hashMap) {
                    Iterator<Map.Entry<Packet<?>, Long>> iterator = this.packetsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Packet<?>, Long> entry = iterator.next();
                        if (entry.getValue() >= System.currentTimeMillis()) continue;
                        mc.getNetHandler().addToSendQueue(entry.getKey());
                        iterator.remove();
                    }
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (this.S08) {
            Stuck.onS08();
            this.S08 = false;
        }
    }

    public static void onS08() {
        Disabler.INSTANCE.S08 = true;
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.lastSlot = -1;
        this.lastSprinting = false;
        Scaffold scaffold = this.getModule(Scaffold.class);
        scaffold.tip = false;
        scaffold.vl = 0;
    }

    @EventTarget
    @EventPriority(value=0)
    public void onHigherPacket(EventHigherPacketSend event) {
        Packet packet = event.getPacket();
        if (Disabler.mc.thePlayer == null) {
            return;
        }
        if (Disabler.mc.thePlayer.isDead) {
            return;
        }
        if ((this.blockValue.getValue().booleanValue() || this.digValue.getValue().booleanValue()) && Disabler.mc.thePlayer.getHeldItem() != null && this.blockValue.getValue().booleanValue() && Disabler.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08 = (C08PacketPlayerBlockPlacement)packet;
            if (this.debug.getValue().booleanValue()) {
                DebugUtil.log(c08.getPosition());
            }
            if ((c08.getPosition().getX() == -1 || c08.getFacingX() == -1.0f) && c08.getFacingZ() == -1.0f) {
                if (this.debug.getValue().booleanValue()) {
                    NotificationManager.post(NotificationType.INFO, "Disabler", "已关闭原神");
                }
                event.setCancelled(true);
            }
        }
        if (this.digValue.getValue().booleanValue() && event.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07 = (C07PacketPlayerDigging)packet;
            if (Disabler.mc.thePlayer.getHeldItem() != null && Disabler.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && c07.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (this.debug.getValue().booleanValue()) {
                    NotificationManager.post(NotificationType.INFO, "Disabler", "玩原神玩的。。。");
                }
                event.setCancelled(true);
                Disabler.mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(0, 36, 0, 2, new ItemStack(Block.getBlockById(166)), (short) 0));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(Disabler.mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(Disabler.mc.thePlayer.inventory.currentItem));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onPacket(EventPacket event) {
        Packet packet = event.getPacket();
        if (Disabler.mc.thePlayer == null) {
            return;
        }
        if (Disabler.mc.thePlayer.isDead) {
            return;
        }
        if (badPacketsF.getValue().booleanValue() && packet instanceof C0BPacketEntityAction) {
            if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = true;
            } else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
        if (this.oldPostValue.getValue().booleanValue() && mc.getCurrentServerData() != null && (packet instanceof C0APacketAnimation || packet instanceof C02PacketUseEntity || packet instanceof C0EPacketClickWindow || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging)) {
            PacketUtil.send(new C0FPacketConfirmTransaction(114, (short) 514, true));
        }
        if (this.fastBreak.getValue().booleanValue() && packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, ((C07PacketPlayerDigging)packet).getPosition(), ((C07PacketPlayerDigging)packet).getFacing()));
        }
        if (this.badPacketsA.getValue().booleanValue() && packet instanceof C09PacketHeldItemChange) {
            int slot = ((C09PacketHeldItemChange)packet).getSlotId();
            if (slot == this.lastSlot && slot != -1) {
                event.setCancelled(true);
            }
            this.lastSlot = ((C09PacketHeldItemChange)packet).getSlotId();
        }
        if (this.fakePingValue.getValue().booleanValue() && (packet instanceof C00PacketKeepAlive || packet instanceof C16PacketClientStatus) && !(Disabler.mc.thePlayer.getHealth() <= 0.0f) && !this.packetsMap.containsKey(packet)) {
            event.setCancelled(true);
            HashMap<Packet<?>, Long> hashMap = this.packetsMap;
            synchronized (hashMap) {
                this.packetsMap.put(packet, System.currentTimeMillis() + TimerUtil.randomDelay(199999, 9999999));
            }
        }
    }

    public static boolean getGrimPost() {
        return Disabler.mc.thePlayer != null && Disabler.mc.theWorld != null && INSTANCE != null && INSTANCE.getState() && postValue.getValue() && Disabler.mc.thePlayer.ticksExisted > 30;
    }

    public static boolean shouldProcess() {
        return !Stuck.isStuck();
    }

}

