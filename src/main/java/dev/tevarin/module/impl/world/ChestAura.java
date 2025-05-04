package dev.tevarin.module.impl.world;


import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.*;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.Gapple;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.module.impl.player.Blink;
import dev.tevarin.module.impl.player.ChestStealer;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.utils.MSTimer;
import dev.tevarin.utils.PacketUtil;
import dev.tevarin.utils.StopWatch;
import dev.tevarin.utils.player.RaytraceUtil;
import dev.tevarin.utils.player.Rotation;
import dev.tevarin.utils.player.RotationUtil;
import dev.tevarin.utils.player.TimeHelper;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import org.lwjglx.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class ChestAura extends Module {
    private final NumberValue range = new NumberValue("Range", 3.0, 1.0, 7.0, 0.1);
    public static BoolValue interactOnce = new BoolValue("InteractOnce", false);
    public TimeHelper waitBoxOpenTimer = new TimeHelper();
    public static boolean isWaitingOpen = false;
    private BlockPos globalPos;
    private TileEntityEnderChest associatedChest;
    private BlockPos openingPos;
    public static List<BlockPos> list = new ArrayList<>();
    private boolean a = false;

    public ChestAura() {
        super("ChestAura", Category.World);
    }


    @Override
    public void onDisable() {
        list.clear();
        a = false;
    }

    @EventTarget
    public void onPre(EventMotion e) {
        if (e.isPre()) {

            if (isGapple() || !getModule(ChestStealer.class).state) return;
            if (getModule(Scaffold.class).state) return;
            float radius;
            this.globalPos = null;
            if (mc.thePlayer.ticksExisted % 20 == 0 || KillAura.target != null|| mc.currentScreen instanceof GuiContainer || getModule(Scaffold.class).state || getModule(Blink.class).state || isFood()) {
                return;
            }

            for (float y = radius = this.range.getValue().floatValue(); y >= -radius; y -= 1.0f) {
                for (float x = -radius; x <= radius; x += 1.0f) {
                    for (float z = -radius; z <= radius; z += 1.0f) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX - 0.5 + (double) x, mc.thePlayer.posY - 0.5 + (double) y, mc.thePlayer.posZ - 0.5 + (double) z);
                        Block block = mc.theWorld.getBlockState(pos).getBlock();
                        BlockPos targetPos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                        if (!(mc.thePlayer.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < (double) mc.playerController.getBlockReachDistance()) || !(block instanceof BlockChest) || list.contains(pos))
                            continue;
                        float[] rotations = RotationUtil.getBlockRotations(pos.getX(), pos.getY() - 1, pos.getZ());
                        if (RaytraceUtil.overBlock(new Vector2f(rotations[0], rotations[1]), mc.objectMouseOver.sideHit, pos, false)) {
                            Client.instance.getRotationManager().setRotation(new Vector2f(rotations[0], rotations[1]), 180, true, false);
                            this.globalPos = pos;
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPost(EventMotion event) {
        if (event.isPost()){
            if (isGapple() || !getModule(ChestStealer.class).state) return;
            if (getModule(Scaffold.class).state) return;
            if (isWaitingOpen) {
                if (this.waitBoxOpenTimer.isDelayComplete(600.0)) {
                    isWaitingOpen = false;
                } else if (this.openingPos != null && mc.thePlayer.openContainer instanceof ContainerChest) {
                    list.add(this.openingPos);
                    this.openingPos = null;
                    isWaitingOpen = false;
                }
            }
        }
    }
    @EventTarget
    public void onPlace(EventPlace event) {
        if (isGapple() || !getModule(ChestStealer.class).state) return;
        if (getModule(Scaffold.class).state) return;
        if (!(this.globalPos == null || mc.currentScreen instanceof GuiContainer || list.size() >= 50 || isWaitingOpen || list.contains(this.globalPos))) {
            if (RaytraceUtil.overBlock(Client.instance.getRotationManager().rotation, mc.objectMouseOver.sideHit, globalPos, false)) {
                this.sendClick(this.globalPos);
                PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                event.setShouldRightClick(false);
            }
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad e) {
        list.clear();
    }

    public void sendClick(BlockPos pos) {
        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, (double) pos.getY() + 0.5 < mc.thePlayer.posY + 1.7 ? 1 : 0, mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
        mc.thePlayer.sendQueue.addToSendQueue(packet);
        this.waitBoxOpenTimer.reset();
        isWaitingOpen = true;
        this.openingPos = this.globalPos;
        if (interactOnce.get()) {
            list.add(pos);
        }
    }

    @EventTarget
    public void SB(EventRender3D event) {
        render();

    }
    private boolean isChest(BlockPos pos) {
        // fuck chest
        TileEntity entity = mc.theWorld.getTileEntity(pos);
        return entity instanceof TileEntityChest;
    }
    public static void render() {
        for (BlockPos pos : mc.theWorld.loadedTileEntityList.stream()
                .filter(e -> e instanceof IInventory)
                .map(TileEntity::getPos)
                .collect(Collectors.toList())) {

            Color color = list.contains(pos) ? new Color(253, 49, 22, 140) : new Color(218, 215, 18, 132);

            double x = pos.getX() - mc.getRenderManager().viewerPosX;
            double y = pos.getY() - mc.getRenderManager().viewerPosY;
            double z = pos.getZ() - mc.getRenderManager().viewerPosZ;

            double sizeX = 1.05, sizeY = 1.05, sizeZ = 1.05;

            //RenderUtils.renderBlock(pos, color.getRGB(), false, false);
//           drawFilledBox(x, y, z, sizeX, sizeY, sizeZ, color);
            if (mc.theWorld.getBlockState(pos).getBlock() != Blocks.chest) {
                RenderUtil.drawBlockBox(pos, color, false);
                RenderUtil.renderOne();
                RenderUtil.drawBlockBox(pos, color, false);
                RenderUtil.renderTwo();
                RenderUtil.drawBlockBox(pos, color, false);
                RenderUtil.renderThree();
                RenderUtil.renderFour(color.getRGB());

                RenderUtil.drawBlockBox(pos, color, true);
                RenderUtil.renderFive();
            }
            if (mc.theWorld.getBlockState(pos).getBlock() != Blocks.furnace) {
                RenderUtil.drawBlockBox(pos, color, false);
            }
        }
    }
}
