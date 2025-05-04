package dev.tevarin.utils;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ItemTagUtli implements IMinecraft {
    public static int overrideOutlineColor = 0x5000FF;
    public static int maxDistance = 8;
    public static int colorBackground = 0x100010;
    public static float alpha = 0.8F;
    public static boolean hideModName, overrideOutline;

    public static void start() {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void end() {
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    public static void renderTooltipTile(int x, int y, int w, int h, int colorPrimary, int colorOutline, int colorSecondary) {
        drawGradientRect(x - 3, y - 4, w + 6, 1, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y + h + 3, w + 6, 1, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y - 3, w + 6, h + 6, colorPrimary, colorPrimary);
        drawGradientRect(x - 4, y - 3, 1, h + 6, colorPrimary, colorPrimary);
        drawGradientRect(x + w + 3, y - 3, 1, h + 6, colorPrimary, colorPrimary);
        drawGradientRect(x - 3, y - 2, 1, h + 4, colorOutline, colorSecondary);
        drawGradientRect(x + w + 2, y - 2, 1, h + 4, colorOutline, colorSecondary);
        drawGradientRect(x - 3, y - 3, w + 6, 1, colorOutline, colorOutline);
        drawGradientRect(x - 3, y + h + 2, w + 6, 1, colorSecondary, colorSecondary);
    }

    public static void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
        w += x;
        h += y;
        float a1 = (float) (color1 >> 24 & 255) / 255.0F;
        float r1 = (float) (color1 >> 16 & 255) / 255.0F;
        float g1 = (float) (color1 >> 8 & 255) / 255.0F;
        float b1 = (float) (color1 >> 0 & 255) / 255.0F;
        float a2 = (float) (color2 >> 24 & 255) / 255.0F;
        float r2 = (float) (color2 >> 16 & 255) / 255.0F;
        float g2 = (float) (color2 >> 8 & 255) / 255.0F;
        float b2 = (float) (color2 >> 0 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer vertexBuffer = tessellator.getWorldRenderer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(w, y, 0.0D).color(r1, g1, b1, a1).endVertex();
        vertexBuffer.pos(x, y, 0.0D).color(r1, g1, b1, a1).endVertex();
        vertexBuffer.pos(x, h, 0.0D).color(r2, g2, b2, a2).endVertex();
        vertexBuffer.pos(w, h, 0.0D).color(r2, g2, b2, a2).endVertex();
        vertexBuffer.endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
