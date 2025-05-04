package dev.tevarin.module.impl.combat;

import dev.tevarin.event.annotations.EventPriority;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.*;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.*;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.BoolValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Gapple
        extends Module {

    private final BoolValue stuck = new BoolValue("Stuck", true);
    private final StopWatch stopWatch = new StopWatch();
    public static boolean eating = false;
    public static int s = 0;
    private int slot = 0;
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue();
    private boolean needSkip = false;
    private boolean start;
    private final Animation anim = new DecelerateAnimation(250, 1);
    public Gapple() {
        super("Gapple" ,Category.Combat);
    }

    public void onEnable() {


        this.packets.clear();
        this.slot = -1;
        this.needSkip = false;
        this.s = 0;
        eating = false;
        if (mc.thePlayer != null) {
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        this.start = true;
    }

    public void onDisable() {
        eating = false;
        this.release();
        if (!KillAura.isBlocking && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.gameSettings.keyBindSprint.pressed = false;
        }

    }

    @EventTarget
    public void onWorld(EventWorldLoad e) {
        eating = false;
        this.release();
    }

    @EventTarget
    public void onMoveMath(MoveMathEvent event) {
        if ((Boolean)this.stuck.getValue()) {
            if (eating && mc.thePlayer.positionUpdateTicks < 19 && !this.needSkip) {
                event.setCancelled(true);
            } else if (this.needSkip) {
                this.needSkip = false;
            }

        }
    }

    @EventTarget
    public void onPost(EventMotion event) {
        if (!event.isPre()) {
            if (eating) {
                ++this.s;
                this.packets.add(new C01PacketChatMessage("release"));
            }

        }
    }

    @EventTarget
    public void onPre(EventTick event) {

        if ((double)mc.thePlayer.getHealth() <= 0.01) {
            this.toggle();
        }

        if (mc.thePlayer != null && mc.thePlayer.isEntityAlive()) {
            if (this.start && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                mc.getNetHandler().addToSendQueueUnregistered(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem)));
                this.start = false;
            }

            if (mc.playerController.getCurrentGameType().isSurvivalOrAdventure() && this.stopWatch.finished(0L)) {
                this.slot = this.getGApple();
                if (this.slot != -1 && !(mc.thePlayer.getHealth() >= 40.0F)) {
                    eating = true;
                    if (this.s >= 32) {
                        mc.gameSettings.keyBindSprint.pressed = false;
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack()));
                        this.release();
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        this.stopWatch.reset();
                    } else {
                        Packet packet;
                        if (mc.thePlayer.ticksExisted % 5 == 0) {
                            for(; !this.packets.isEmpty(); mc.getNetHandler().addToSendQueueUnregistered(packet)) {
                                packet = (Packet)this.packets.poll();
                                if (packet instanceof C01PacketChatMessage) {
                                    break;
                                }

                                if (packet instanceof C03PacketPlayer) {
                                    --this.s;
                                }
                            }
                        }
                    }
                } else if (eating) {
                    eating = false;
                    this.release();
                }

            } else {
                eating = false;
                this.release();
            }
        } else {
            eating = false;
            this.packets.clear();
        }
    }
    @EventTarget
    public void onPacketSend(EventPacket event) {
        if (event.getEventType() != EventPacket.EventState.SEND) return;
        if (event.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) event.getPacket()).getStatus().equals(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
            event.setCancelled(true);
        }
    }
    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity wrapped) {
            if (wrapped.getEntityID() == mc.thePlayer.getEntityId()) {
                this.needSkip = true;
            }
        }

    }

    @EventTarget
    public void onSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer != null && mc.playerController.getCurrentGameType().isSurvivalOrAdventure()) {
            if (eating && packet instanceof C07PacketPlayerDigging) {
                C07PacketPlayerDigging dig = (C07PacketPlayerDigging)packet;
                if (dig.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    event.setCancelled(true);
                }
            }

            if (!(packet instanceof C00Handshake) && !(packet instanceof C00PacketLoginStart) && !(packet instanceof C00PacketServerQuery) && !(packet instanceof C01PacketPing) && !(packet instanceof C01PacketEncryptionResponse) && !(packet instanceof C01PacketChatMessage)) {
                if (!(packet instanceof C09PacketHeldItemChange) && !(packet instanceof C0EPacketClickWindow) && !(packet instanceof C16PacketClientStatus) && !(packet instanceof C0DPacketCloseWindow) && eating) {
                    event.setCancelled(true);
                    this.packets.add(packet);
                }

            }
        }
    }

    @EventTarget
    public void onSlow(EventSlowDown event) {
        if (eating) {
            event.setCancelled(false);
        }

    }

    private void release() {
        if (mc.getNetHandler() != null) {
            while(!this.packets.isEmpty()) {
                Packet<?> packet = (Packet)this.packets.poll();
                if (!(packet instanceof C01PacketChatMessage) && !(packet instanceof C08PacketPlayerBlockPlacement) && !(packet instanceof C07PacketPlayerDigging)) {
                    mc.getNetHandler().addToSendQueueUnregistered(packet);
                }
            }

            this.s = 0;
        }
    }

    private int getGApple() {
        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemAppleGold) {
                return i;
            }
        }

        this.toggle();
        return -1;
    }


}