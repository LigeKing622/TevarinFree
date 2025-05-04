package dev.tevarin.ui.hud.impl;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.StopWatch;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.utils.math.MathUtils;
import dev.tevarin.utils.render.*;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.AnimationUtils;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.ContinualAnimation;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.utils.render.shader.KawaseBloom;
import dev.tevarin.utils.render.shader.KawaseBlur;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.tevarin.module.impl.render.HUD.color;

public class TargetHUD extends HUD {
    public TargetHUD() {
        super(150, 100, "TargetHUD");
    }

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    private boolean sentParticles;
    public ModeValue mode = new ModeValue("Mode", new String[]{  "Exhibition"}, "Exhibition");
    public final List<Particle> particles = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    public static boolean inWorld;
    private NumberValue bgAlpha = new NumberValue("Background Alpha", 100.0, 0.0, 255.0, 1.0, () -> mode.is("Novoline Fky"));
    private final ContinualAnimation animation2 = new ContinualAnimation();
    private final Animation openAnimation = new DecelerateAnimation(175, .5);
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");
    public StopWatch stopwatch = new StopWatch();
    private EntityLivingBase target;

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {

    }
    @EventTarget()
    public void onPreMotionEvent(EventMotion event ) {
        if (event.isPre()) {
//        target = mc.thePlayer;

            if (mc.currentScreen instanceof GuiChat) {
                stopwatch.reset();
                target = mc.thePlayer;
            }

            if (target == null) {
                inWorld = false;
                return;
            }
            inWorld = mc.theWorld.loadedEntityList.contains(target);
        }
        ;
    }
    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        GlStateManager.pushMatrix();
        if (!mode.is("Raven")) RenderUtil.scaleStart(xPos + getWidth() / 2f, yPos + getHeight() / 2f,
                (float) (.5 + openAnimation.getOutput()));
        float alpha = (float) Math.min(1, openAnimation.getOutput() * 2);
        if (target != null) {
            render(xPos, yPos, alpha, target, false);
        }
        if (!mode.is("Raven")) RenderUtil.scaleEnd();
        GlStateManager.popMatrix();
    }

    @Override
    public void predrawhud() {

        KillAura killAura = Client.instance.moduleManager.getModule(KillAura.class);

        if (!(mc.currentScreen instanceof GuiChat)) {
            if (!killAura.getState()) {
                openAnimation.setDirection(Direction.BACKWARDS);
            }

            if (target == null && KillAura.target != null) {
                target = KillAura.target;
                openAnimation.setDirection(Direction.FORWARDS);

            } else if (KillAura.target == null /*|| target != KillAura.target*/) {
                openAnimation.setDirection(Direction.BACKWARDS);
            } else if (target != KillAura.target) {
                target = KillAura.target;
            }

            if (openAnimation.finished(Direction.BACKWARDS)) {
                target = null;
            }
        } else {
            openAnimation.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
        }

    }


    public void render(float x, float y, float alpha, EntityLivingBase target, boolean blur) {
        GlStateManager.pushMatrix();
        switch (mode.getValue().toLowerCase()) {
            case "exhibition": {
                GlStateManager.pushMatrix();
                this.width = (int) (mc.fontRendererObj.getStringWidth(target.getName()) > 70.0f ? (double) (125.0f + mc.fontRendererObj.getStringWidth(target.getName()) - 70.0f) : 125.0);
                this.height = 45;
                GlStateManager.translate(x, y + 6, 0.0f);
                RenderUtil.skeetRect(0, -2.0, mc.fontRendererObj.getStringWidth(target.getName()) > 70.0f ? (double) (124.0f + mc.fontRendererObj.getStringWidth(target.getName()) - 70.0f) : 124.0, 38.0, 1.0);
                RenderUtil.skeetRectSmall(0.0f, -2.0f, 124.0f, 38.0f, 1.0);
                mc.fontRendererObj.drawStringWithShadow(target.getName(), 43f, 0.3f, -1);
                final float health = target.getHealth();
                final float healthWithAbsorption = target.getHealth() + target.getAbsorptionAmount();
                final float progress = health / target.getMaxHealth();
                final Color healthColor = health >= 0.0f ? ColorUtil.getBlendColor(target.getHealth(), target.getMaxHealth()).brighter() : Color.RED;
                double cockWidth = 0.0;
                cockWidth = MathUtils.round(cockWidth, (int) 5.0);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                final double healthBarPos = cockWidth * (double) progress;
                Gui.drawRect(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
                if (target.getAbsorptionAmount() > 0.0f) {
                    Gui.drawRect(97.5 - (double) target.getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
                }
                RenderUtil.drawBorderedRect2(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5f, 0, Color.BLACK.getRGB());
                for (int dist = 1; dist < 10; ++dist) {
                    final double cock = cockWidth / 8.5 * (double) dist;
                    Gui.drawRect(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
                }
                GlStateManager.scale(0.5, 0.5, 0.5);
                final int distance = (int) mc.thePlayer.getDistanceToEntity(target);
                final String nice = "HP: " + (int) healthWithAbsorption + " | Dist: " + distance;
                mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
                GlStateManager.scale(2.0, 2.0, 2.0);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                if (target != null) drawEquippedShit(28, 20, target);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.scale(0.31, 0.31, 0.31);
                GlStateManager.translate(73.0f, 102.0f, 40.0f);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                drawModel(target.rotationYaw, target.rotationPitch, target);
                GlStateManager.popMatrix();
                break;
            }

        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static class Particle {
        public float x, y, adjustedX, adjustedY, deltaX, deltaY, size, opacity;
        public Color color;

        public void render2D() {
            RoundedUtil.drawRound(x + adjustedX, y + adjustedY, size, size, (size / 2f) - .5f, ColorUtil.applyOpacity(color, opacity / 255f));
        }

        public void updatePosition() {
            for (int i = 1; i <= 2; i++) {
                adjustedX += deltaX;
                adjustedY += deltaY;
                deltaY *= 0.97;
                deltaX *= 0.97;
                opacity -= 1f;
                if (opacity < 1) opacity = 1;
            }
        }

        public void init(float x, float y, float deltaX, float deltaY, float size, Color color) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.size = size;
            this.opacity = 254;
            this.color = color;
        }
    }

    public static void drawEquippedShit(final int x, final int y, final EntityLivingBase target) {
        if (!(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            final ItemStack armor = target.getCurrentArmor(geraltOfNigeria);
            if (armor != null) {
                stuff.add(armor);
            }
        }
        if (target.getHeldItem() != null) {
            stuff.add(target.getHeldItem());
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    protected void renderPlayer2D(float x, float y, float width, float height, AbstractClientPlayer player) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);
        GLUtil.endBlend();
    }

    public void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    // 构建颜色代码到颜色名称的映射
    private static final Map<Character, String> COLOR_CODE_MAP = new HashMap<>();

    public static String detectColorInitials(String text) {
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == '§') {
                char code = text.charAt(i + 1);
                String colorName = COLOR_CODE_MAP.get(code);
                if (colorName != null) {
                    // 提取颜色名称首字母并转为大写添加到结果中
                    initials.append(Character.toUpperCase(colorName.charAt(0)));
                }
            }
        }
        return initials.toString();

    }


    // 颜色代码映射表，将颜色代码字符映射到对应的颜色名称

    static {
        COLOR_CODE_MAP.put('0', "黑色");
        COLOR_CODE_MAP.put('1', "蓝色");
        COLOR_CODE_MAP.put('2', "深绿色");
        COLOR_CODE_MAP.put('3', "湖蓝色");
        COLOR_CODE_MAP.put('4', "深红色");
        COLOR_CODE_MAP.put('5', "紫色");
        COLOR_CODE_MAP.put('6', "金色");
        COLOR_CODE_MAP.put('7', "灰色");
        COLOR_CODE_MAP.put('8', "深灰色");
        COLOR_CODE_MAP.put('9', "蓝色");
        COLOR_CODE_MAP.put('a', "绿色");
        COLOR_CODE_MAP.put('b', "天蓝色");
        COLOR_CODE_MAP.put('c', "红色");
        COLOR_CODE_MAP.put('d', "粉红色");
        COLOR_CODE_MAP.put('e', "黄色");
        COLOR_CODE_MAP.put('f', "白色");
    }

    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20.0;
        return Math.round(bps * 100.0) / 10.0;
    }
}
