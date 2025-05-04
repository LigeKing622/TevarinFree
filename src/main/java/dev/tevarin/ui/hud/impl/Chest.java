package dev.tevarin.ui.hud.impl;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.PacketSendEvent;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.utils.vector.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.lwjgl.compatibility.util.glu.GLU;
import org.lwjglx.opengl.Display;

import javax.vecmath.Vector4d;
import java.awt.*;

public class Chest extends HUD {

    public Chest() {
        super(200, 100, "Chest");
    }

    @Override
    public void drawShader() {

    }

    @Override
    public void predrawhud() {

    }

    @Override
    public void onTick() {

    }

    private BlockPos currentContainerPos;

    public ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    @EventTarget
    public void onSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof C08PacketPlayerBlockPlacement wrapper) {
            if (wrapper.getPosition() != null) {
                Block block = mc.theWorld.getBlockState(wrapper.getPosition()).getBlock();
                if (block instanceof BlockContainer) {
                    currentContainerPos = wrapper.getPosition();
                }

            }
        }
    }

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        if (mc.thePlayer.openContainer == null || mc.currentScreen == null) return;
        Container container = mc.thePlayer.openContainer;
        if (!(container instanceof ContainerChest || container instanceof ContainerFurnace || container instanceof ContainerBeacon || container instanceof ContainerDispenser || container instanceof ContainerHopper || container instanceof ContainerHorseInventory || container instanceof ContainerBrewingStand)) {
            return;
        }
        int slots = container.inventorySlots.size();

        int scaleFactor = this.getScaledResolution().getScaleFactor();

        if (slots > 0) {
            Vector4d projection = calculate(currentContainerPos, scaleFactor);
            if (projection == null) return;

            float roundX = (float) projection.x - (164 / 2F);
            float roundY = (float) projection.y / 1.5F;

            GlStateManager.pushMatrix();
            GlStateManager.translate(roundX + 82, roundY + 30, 0);
            GlStateManager.translate(-(roundX + 82), -(roundY + 30), 0);
            //Glow
            ShaderElement.addBloomTask(() -> RoundedUtil.drawRound(roundX, roundY, 164, 60, 5, new Color(0, 0, 0, 255)));
            ShaderElement.addBlurTask(() -> RoundedUtil.drawRound(roundX, roundY, 164, 60, 5, Color.WHITE));
            RoundedUtil.drawRound(roundX, roundY, 164, 60, 3, new Color(0, 0, 0, 100));
            double startX = roundX + 5;
            double startY = roundY + 5;

            RenderItem itemRender = mc.getRenderItem();

            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.zLevel = 200.0F;

            for (Slot slot : container.inventorySlots) {
                if (!slot.inventory.equals(mc.thePlayer.inventory)) {
                    int x = (int) (startX + (slot.slotNumber % 9) * 18);
                    int y = (int) (startY + ((double) slot.slotNumber / 9) * 18);

                    itemRender.renderItemAndEffectIntoGUI(slot.getStack(), x, y);
                }
            }
            GlStateManager.popMatrix();

            itemRender.zLevel = 0.0F;
            GlStateManager.popMatrix();
            GlStateManager.disableLighting();
        }
    }

    private static Vector3d project(double x, double y, double z, int factor) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new Vector3d((ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2));
        }
        return null;
    }

    public Vector4d calculate(BlockPos blockPos, int factor) {
        try {
            double renderX = RenderManager.getRenderPosX();
            double renderY = RenderManager.getRenderPosY();
            double renderZ = RenderManager.getRenderPosZ();

            double x = blockPos.getX() + 0.5 - renderX;
            double y = blockPos.getY() + 0.5 - renderY;
            double z = blockPos.getZ() + 0.5 - renderZ;

            Vector3d projectedCenter = project(x, y, z, factor);
            if (projectedCenter != null && projectedCenter.getZ() >= 0.0D && projectedCenter.getZ() < 1.0D) {
                return new Vector4d(projectedCenter.getX(), projectedCenter.getY(), projectedCenter.getX(), projectedCenter.getY());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}


