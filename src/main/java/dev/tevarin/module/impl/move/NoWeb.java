package dev.tevarin.module.impl.move;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMove;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.BlockUtil;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.value.impl.BoolValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class NoWeb extends Module {
       public final BoolValue Web = new BoolValue("Web", Boolean.valueOf(true));
       public final BoolValue Liquiuds = new BoolValue("Liquids", Boolean.valueOf(false));

    public NoWeb() {
        super("NoWeb", Category.Movement);
    }

       @EventTarget
       public void onMove(EventMove event) {
            if (((Boolean)this.Liquiuds.getValue()).booleanValue() || ((Boolean)this.Web.getValue()).booleanValue())
                   for (int i = -3; i < 3; i++) {
                       for (int i2 = -12; i2 < 12; i2++) {
                           BlockPos playerPos = new BlockPos((Entity) mc.thePlayer);
                           BlockPos[] blockPoses = {playerPos.add(i2, i, 7), playerPos.add(i2, i, -7), playerPos.add(7, i, i2), playerPos.add(-7, i, i2)};
                           BlockPos[] var6 = blockPoses;
                           int var7 = blockPoses.length;

                           for (int var8 = 0; var8 < var7; var8++) {
                               BlockPos blockPos = var6[var8];
                               IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                               Block block = blockState.getBlock();
                               if (block instanceof net.minecraft.block.BlockLiquid && ((Boolean) this.Liquiuds.getValue()).booleanValue()) {
                                   mc.getNetHandler().addToSendQueue((Packet) new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                   mc.getNetHandler().addToSendQueue((Packet) new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                                   mc.theWorld.setBlockToAir(blockPos);
                               }

                                   Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                                   for (Map.Entry<BlockPos, Block> block2 : searchBlock.entrySet()) {
                                       if (mc.theWorld.getBlockState(block2.getKey()).getBlock() instanceof BlockWeb && ((Boolean) this.Web.getValue()).booleanValue()) {
                                           PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block2.getKey(), EnumFacing.DOWN));
                                           PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block2.getKey(), EnumFacing.DOWN));
                                       }
                                   }
                                   mc.thePlayer.isInWeb = false;
                           }
                       }
                   }
    }
}