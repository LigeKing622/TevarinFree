package dev.tevarin.module.impl.move;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventStrafe;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.Gapple;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.Movement);
    }

    @EventTarget
    public void onStrafe(EventStrafe e) {
        if (Client.instance.moduleManager.getModule(Gapple.class).getState()) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
            return;
        }
        mc.gameSettings.keyBindSprint.pressed = true;
    }
}
