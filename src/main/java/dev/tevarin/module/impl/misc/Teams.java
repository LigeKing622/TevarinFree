package dev.tevarin.module.impl.misc;

import dev.tevarin.Client;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.player.PlayerUtil;
import dev.tevarin.value.impl.BoolValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Objects;

public class Teams extends Module {
    private static final BoolValue armorValue = new BoolValue("ArmorColor", true);
    private static final BoolValue colorValue = new BoolValue("Color", true);
    private static final BoolValue scoreboardValue = new BoolValue("ScoreboardTeam", true);


    public Teams() {
        super("Teams", Category.Misc);
    }

    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (Objects.requireNonNull(Client.instance.moduleManager.getModule(Teams.class)).getState()) {
                return (armorValue.getValue() && PlayerUtil.armorTeam(entityPlayer)) ||
                        (colorValue.getValue() && PlayerUtil.colorTeam(entityPlayer)) ||
                        (scoreboardValue.getValue() && PlayerUtil.scoreTeam(entityPlayer));
            }
            return false;
        }
        return false;
    }


}
