package dev.tevarin.module.impl.player;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Module {
    private final NumberValue ticks = new NumberValue("Ticks", 0, 0, 4, 1);

    public FastPlace() {
        super("FastPlace", Category.Player);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            // 取消防止方块延迟
            mc.rightClickDelayTimer = Math.min(0, ticks.getValue().intValue());
        }
    }
}
