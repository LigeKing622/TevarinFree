/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package dev.tevarin.utils;


import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class WorldUtil
        implements IMinecraft {

    public static boolean isAir(BlockPos pos) {
        Block block = WorldUtil.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir;
    }

    public static boolean isAirOrLiquid(BlockPos pos) {
        Block block = WorldUtil.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    public static boolean isOverAirOrLiquid() {
        return WorldUtil.isAirOrLiquid(new BlockPos(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ));
    }

    public static MovingObjectPosition raytrace(float yaw, float pitch) {
        float blockReachDistance = WorldUtil.mc.playerController.getBlockReachDistance();
        Vec3 vec3 = new Vec3(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY + (double)WorldUtil.mc.thePlayer.getEyeHeight(), WorldUtil.mc.thePlayer.posZ);
        Vec3 vec31 = WorldUtil.mc.thePlayer.getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * 1000.0, vec31.yCoord * 1000.0, vec31.zCoord * 1000.0);
        return WorldUtil.mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static MovingObjectPosition raytraceLegit(float yaw, float pitch, float lastYaw, float lastPitch) {
        float partialTicks = WorldUtil.mc.timer.renderPartialTicks;
        float blockReachDistance = WorldUtil.mc.playerController.getBlockReachDistance();
        Vec3 vec3 = WorldUtil.mc.thePlayer.getPositionEyes(partialTicks);
        float f = lastPitch + (pitch - lastPitch) * partialTicks;
        float f1 = lastYaw + (yaw - lastYaw) * partialTicks;
        Vec3 vec31 = WorldUtil.mc.thePlayer.getVectorForRotation(f, f1);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * (double)blockReachDistance, vec31.yCoord * (double)blockReachDistance, vec31.zCoord * (double)blockReachDistance);
        return WorldUtil.mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static boolean isBlockUnder() {
        if (WorldUtil.mc.thePlayer.posY < 0.0) {
            return false;
        }
        for (int offset = 0; offset < (int)WorldUtil.mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = WorldUtil.mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
            if (WorldUtil.mc.theWorld.getCollidingBoundingBoxes(WorldUtil.mc.thePlayer, bb).isEmpty()) continue;
            return true;
        }
        return false;
    }

    public static boolean isBlockUnder(int distance) {
        for (int y = (int)WorldUtil.mc.thePlayer.posY; y >= (int)WorldUtil.mc.thePlayer.posY - distance; --y) {
            if (WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, (double)y, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }

    public static boolean negativeExpand(double negativeExpandValue) {
        return WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX + negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX - negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX - negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX + negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir;
    }

    public static boolean negativeExpand(double posX, double posY, double posZ, double negativeExpandValue) {
        return WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX + negativeExpandValue, posY - 1.0, posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX - negativeExpandValue, posY - 1.0, posZ - negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX - negativeExpandValue, posY - 1.0, posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX + negativeExpandValue, posY - 1.0, posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX, posY - 1.0, posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(posX, posY - 1.0, posZ - negativeExpandValue)).getBlock() instanceof BlockAir;
    }
}

