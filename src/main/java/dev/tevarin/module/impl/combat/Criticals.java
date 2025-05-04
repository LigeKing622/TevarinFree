package dev.tevarin.module.impl.combat;


import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventStrafe;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.event.impl.events.MoveEvent;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;


public class Criticals extends Module {
    public Criticals() {
        super("Criticals", Category.Combat);
    }

    private final ModeValue mode = new ModeValue("Mode", new String[]{"Packet", "WatchDog", "Legit", "Grim"}, "Packet");
    private final NumberValue delay = new NumberValue("Delay", 1, 0, 20, 1);
    private final BoolValue display = new BoolValue("DeBug", true);
    boolean gappleNoGround = false;

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        mc.theWorld.skiptick = 0;
    }

    @EventTarget
    public void onMotionEvent(EventMotion e) {
        if (e.isPre()) {
            this.setSuffix(mode.get());
            switch (mode.get()) {
                case "WatchDog": {
                    if (e.isGround()) {
                        if (KillAura.target != null && KillAura.target.hurtTime >= delay.getValue().intValue()) {
                            for (double offset : new double[]{0.06f, 0.01f}) {
                                onCritical();
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset + (Math.random() * 0.001), mc.thePlayer.posZ, false));
                            }
                        }
                    }
                    break;
                }
                case "Packet": {
                    if (KillAura.target != null && KillAura.target.hurtTime >= delay.getValue().intValue()) {
                        if (KillAura.target.hurtTime > delay.getValue().intValue()) {
                            for (double offset : new double[]{0.006253453, 0.002253453, 0.001253453}) {
                                onCritical();
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                            }
                        }
                    }
                    break;
                }
                case "Legit": {
                    if (KillAura.target != null && KillAura.target.hurtTime >= delay.getValue() && mc.thePlayer.onGround) {
                        if (KillAura.target.hurtTime > delay.getValue().intValue()) {
                            onCritical();
                            if (!mc.thePlayer.onGround) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer(false));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        switch (mode.get()) {
            case "Grim": {
                if (isGapple()) {
                    return;
                }
                if (KillAura.target == null) return;
                if (isGapple() && !mc.thePlayer.onGround) {
                    gappleNoGround = true;
                }
                if (!isGapple() && gappleNoGround && mc.thePlayer.onGround) {
                    gappleNoGround = false;
                }
                if (cantCrit(KillAura.target)) {
                    reset();
                } else {
                    KillAura aura = Client.instance.getModuleManager().getModule(KillAura.class);
                    if (KillAura.target != null) {
                        if (!isNull() && mc.thePlayer.motionY < 0 && !mc.thePlayer.onGround && aura.state && mc.thePlayer.getClosestDistanceToEntity(KillAura.target) <= 2.0f && mc.theWorld.skiptick <= 0) {
                            mc.theWorld.skiptick++;
                        } else {
                            if (!isNull() && (!aura.state)) {
                                reset();
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        switch (mode.get()) {
            case "Grim": {
                if (isGapple()) {
                    return;
                }
                if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
                    if (KillAura.target != null && mc.thePlayer.getDistanceToEntity(KillAura.target) <= 3.0f) {
                        onCritical();
                        mc.thePlayer.jump();
                    }
                }
                break;
            }
        }
    }

    @EventTarget
    public void onCritical() {
        if (KillAura.target != null && display.get()) {
        }
    }

    public boolean cantCrit(EntityLivingBase targetEntity) {
        EntityPlayerSP player = mc.thePlayer;
        return player.isOnLadder() || player.isInWater() || player.isInLava() || player.ridingEntity != null
                || targetEntity.hurtTime > 10 || targetEntity.getHealth() <= 0 || isGapple() || gappleNoGround;
    }

    private void reset() {
        mc.theWorld.skiptick = 0;
    }
}
