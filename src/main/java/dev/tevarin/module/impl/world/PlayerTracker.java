package dev.tevarin.module.impl.world;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.misc.Teams;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.ui.hud.notification.NotificationType;
import dev.tevarin.utils.HYTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerTracker extends Module {
    public static List<Entity> flaggedEntity = new ArrayList<Entity>();

    public PlayerTracker() {
        super("PlayerTracker", Category.World);
    }

    @EventTarget
    public void onWorld(EventWorldLoad e) {
        flaggedEntity.clear();
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (PlayerTracker.mc.theWorld == null || PlayerTracker.mc.theWorld.loadedEntityList.isEmpty()) {
            return;
        }
        if (HYTUtils.isInLobby()) {
            return;
        }
        if (PlayerTracker.mc.thePlayer.ticksExisted % 6 == 0) {
            for (Entity ent : PlayerTracker.mc.theWorld.loadedEntityList) {
                if (!(ent instanceof EntityPlayer) || ent == PlayerTracker.mc.thePlayer) continue;
                EntityPlayer player = (EntityPlayer) ent;
                if (HYTUtils.isStrength(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    NotificationManager.post(NotificationType.WARNING, "PlayerTracker", player.getName() + " has Strength effect!", 20.0f);
                }
                if (HYTUtils.isRegen(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    NotificationManager.post(NotificationType.WARNING, "PlayerTracker", player.getName() + " has Regen effect!", 20.0f);
                }
                if (HYTUtils.isHoldingGodAxe(player) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    NotificationManager.post(NotificationType.WARNING, "PlayerTracker", player.getName() + " is holding God Axe!", 20.0f);
                }
                if (HYTUtils.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    NotificationManager.post(NotificationType.WARNING, "PlayerTracker", player.getName() + " is holding KB ball!", 20.0f);
                }
                if (HYTUtils.hasEatenGoldenApple(player) <= 0 || flaggedEntity.contains(player) || Teams.isSameTeam(player))
                    continue;
                flaggedEntity.add(player);
                NotificationManager.post(NotificationType.WARNING, "PlayerTracker", player.getName() + " has eaten Golden Apple!", 20.0f);
            }
        }
    }
}
