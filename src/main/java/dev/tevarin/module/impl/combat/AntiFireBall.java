package dev.tevarin.module.impl.combat;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AntiFireBall extends Module {

    public AntiFireBall() {
        super("AntiFireBall", Category.Combat);
    }

    public static final TimerUtil timer = new TimerUtil();


    @EventTarget
    public void onUpdate(final EventMotion event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFireball) {
                if (mc.thePlayer.getDistanceToEntity(entity) < 6.0 && timer.hasTimeElapsed(0L)) {
                    mc.getNetHandler().getNetworkManager().sendPacket((new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK)));
                    mc.thePlayer.swingItem();
                }
            }
        }
    }
}
