package dev.tevarin.ui.hud.impl;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.PacketSendEvent;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.shader.ShaderElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Armor extends HUD {

    public Armor() {
        super(80, 33, "Armor");
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

        for (int i = 3; i >= 0; i--) {
            ItemStack armorStack = mc.thePlayer.inventory.armorInventory[i];
            if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {

                drawItemStack(armorStack, (int) xPos + 2, (int) yPos + 15);

                xPos += 20;

            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, null);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }


}


