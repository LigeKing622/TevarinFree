/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package dev.tevarin.utils;


import dev.tevarin.Client;
import dev.tevarin.module.ModuleManager;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.module.impl.misc.Teams;
import dev.tevarin.module.impl.move.Speed;
import dev.tevarin.module.impl.player.Blink;
import dev.tevarin.module.impl.render.BlockAnimation;
import dev.tevarin.module.impl.render.HUD;

public class ModuleUtil {
    private static ModuleManager moduleManager;

    private static ModuleManager getModuleManager() {
        if (moduleManager == null) {
            moduleManager = Client.instance.getModuleManager();
        }
        return moduleManager;
    }

    public static KillAura getKillaura() {
        return ModuleUtil.getModuleManager().getModule(KillAura.class);
    }


    public static Teams getTeams() {
        return ModuleUtil.getModuleManager().getModule(Teams.class);
    }


    public static Speed getSpeed() {
        return ModuleUtil.getModuleManager().getModule(Speed.class);
    }

    public static HUD getHUD() {
        return ModuleUtil.getModuleManager().getModule(HUD.class);
    }


    public static BlockAnimation getAnimations() {
        return ModuleUtil.getModuleManager().getModule(BlockAnimation.class);
    }

    public static Blink getBlink() {
        return ModuleUtil.getModuleManager().getModule(Blink.class);
    }
}

