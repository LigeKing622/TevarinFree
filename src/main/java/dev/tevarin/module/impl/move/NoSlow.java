package dev.tevarin.module.impl.move;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventSlowDown;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.player.MovementUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.LinkedBlockingQueue;



public class NoSlow extends Module {
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue();
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Grim", "Vanalia"}, "Grim");
    private final BoolValue food = new BoolValue("Food", true);
    private final BoolValue bow = new BoolValue("Bow", true);
    private boolean hasDroppedFood = false;
    public static boolean fix = false;

    public NoSlow() {
        super("NoSlow", Category.Movement);

    }

    private boolean isHoldingPotionAndSword(ItemStack stack, boolean checkSword, boolean checkBow) {
        if (stack == null) {
            return false;
        } else if (stack.getItem() instanceof ItemSword && checkSword) {
            return true;
        } else {
            return stack.getItem() instanceof ItemBow && checkBow ? (Boolean) this.bow.getValue() : stack.getItem() instanceof ItemBucketMilk;
        }
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (mc.thePlayer.getHeldItem() != null) {
            if (event.isPre()) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem() && ((String) this.mode.getValue()).equals("Grim")) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }

                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && MovementUtil.isMoving() && ((String) this.mode.getValue()).equals("Grim")) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }

                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && (Boolean) this.food.getValue() && ((String) this.mode.getValue()).equals("Grim")) {
                    if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) {
                        return;
                    }

                    ItemStack itemInHand = mc.thePlayer.getCurrentEquippedItem();
                    ItemStack itemStack = mc.thePlayer.getHeldItem();
                    int itemID = Item.getIdFromItem(itemInHand.getItem());
                    int itemMeta = itemInHand.getMetadata();
                    String itemId = itemInHand.getItem().getUnlocalizedName();
                    if (mc.thePlayer.getHeldItem() != null && (itemID != 322 || itemMeta != 1) && !itemId.equals("item.appleGoldEnchanted")) {
                        if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                            if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
                                Minecraft.getMinecraft().rightClickDelayTimer = 4;
                            } else {
                                Minecraft.getMinecraft().rightClickDelayTimer = 4;
                            }
                        }

                        if (mc.thePlayer.isUsingItem() && !this.hasDroppedFood && itemStack.stackSize > 1) {
                            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                            this.hasDroppedFood = true;
                            fix = true;
                        } else if (!mc.thePlayer.isUsingItem()) {
                            this.hasDroppedFood = false;
                            (new Thread(() -> {
                                try {
                                    Thread.sleep(500L);
                                    fix = false;
                                } catch (InterruptedException var1) {
                                    InterruptedException ex = var1;
                                    ex.printStackTrace();
                                }

                            })).start();
                        }
                    }
                } else {
                    fix = false;
                }
            }

            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem() && event.isPost()) {
                if (((String) this.mode.getValue()).equals("Grim")) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }

                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && MovementUtil.isMoving() && event.isPost() && ((String) this.mode.getValue()).equals("Grim")) {
                    PacketWrapper useItem = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Type.VAR_INT, 1);
                    PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                    PacketWrapper useItem2 = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem2.write(Type.VAR_INT, 0);
                    PacketUtil.sendToServer(useItem2, Protocol1_8To1_9.class, true, true);
                }
            }
        }

    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.packets.clear();
    }

    @EventTarget
    public void onSlowDown(EventSlowDown event) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.getHeldItem() != null) {
            if (mc.thePlayer.isUsingItem() && this.isHoldingPotionAndSword(mc.thePlayer.getHeldItem(), true, false)) {
                event.setCancelled(true);
                mc.thePlayer.setSprinting(true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            } else if (mc.thePlayer.isUsingItem() && this.isHoldingPotionAndSword(mc.thePlayer.getHeldItem(), false, true)) {
                event.setCancelled(true);
                mc.thePlayer.setSprinting(true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            } else if (!((String) this.mode.getValue()).equals("Grim") && mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && (Boolean) this.food.getValue()) {
                event.setCancelled(true);
            }

        }
    }

    public String getModTag() {
        return (String) this.mode.getValue();
    }
}
