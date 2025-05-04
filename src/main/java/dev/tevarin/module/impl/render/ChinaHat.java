package dev.tevarin.module.impl.render;


import dev.tevarin.event.annotations.EventPriority;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventRender3D;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.utils.math.MathUtils;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ColorValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;


public class ChinaHat extends Module {
    public ChinaHat() {
        super("ChinaHat", Category.Render);
    }
    private final ModeValue colorModeValue = new ModeValue("Mode", new String[]{"Astolfo","RainBow","Cherry","Custom","Purple","Blend","Matrix"},"Astolfo");

    private final BoolValue target = new BoolValue("Target", false);
    private final NumberValue sizeValue = new NumberValue("Size", 0.5, 0.0, 2.0, 0.1);
    private final NumberValue pointsValue = new NumberValue("Points", 30.0, 3.0, 180.0, 1.0);
    private final NumberValue offSetValue = new NumberValue("OffSet",2000.0, 0.0, 5000.0, 100.0);
    public final ColorValue colorValue = new ColorValue("Color",new Color(255, 255, 255).getRGB());
    public final ColorValue secondColorValue = new ColorValue("SecondColor", new Color(0, 0, 0).getRGB());
    public final ColorValue thirdColorValue = new ColorValue("ThirdColor", new Color(0, 0, 0).getRGB());
    private final double[][] pointsCache = new double[181][2];
    private int lastPoints;
    private double lastSize;
    private float yaw;
    private float prevYaw;
    private float pitch;
    private float prevPitch;
    public Entity targets;
    private final Color[] gradient = new Color[] { new Color(255, 150, 255), new Color(255, 132, 199), new Color(211, 101, 187), new Color(160, 80, 158), new Color(120, 63, 160), new Color(123, 65, 168), new Color(104, 52, 152), new Color(142, 74, 175), new Color(160, 83, 179), new Color(255, 110, 189), new Color(255, 150, 255) };
    private final Color[] cherry = new Color[] { new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(155, 155, 155), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(200, 200, 200) };
    private final Color[] rainbow = new Color[] { new Color(30, 250, 215), new Color(0, 200, 255), new Color(50, 100, 255), new Color(100, 50, 255), new Color(255, 50, 240), new Color(255, 0, 0), new Color(255, 150, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(80, 240, 155) };
    private final Color[] astolfo= new Color[] { new Color(252, 106, 140), new Color(252, 106, 213), new Color(218, 106, 252), new Color(145, 106, 252), new Color(106, 140, 252), new Color(106, 213, 252), new Color(106, 213, 252), new Color(106, 140, 252), new Color(145, 106, 252), new Color(218, 106, 252), new Color(252, 106, 213), new Color(252, 106, 140) };
    private final Color[] metrix= new Color[] { new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)), new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)) };
    @EventTarget()
    public void onPreMotion(EventMotion event) {
        if (event.isPre()) {
            if (isNull()) return;
            this.yaw = event.getYaw();
            this.prevYaw = event.getPrevYaw();
            this.pitch = event.getPitch();
            this.prevPitch = event.getPrevPitch();
        }
        ;
    }
    @EventTarget()
    @EventPriority(10)
    public void onRender3D(EventRender3D event){
        if (isNull()) return;
        targets = getModule(KillAura.class).target;
        final Iterator<EntityPlayer> iterator = getLoadedPlayers().iterator();
        if (this.lastSize != this.sizeValue.getValue().doubleValue() || this.lastPoints != this.pointsValue.getValue().intValue()) {
            this.lastSize = this.sizeValue.getValue().doubleValue();
            this.genPoints(this.lastPoints = this.pointsValue.getValue().intValue(), this.lastSize);
        }
        while (iterator.hasNext()) {
            final EntityPlayer entity = iterator.next();
            if (entity == ChinaHat.mc.thePlayer && ChinaHat.mc.gameSettings.thirdPersonView != 0) {
                this.drawHat(event, entity);
            }
            if (this.target.getValue() && entity == targets) {
                this.drawHat(event, entity);
            }
        }
    };
    public static java.util.List<EntityPlayer> getLoadedPlayers() {
        return ChinaHat.mc.theWorld.playerEntities;
    }

    private void drawHat(final EventRender3D event, final EntityLivingBase entity) {
        final boolean isPlayerSP = entity.isEntityEqual(ChinaHat.mc.thePlayer);
        if (ChinaHat.mc.gameSettings.thirdPersonView == 0 && isPlayerSP ) {
            return;
        }
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        final Color[] colors = new Color[181];
        Color[] colorMode;
        switch (this.colorModeValue.getValue()) {
            case "Purple": {
                colorMode = this.gradient;
                break;
            }
            case "Astolfo": {
                colorMode = this.astolfo;
                break;
            }
            case "Cherry": {
                colorMode = this.cherry;
                break;
            }
            case "Matrix": {
                colorMode = this.metrix;
                break;
            }
            case "Custom": {
                colorMode = new Color[] { new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()).darker(), new Color(this.colorValue.getValue()).darker().darker(), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()).darker(), new Color(this.colorValue.getValue()).darker().darker(), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()).darker(), new Color(this.colorValue.getValue()).darker().darker(), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()) };
                break;
            }
            case "Blend": {
                colorMode = new Color[] { new Color(this.colorValue.getValue()).darker().darker(), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()), new Color(this.colorValue.getValue()).darker().darker(), new Color(this.secondColorValue.getValue()).darker().darker(), new Color(this.secondColorValue.getValue()), new Color(this.secondColorValue.getValue()), new Color(this.secondColorValue.getValue()), new Color(this.secondColorValue.getValue()).darker().darker(), new Color(this.thirdColorValue.getValue()).darker().darker(), new Color(this.thirdColorValue.getValue()), new Color(this.thirdColorValue.getValue()), new Color(this.thirdColorValue.getValue()), new Color(this.thirdColorValue.getValue()).darker().darker() };
                break;
            }
            default: {
                colorMode = this.rainbow;
                break;
            }
        }
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = ((this.colorModeValue.getValue() == colorModeValue.get()) ? this.fadeBetween(colorMode, 6000.0, i * (6000.0 / this.pointsValue.getValue().doubleValue())) : this.fadeBetween(colorMode, (double)this.offSetValue.getValue().longValue(), i * (this.offSetValue.getValue().floatValue() / this.pointsValue.getValue().doubleValue())));
        }
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 1.9, z);
        if (entity.isSneaking()) {
            GL11.glTranslated(0.0, -0.2, 0.0);
        }
        GL11.glRotatef((float) MathUtils.interpolate(this.prevYaw, this.yaw, event.getPartialTicks()), 0.0f, -1.0f, 0.0f);
        final float interpolate = (float) MathUtils.interpolate(this.prevPitch,this.pitch, event.getPartialTicks());
        GL11.glRotatef(interpolate / 3.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslated(0.0, 0.0, interpolate / 270.0f);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(2);
        this.drawCircle(this.pointsValue.getValue().intValue() - 1, colors, 255);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glBegin(6);
        GL11.glVertex3d(0.0, this.sizeValue.getValue().doubleValue() / 2.0, 0.0);
        this.drawCircle(this.pointsValue.getValue().intValue(), colors, 85);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glShadeModel(7424);
        GL11.glEnable(2929);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
    }

    private void drawCircle(final int points, final Color[] colors, final int alpha) {
        for (int i = 0; i <= points; ++i) {
            final double[] point = this.pointsCache[i];
            final Color clr = colors[i];
            GL11.glColor4f(clr.getRed() / 255.0f, clr.getGreen() / 255.0f, clr.getBlue() / 255.0f, alpha / 255.0f);
            GL11.glVertex3d(point[0], 0.0, point[1]);
        }
    }

    private void genPoints(final int points, final double size) {
        for (int i = 0; i <= points; ++i) {
            final double cos = size * StrictMath.cos(i * 3.141592653589793 * 2.0 / points);
            final double sin = size * StrictMath.sin(i * 3.141592653589793 * 2.0 / points);
            this.pointsCache[i][0] = cos;
            this.pointsCache[i][1] = sin;
        }
    }

    public Color fadeBetween(final Color[] table, final double progress) {
        final int i = table.length;
        if (progress == 1.0) {
            return table[0];
        }
        if (progress == 0.0) {
            return table[i - 1];
        }
        final double max = Math.max(0.0, (1.0 - progress) * (i - 1));
        final int min = (int)max;
        return this.fadeBetween(table[min], table[min + 1], max - min);
    }

    public Color fadeBetween(final Color start, final Color end, double progress) {
        if (progress > 1.0) {
            progress = 1.0 - progress % 1.0;
        }
        return this.gradient(start, end, progress);
    }

    public Color gradient(final Color start, final Color end, final double progress) {
        final double invert = 1.0 - progress;
        return new Color((int)(start.getRed() * invert + end.getRed() * progress), (int)(start.getGreen() * invert + end.getGreen() * progress), (int)(start.getBlue() * invert + end.getBlue() * progress), (int)(start.getAlpha() * invert + end.getAlpha() * progress));
    }

    public Color fadeBetween(final Color[] table, final double speed, final double offset) {
        return this.fadeBetween(table, (System.currentTimeMillis() + offset) % speed / speed);
    }
}
