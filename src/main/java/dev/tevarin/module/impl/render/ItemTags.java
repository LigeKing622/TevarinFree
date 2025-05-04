package dev.Ethereal.module.impl.render;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventRender3D;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.ItemTagUtli;
import dev.tevarin.value.impl.NumberValue;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ItemTags extends Module {
    private static final Map<String, String> itemIdToModName = new HashMap<>();
    int colorBackground, overrideOutlineColor, alpha;
    List<String> text = new ArrayList<>();
    boolean overrideOutline;
    @Getter
    EntityItem entity;
    @Getter
    int width, height;
    NumberValue size = new NumberValue("Size", 1.0, 0.0, 10.0, 0.1);

    public ItemTags() {
        super("ItemTags", Category.Render);
    }

    public int size() {
        return text.size();
    }

    public String getLine(int line) {
        return text.get(line);
    }

    public EnumChatFormatting getRarityColor() {
        return entity.getEntityItem().getRarity().rarityColor;
    }

    @EventTarget
    public void onRender3DEvent(EventRender3D event) {
        entity = getMouseOver(mc, event.getTicks());
        if (!Objects.isNull(entity)) {
            GlStateManager.scale(size.getValue(),size.getValue(),size.getValue());
            syncSettings();
            generateTooltip(Minecraft.getMinecraft().thePlayer, entity.getEntityItem());
            renderTooltip3D(mc, event.getTicks());
        }
    }

    public void syncSettings() {
        overrideOutline = ItemTagUtli.overrideOutline;
        alpha = ((int) (ItemTagUtli.alpha * 255) & 0xFF) << 24;
        colorBackground = ItemTagUtli.colorBackground & 0xFFFFFF;
        overrideOutlineColor = ItemTagUtli.overrideOutlineColor & 0xFFFFFF;
    }

    private void generateTooltip(EntityPlayer player, ItemStack item) {
        text = item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        if (!ItemTagUtli.hideModName)
            text.add(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC + getModName(item.getItem()) + EnumChatFormatting.RESET);
        if (item.stackSize > 1)
            text.set(0, item.stackSize + " x " + text.get(0));
        int maxwidth = 0;
        for (int line = 0; line < text.size(); line++) {
            final int swidth = (int) Minecraft.getMinecraft().fontRendererObj.getStringWidth(getLine(line));
            if (swidth > maxwidth)
                maxwidth = swidth;
        }
        width = maxwidth;
        height = 8;
        if (size() > 1)
            height += 2 + (size() - 1) * 10;
    }

    private String getModName(Item item) {
        ResourceLocation itemResourceLocation = Item.itemRegistry.getNameForObject(item);
        if (itemResourceLocation == null)
            return null;
        String modId = itemResourceLocation.getResourceDomain();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = itemIdToModName.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            itemIdToModName.put(lowercaseModId, modName);
        }
        return modName;
    }

    public void renderTooltip3D(Minecraft mc, double partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        int outline1 = 0xFEFEFE;
        outline1 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
        double interpX = mc.getRenderManager().viewerPosX - (getEntity().posX - (getEntity().prevPosX - getEntity().posX) * partialTicks);
        double interpY = mc.getRenderManager().viewerPosY - (getEntity().posY - (getEntity().prevPosY - getEntity().posY) * partialTicks);
        double interpZ = mc.getRenderManager().viewerPosZ - (getEntity().posZ - (getEntity().prevPosZ - getEntity().posZ) * partialTicks);
        double scale = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
        scale /= sr.getScaleFactor() * 160;
        if (scale <= 0.01)
            scale = 0.01;
        ItemTagUtli.start();
        GlStateManager.translate(-interpX, -(interpY - 0.65), -interpZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY + 180, 0, 1, 0);
        GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
        GlStateManager.scale(scale, -scale, scale);
        int x = -getWidth() / 2;
        int y = -getHeight();
        GlStateManager.disableDepth();
        ItemTagUtli.renderTooltipTile(x, y, getWidth(), getHeight(), colorBackground | alpha, outline1 | alpha, outline1 | alpha);
        renderTooltipText(this, x, y, alpha);
        GlStateManager.enableDepth();
        GlStateManager.scale(1F / scale, 1F / -scale, 1F / scale);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
        GlStateManager.translate(interpX, interpY - 0.65, interpZ);
        ItemTagUtli.end();
    }

    public static EntityItem getMouseOver(Minecraft mc, float partialTicks) {
        Entity viewer = mc.getRenderViewEntity();
        mc.mcProfiler.startSection("world-tooltips");
        double distanceLook = ItemTagUtli.maxDistance;
        Vec3 eyes = viewer.getPositionEyes(partialTicks);
        Vec3 look = viewer.getLook(partialTicks);
        Vec3 eyesLook = eyes.addVector(look.xCoord * distanceLook, look.yCoord * distanceLook, look.zCoord * distanceLook);
        float distanceMax = 1;
        List<EntityItem> entityList = mc.theWorld.getEntitiesWithinAABB(EntityItem.class,
                viewer.getEntityBoundingBox().addCoord(look.xCoord * distanceLook, look.yCoord * distanceLook, look.zCoord * distanceLook).expand(distanceMax, distanceMax, distanceMax));
        double difference = 0;
        EntityItem target = null;
        for (EntityItem entity : entityList) {
            float boundSize = 0.15F;
            AxisAlignedBB aabb1 = entity.getEntityBoundingBox();
            AxisAlignedBB aabb2 = new AxisAlignedBB(aabb1.minX, aabb1.minY, aabb1.minZ, aabb1.maxX, aabb1.maxY, aabb1.maxZ);
            AxisAlignedBB expandedAABB = aabb2.offset(0, 0.25, 0).expand(0.15, 0.1, 0.15).expand(boundSize, boundSize, boundSize);
            MovingObjectPosition objectInVector = expandedAABB.calculateIntercept(eyes, eyesLook);
            if (expandedAABB.isVecInside(eyes)) {
                if (0.0D <= difference) {
                    target = entity;
                    difference = 0;
                }
            } else if (objectInVector != null) {
                final double distance = eyes.distanceTo(objectInVector.hitVec);
                if (distance < difference || difference == 0.0D) {
                    target = entity;
                    difference = distance;
                }
            }
        }
        mc.mcProfiler.endSection();
        return target;
    }

    public static void renderTooltipText(ItemTags tooltip, int drawx, int drawy, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        for (int i = 0; i < tooltip.size(); i++) {
            String s = tooltip.getLine(i);
            if (i == 0)
                s = tooltip.getRarityColor() + s;
            Minecraft.getMinecraft().fontRendererObj.drawString(s, drawx, drawy, 0xFFFFFF | alpha, true);
            if (i == 0)
                drawy += 2;
            drawy += 10;
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}