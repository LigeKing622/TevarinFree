package dev.tevarin.module.impl.player;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventPacket;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.utils.math.MathUtils;
import dev.tevarin.utils.player.MoveUtil;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BalanceTimer extends Module {
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    final Animation anim = new DecelerateAnimation(250, 1);
    private final NumberValue amount = new NumberValue("Amount", 1, 1, 10, 0.1);
    private int count = 0;

    public BalanceTimer() {
        super("BalanceTimer", Category.Player);
    }

    @EventTarget
    public void onMotionEvent(EventMotion event) {
        PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(MathUtils.getRandom(114514, 191981000), (short) MathUtils.getRandomInRange(114514, 191981000), true));
        if (count > 0) {
            mc.timer.timerSpeed = MoveUtil.isMoving() ? amount.getValue().floatValue() : 1f;
        } else {
            mc.timer.timerSpeed = 1f;
            if (!packets.isEmpty()) {
                packets.forEach(PacketUtil::sendPacketNoEvent);
                packets.clear();
            }
        }
    }

    @EventTarget
    public void onPacketSendEvent(EventPacket event) {
        if (event.getEventType() == EventPacket.EventState.SEND) {
            if (event.getPacket() instanceof C03PacketPlayer && !(event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
                if (!((C03PacketPlayer) event.getPacket()).isMoving()) {
                    count += 50;
                    event.setCancelled(true);
                } else {
                    if (count > 0) {
                        count -= 50;
                    }
                }
            }
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                event.setCancelled(true);
                packets.add(event.getPacket());
            }
            if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK)
                toggle();
        }
        if (event.getEventType() == EventPacket.EventState.RECEIVE) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                toggle();
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        if (!packets.isEmpty()) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
        }

        count = 0;

    }

    public void renderProgessBar4() {
        anim.setDirection(state ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!state && anim.isDone()) return;
        String countStr = String.valueOf(count);
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = (float) anim.getOutput();
        int spacing = 3;
        String text = "§r Grim Timer Balance: " + "§l" + countStr;
        float textWidth = FontManager.interSemiBold18.getStringWidth(text);
        float totalWidth = ((textWidth + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
        y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
        float height = 20;
        Color c1 = ColorUtil.applyOpacity(HUD.color(1), 222);
        Color c2 = ColorUtil.applyOpacity(HUD.color(6), 222);
        RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 23);
        RoundedUtil.drawRound(x, y, totalWidth, height + 20, 4, ColorUtil.tripleColor(20, .45f));
        FontManager.interSemiBold18.drawString(text, x + 2 + spacing, y + 9.5f, -1);
        RoundedUtil.drawRound((x + 3) * output, y + 25, totalWidth - 8 * output, 5, 2, new Color(166, 164, 164, 81));
        RoundedUtil.drawGradientHorizontal(x + 3, y + 25, (totalWidth - 8) * Math.min(Math.max((count / 7500f), 0F), 1f), 5, 2, c1, c2);
        RenderUtil.scissorEnd();

    }

}