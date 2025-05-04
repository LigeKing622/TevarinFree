package dev.tevarin.module.impl.misc;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.manager.FriendManager;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.utils.TimerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjglx.input.Mouse;

public class MCF extends Module {
    private TimerUtil timer = new TimerUtil();

    public MCF() {
        super("MCF", Category.Misc);
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (mc.inGameHasFocus) {
            boolean down = Mouse.isButtonDown(2);
            if (down) {
                if (timer.delay(200)) {
                    if ((!getModule(KillAura.class).getState() || KillAura.target == null) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                        String name = StringUtils.stripControlCodes(player.getName());
                        FriendManager friendManager = Client.instance.friendManager;
                        if (friendManager.isFriend(name)) {
                            friendManager.remove(name);
                        } else {
                            friendManager.add(name);
                        }
                    }
                    timer.reset();
                }
            }
        }
    }

}
