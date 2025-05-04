package dev.tevarin.module.impl.player;

import com.mojang.authlib.GameProfile;
import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.module.impl.world.Scaffold;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.BlinkUtils;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import lombok.Getter;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import java.awt.*;
import java.util.UUID;

public class Blink
        extends Module {
    @Getter
    private static EntityOtherPlayerMP fakePlayer;

    private final BoolValue slowRelease = new BoolValue("SlowRelease", false);
    private final NumberValue releaseDelay = new NumberValue("ReleaseDelay", 1000, 10000, 300, 10);
    private final TimerUtil timer = new TimerUtil();

    final Animation anim = new DecelerateAnimation(250, 1);

    public Blink() {
        super("Blink", Category.Player);
    }

    @Override
    public void onEnable() {
        if (Blink.mc.thePlayer == null) {
            return;
        }
        timer.reset();
        BlinkUtils.startBlink();
        fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(new UUID(69L, 96L), "[Blink]" + mc.thePlayer.getName()));
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-1337, fakePlayer);
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition position = (C03PacketPlayer.C04PacketPlayerPosition) packet;
            this.fakePlayer.setPositionAndRotation2(position.x, position.y, position.z, this.fakePlayer.rotationYaw, this.fakePlayer.rotationPitch, 3, true);
            this.fakePlayer.onGround = position.isOnGround();
        } else if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook rotation = (C03PacketPlayer.C05PacketPlayerLook) packet;
            this.fakePlayer.setPositionAndRotation2(this.fakePlayer.posX, this.fakePlayer.posY, this.fakePlayer.posZ, rotation.getYaw(), rotation.getPitch(), 3, true);
            this.fakePlayer.onGround = rotation.isOnGround();
            this.fakePlayer.rotationYawHead = rotation.getYaw();
            this.fakePlayer.rotationYaw = rotation.getYaw();
            this.fakePlayer.rotationPitch = rotation.getPitch();
        } else if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook positionRotation = (C03PacketPlayer.C06PacketPlayerPosLook) packet;
            this.fakePlayer.setPositionAndRotation2(positionRotation.x, positionRotation.y, positionRotation.z, positionRotation.getYaw(), positionRotation.getPitch(), 3, true);
            this.fakePlayer.onGround = positionRotation.isOnGround();
            this.fakePlayer.rotationYawHead = positionRotation.getYaw();
            this.fakePlayer.rotationYaw = positionRotation.getYaw();
            this.fakePlayer.rotationPitch = positionRotation.getPitch();
        } else if (packet instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction action = (C0BPacketEntityAction) packet;
            if (action.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                this.fakePlayer.setSprinting(true);
            } else if (action.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                this.fakePlayer.setSprinting(false);
            } else if (action.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
                this.fakePlayer.setSneaking(true);
            } else if (action.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
                this.fakePlayer.setSneaking(false);
            }
        } else if (packet instanceof C0APacketAnimation) {
            C0APacketAnimation animation = (C0APacketAnimation) packet;
            this.fakePlayer.swingItem();
        }
    }

    @Override
    public void onDisable() {
        BlinkUtils.stopBlink();
        timer.reset();
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
    }

    @EventTarget
    private void onTick(EventTick event) {

        if (slowRelease.get() && BlinkUtils.isBlinking() && timer.hasReached(releaseDelay.getValue().longValue())) {
            BlinkUtils.releaseC03render(1);
        }
    }

    @Override
    public String getSuffix() {
        return "Grim";
    }
}

