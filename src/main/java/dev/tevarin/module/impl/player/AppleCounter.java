package dev.tevarin.module.impl.player;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.DebugUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AppleCounter extends Module {
    int goldenApplesCount = 0;
    public AppleCounter() {
        super("AppleCounter", Category.Player);
    }
      @EventTarget
        public void onTick(EventMotion event){
          DebugUtil.print(goldenApplesCount);

          // 遍历目标玩家的所有物品槽位
          for (int i = 0; i < 9; i++) {
              ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
              if (stack != null  && stack.getItem() == Items.golden_apple) {
                  goldenApplesCount += stack.getMetadata();
              }
          }


    }

}