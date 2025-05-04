package dev.tevarin.module.impl.render;

import org.lwjgl.opengl.GL11;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventAttack;
import dev.tevarin.event.impl.events.EventRender3D;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.utils.math.MathUtils;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import java.awt.*;

public class TargetESP extends Module {
    public TargetESP() {
        super("TargetESP", Category.Render);
    }

    private final ModeValue mode = new ModeValue("Mark Mode", new String[]{"Points", "Ghost", "Exhi", "Circle"}, "Points");
    private final NumberValue circleSpeed = new NumberValue("Circle Speed", 2.0F, 1.0F, 5.0F, 0.1F, () -> mode.is("Circle"));
    private final BoolValue onlyPlayer = new BoolValue("Only Player", true);
    private EntityLivingBase target;
    private final TimerUtil timerUtils = new TimerUtil();
    private final long lastTime = System.currentTimeMillis();
    private final Animation alphaAnim = new DecelerateAnimation(400, 1);
    private final ResourceLocation glowCircle = new ResourceLocation("tevarin/targetesp/glow_circle.png");
    public double prevCircleStep;
    public double circleStep;

    @EventTarget
    public void onAttack(EventAttack event) {
        if (event.getTarget() != null && (onlyPlayer.get() && event.getTarget() instanceof EntityPlayer || !onlyPlayer.get())) {
            target = (EntityLivingBase) event.getTarget();
            alphaAnim.setDirection(Direction.FORWARDS);
            timerUtils.reset();
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (timerUtils.hasTimeElapsed(100)) {
            alphaAnim.setDirection(Direction.BACKWARDS);
            if (alphaAnim.isDone())
                target = null;
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (target != null) {
            if (mode.is("Points"))
                points();

            if (mode.is("Exhi")) {
                int color = this.target.hurtTime > 3 ? new Color(200, 255, 100, 75).getRGB() : this.target.hurtTime < 3 ? new Color(235, 40, 40, 75).getRGB() : new Color(255, 255, 255, 75).getRGB();
                GlStateManager.pushMatrix();
                GL11.glShadeModel(7425);
                GL11.glHint(3154, 4354);
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                double x = target.prevPosX + (target.posX - target.prevPosX) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosX;
                double y = target.prevPosY + (target.posY - target.prevPosY) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosY;
                double z = target.prevPosZ + (target.posZ - target.prevPosZ) * (double) event.getPartialTicks() - mc.getRenderManager().renderPosZ;
                double xMoved = target.posX - target.prevPosX;
                double yMoved = target.posY - target.prevPosY;
                double zMoved = target.posZ - target.prevPosZ;
                double motionX = 0.0;
                double motionY = 0.0;
                double motionZ = 0.0;
                GlStateManager.translate(x + (xMoved + motionX + (mc.thePlayer.motionX + 0.005)), y + (yMoved + motionY + (mc.thePlayer.motionY - 0.002)), z + (zMoved + motionZ + (mc.thePlayer.motionZ + 0.005)));
                AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
                RenderUtil.drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX - 0.1 - target.posX, axisAlignedBB.minY - 0.1 - target.posY, axisAlignedBB.minZ - 0.1 - target.posZ, axisAlignedBB.maxX + 0.1 - target.posX, axisAlignedBB.maxY + 0.2 - target.posY, axisAlignedBB.maxZ + 0.1 - target.posZ), true, color);
                GlStateManager.popMatrix();
            }

            if (mode.is("Ghost")) {
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableCull();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(770, 1, 0, 1);
                double radius = 0.67;
                float speed = 45;
                float size = 0.4f;
                double distance = 19;
                int lenght = 20;

                Vec3 interpolated = MathUtils.interpolate(new Vec3((Double) target.lastTickPosX, (Double) target.lastTickPosY, (Double) target.lastTickPosZ), target.getPositionVector(), event.getPartialTicks());
                interpolated.yCoord += 0.75f;

                RenderUtil.setupOrientationMatrix(interpolated.xCoord, interpolated.yCoord + 0.5f, interpolated.zCoord);

                float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(s, (c), -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = getModule(HUD.class).color(i).getRGB();
                    RenderUtil.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate(-(s), -(c), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-s, s, -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = getModule(HUD.class).color(i).getRGB();
                    RenderUtil.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), -(s), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-(s), -(s), (c));
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = getModule(HUD.class).color(i).getRGB();
                    RenderUtil.drawImage(glowCircle, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), (s), -(c));
                }
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
                GlStateManager.enableAlpha();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
            }

