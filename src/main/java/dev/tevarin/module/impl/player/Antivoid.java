/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package dev.tevarin.module.impl.player;


import dev.tevarin.event.annotations.EventPriority;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.WorldUtil;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class Antivoid
        extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Flag", "Collision flag", "Bounce"},"Flag");
    private final NumberValue bounceMotion = new NumberValue("Bounce motion", 1.5, 0.4, 3.0, 0.1, () -> this.mode.is("Bounce"));
    private final NumberValue minFallDist = new NumberValue("Min fall dist", 3.5, 2.0, 10.0, 0.25);

    private BlockPos collisionBlock;
    private boolean blinking;

    private boolean receivedLagback;

    public Antivoid() {
        super("Antivoid", Category.Player);

    }

    @Override
    public void onEnable() {
        this.collisionBlock = null;
    }

    @Override
    public void onDisable() {

        this.receivedLagback = false;
    }

    @EventTarget()
    @EventPriority()
    public void onTick(EventTick event) {
        if (Antivoid.mc.thePlayer.ticksExisted < 10) {
            this.collisionBlock = null;
            return;
        }
        switch (this.mode.get()) {
            case "Bounce": {
                if (!this.shouldSetback() || !(Antivoid.mc.thePlayer.motionY < -0.1)) break;
                Antivoid.mc.thePlayer.motionY = this.bounceMotion.getValue();
                break;
            }
            case "Collision flag": {
                if (this.shouldSetback()) {
                    if (this.collisionBlock != null) {
                        Antivoid.mc.theWorld.setBlockToAir(this.collisionBlock);
                    }
                    this.collisionBlock = new BlockPos(Antivoid.mc.thePlayer.posX, Antivoid.mc.thePlayer.posY - 1.0, Antivoid.mc.thePlayer.posZ);
                    Antivoid.mc.theWorld.setBlockState(this.collisionBlock, Blocks.barrier.getDefaultState());
                    break;
                }
                if (this.collisionBlock == null) break;
                Antivoid.mc.theWorld.setBlockToAir(this.collisionBlock);
                this.collisionBlock = null;
                break;
            }

        }
    }

    @EventTarget()
    @EventPriority(4)
    public void onMotion(EventMotion event) {
        switch (this.mode.get()) {
            case "Flag": {
                if (!this.shouldSetback()) break;
                event.setY(event.getY() + 8.0 + Math.random());
            }
        }
    }


    private boolean shouldSetback() {
        return (double)Antivoid.mc.thePlayer.fallDistance >= this.minFallDist.getValue() && !WorldUtil.isBlockUnder() && Antivoid.mc.thePlayer.ticksExisted >= 100;
    }



    
}

