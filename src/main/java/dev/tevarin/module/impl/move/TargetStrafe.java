package dev.tevarin.module.impl.move;

import dev.tevarin.Client;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.utils.player.PlayerUtil;
import dev.tevarin.utils.player.RotationUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;

public class TargetStrafe
        extends Module {
    private final NumberValue maxRange = new NumberValue("Max range", 3.0, 1.0, 6.0, 0.1);
    public final BoolValue whilePressingSpace = new BoolValue("While pressing space", false);
    private boolean goingRight;
    private KillAura killaura;

    public TargetStrafe() {
        super("TargetStrafe", Category.Combat);

    }


    public void onClientStarted() {
        this.killaura = Client.instance.moduleManager.getModule(KillAura.class);
    }

    public boolean shouldTargetStrafe() {
        return this.killaura.getState() && this.killaura.target != null&& this.killaura.getDistanceToEntity(this.killaura.target) <= this.killaura.range.getValue() &&  this.getState() && (Keyboard.isKeyDown(TargetStrafe.mc.gameSettings.keyBindJump.getKeyCode()) || !this.whilePressingSpace.get());
    }

    public float getDirection() {
        float direction;
        EntityLivingBase target;
        double distance;
        if (TargetStrafe.mc.thePlayer.isCollidedHorizontally || !PlayerUtil.isBlockUnder(3)) {
            boolean bl = this.goingRight = !this.goingRight;
        }
        if ((distance = this.killaura.getDistanceToEntity(target = this.killaura.target)) > this.maxRange.getValue()) {
            direction = RotationUtil.getRotationsToEntity(target, false)[0];
        } else {
            double offset = 90.0 - this.killaura.getDistanceToEntity(target) * 5.0;
            if (!this.goingRight) {
                offset = -offset;
            }
            direction = (float)((double)RotationUtil.getRotationsToEntity(target, false)[0] + offset);
        }
        return (float)Math.toRadians(direction);
    }
}

