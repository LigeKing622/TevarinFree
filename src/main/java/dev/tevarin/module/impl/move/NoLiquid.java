package dev.tevarin.module.impl.move;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventWorldLoad;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.value.impl.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoLiquid extends Module {
    private final ModeValue modeValue = new ModeValue("Mode", new String[]{"Vanilla", "Grim"}, "Grim");
    public static boolean shouldCancelWater;


    public NoLiquid() {
        super("NoLiquid", Category.Movement);
    }

    @Override
    public void onDisable() {
        shouldCancelWater = false;
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        shouldCancelWater = false;
    }

   @EventTarget
   public void onUpdate(EventMotion e) {
            if (this.modeValue.is("Grim")) {
                   for (int i = -3; i < 3; i++) {
                         for (int i2 = -12; i2 < 12; i2++) {
                               BlockPos playerPos = new BlockPos((Entity)mc.thePlayer);
                               BlockPos[] blockPoses = { playerPos.add(i2, i, 7), playerPos.add(i2, i, -7), playerPos.add(7, i, i2), playerPos.add(-7, i, i2) };
                               for (BlockPos blockPos : blockPoses) {
                                     IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                                     Block block = blockState.getBlock();

                                    if (block instanceof net.minecraft.block.BlockLiquid) {
                                           mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                           mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                           mc.theWorld.setBlockToAir(blockPos);
                                     }
                               }
                         }
                   }
             }
    }
}