            if (mode.is("Circle")) {
                prevCircleStep = circleStep;
                circleStep += (double) this.circleSpeed.get() * RenderUtil.deltaTime;
                float eyeHeight = target.getEyeHeight();
                if (target.isSneaking()) {
                    eyeHeight -= 0.2F;
                }

                double cs = prevCircleStep + (circleStep - prevCircleStep) * (double) mc.timer.renderPartialTicks;
                double prevSinAnim = Math.abs(1.0D + Math.sin(cs - 0.5D)) / 2.0D;
                double sinAnim = Math.abs(1.0D + Math.sin(cs)) / 2.0D;
                double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY + prevSinAnim * (double) eyeHeight;
                double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                double nextY = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY + sinAnim * (double) eyeHeight;
                GL11.glPushMatrix();
                GL11.glDisable(2884);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                GL11.glDisable(3008);
                GL11.glShadeModel(7425);
                GL11.glBegin(8);

                int i;
                Color color;
                for (i = 0; i <= 360; ++i) {
                    color = new Color(getModule(HUD.class).color(i).getRGB());
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.6F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians((double) i)) * (double) target.width * 0.8D, nextY, z + Math.sin(Math.toRadians((double) i)) * (double) target.width * 0.8D);
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.01F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians((double) i)) * (double) target.width * 0.8D, y, z + Math.sin(Math.toRadians((double) i)) * (double) target.width * 0.8D);
                }

                GL11.glEnd();
                GL11.glEnable(2848);
                GL11.glBegin(2);

                for (i = 0; i <= 360; ++i) {
                    color = new Color(getModule(HUD.class).color(i).getRGB());
                    GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.8F);
                    GL11.glVertex3d(x + Math.cos(Math.toRadians((double) i)) * (double) target.width * 0.8D, nextY, z + Math.sin(Math.toRadians((double) i)) * (double) target.width * 0.8D);
                }

                GL11.glEnd();
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glEnable(2929);
                GL11.glShadeModel(7424);
                GL11.glDisable(3042);
                GL11.glEnable(2884);
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        }
    }


    private void points() {
        if (target != null) {
            double markerX = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosX, target.posX);
            double markerY = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosY, target.posY) + target.height / 1.6f;
            double markerZ = MathUtils.interporate(mc.timer.renderPartialTicks, target.lastTickPosZ, target.posZ);
            float time = (float) ((((System.currentTimeMillis() - lastTime) / 1500F)) + (Math.sin((((System.currentTimeMillis() - lastTime) / 1500F))) / 10f));
            float alpha = ((1) * 1);
            float pl = 0;
            boolean fa = false;
            for (int iteration = 0; iteration < 3; iteration++) {
                for (float i = time * 360; i < time * 360 + 90; i += 2) {
                    float max = time * 360 + 90;
                    float dc = MathUtils.normalize(i, time * 360 - 45, max);
                    float rf = 0.6f;
                    double radians = Math.toRadians(i);
                    double plY = pl + Math.sin(radians * 1.2f) * 0.1f;
                    int firstColor = ColorUtil.applyOpacity(new Color(getModule(HUD.class).color(0).getRGB()), (float) alphaAnim.getOutput()).getRGB();
                    int secondColor = ColorUtil.applyOpacity(new Color(getModule(HUD.class).color(90).getRGB()), (float) alphaAnim.getOutput()).getRGB();
                    GlStateManager.pushMatrix();
                    RenderUtil.setupOrientationMatrix(markerX, markerY, markerZ);

                    float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                    GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                    GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                    GlStateManager.depthMask(false);
                    float q = (!fa ? 0.25f : 0.15f) * (Math.max(fa ? 0.25f : 0.15f, fa ? dc : (1f + (0.4f - dc)) / 2f) + 0.45f);
                    float size = q * (2f + ((0.5f - alpha) * 2));
                    RenderUtil.drawImage(
                            glowCircle,
                            Math.cos(radians) * rf - size / 2f,
                            plY - 0.7,
                            Math.sin(radians) * rf - size / 2f, size, size,
                            firstColor,
                            secondColor,
                            secondColor,
                            firstColor);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GlStateManager.depthMask(true);
                    GlStateManager.popMatrix();
                }
                time *= -1.025f;
                fa = !fa;
                pl += 0.45f;
            }
        }
    }


}