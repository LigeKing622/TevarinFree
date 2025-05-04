package dev.tevarin.module.impl.combat;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventAttack;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;

public class SuperKnockBack extends Module {
    public static boolean sprint = true, wTap;

    public SuperKnockBack() {
        super("SuperKnockBack", Category.Combat);
    }

    @Override
    public void onDisable() {
        sprint = true;
    }


    @Override
    public void onEnable() {
        sprint = true;
    }

    @EventTarget
    public void onAttack(EventAttack event) {

        wTap = Math.random() * 100 < 100;

        //if (!wTap) return;

    }

    @EventTarget
    public void onPre(EventMotion event) {
        if (event.isPre()) {
            // if (!wTap) return;

            if (mc.thePlayer.moveForward > 0 && mc.thePlayer.serverSprintState == mc.thePlayer.isSprinting()) {
                sprint = !mc.thePlayer.serverSprintState;
            }
        }

    }

}
