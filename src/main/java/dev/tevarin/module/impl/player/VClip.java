package dev.tevarin.module.impl.player;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.event.impl.events.PacketReceiveEvent;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.*;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.block.BlockGlass;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Map;


public class VClip extends Module {
    private final NumberValue delay = new NumberValue("Delay", 25.0, 20.0, 30.0, 1.0);

    public VClip() {
        super("VClip", Category.Player);
    }

    private final TimerUtil timers = new TimerUtil();

    private boolean waiting;
    private int timer = 0;
    private boolean game = false;

    @Override
    public void onEnable() {
        waiting = false;
        timers.reset();
        game = false;
        timer = 0;
    }

    @Override
    public void onDisable() {
        game = false;
        timer = 0;
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPre()) {
            this.setSuffix("HYT");
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {

        if (getModule(Blink.class).state) {
            for (Map.Entry<BlockPos, ?> block : BlockUtil.searchBlocks(3).entrySet()) {
                BlockPos blockpos = block.getKey();
                if (block.getValue() instanceof BlockGlass) {
                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockpos, EnumFacing.DOWN));
                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockpos, EnumFacing.DOWN));
                    mc.theWorld.setBlockState(blockpos, Blocks.air.getDefaultState(), 2);
                    BlinkUtils.setCantSlowRelease(true);
                }

            }
            if (timer >= delay.getValue()) {
                game = false;
                timer = 0;
                BlinkUtils.setCantSlowRelease(false);
                getModule(Blink.class).setState(false);
            }
            if (game) timer++;
            if (game) DebugUtil.print(String.valueOf(timer));
        }
    }


    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        Object packet = event.getPacket();
        if (packet instanceof S02PacketChat) {
            String text = ((S02PacketChat) packet).getChatComponent().getUnformattedText();
            String loseMessage = "You died! Want to play again? Click here!";
            String winMessage = "You won! Want to play again? Click here!";
            if ((text.contains(winMessage) && text.length() < winMessage.length() + 3) || (text.contains(loseMessage) && text.length() < loseMessage.length() + 3)) {
                waiting = true;
                timers.reset();
            }
            if (text.contains("开始倒计时: 3 秒")) {
                BlinkUtils.setCantSlowRelease(true);
                getModule(Blink.class).setState(true);
            }
            if (text.contains("开始倒计时: 1 秒")) {
                game = true;
            }
        }
    }
}
