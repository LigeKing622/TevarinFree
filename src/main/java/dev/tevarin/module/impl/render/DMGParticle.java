package dev.tevarin.module.impl.render;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.*;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.math.Location;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

import static dev.tevarin.utils.render.RenderUtil.disableGL2D;
import static dev.tevarin.utils.render.RenderUtil.enableGL2D;

public final class DMGParticle extends Module {
    public DMGParticle() {
        super("DMGParticle", Category.Render);
    }

    private HashMap<EntityLivingBase,Float> healthMap = new HashMap<>();
    private List<Particle> particles = new ArrayList<>();

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.particles.clear();
        this.healthMap.clear();
    }
    @EventTarget
    public void onRender(EventRender3D event) {
        for (Particle particle : this.particles) {
            final double x = particle.location.getX() - this.mc.getRenderManager().getRenderPosX();
            final double y = particle.location.getY() - this.mc.getRenderManager().getRenderPosY();
            final double z = particle.location.getZ() - this.mc.getRenderManager().getRenderPosZ();

            GlStateManager.pushMatrix();

            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0F, -1500000.0F);

            GlStateManager.translate((float) x, (float) y, (float) z);
            GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            float var10001 = this.mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
            GlStateManager.rotate(this.mc.getRenderManager().playerViewX, var10001, 0.0F, 0.0F);
            double scale = 0.03;
            GlStateManager.scale(-scale, -scale, scale);

            enableGL2D();
            disableGL2D();

            GL11.glDepthMask(false);
            mc.fontRendererObj.drawString(particle.text,
                    -(this.mc.fontRendererObj.getStringWidth(particle.text) / 2),
                    -(this.mc.fontRendererObj.FONT_HEIGHT - 1), 0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);

            GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
            GlStateManager.disablePolygonOffset();

            GlStateManager.popMatrix();
        }
    }
    @EventTarget
    public void onLiving(EventLivingUpdate event) {
        final EntityLivingBase entity = (EntityLivingBase) event.getEntity();

        if (entity == this.mc.thePlayer)
            return;

        // detect

        if (!healthMap.containsKey(entity))
            healthMap.put(entity, entity.getHealth());

        final float before = healthMap.get(entity);
        final float after = entity.getHealth();

        if (before != after) {
            String text;

            if ((before - after) < 0) {
                text = EnumChatFormatting.GREEN + "" + roundToPlace((before - after) * -1, 1);
            } else {
                text = EnumChatFormatting.YELLOW + "" + roundToPlace((before - after), 1);
            }

            Location location = new Location(entity);

            location.setY(entity.getEntityBoundingBox().minY
                    + ((entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2));

            location.setX((location.getX() - 0.5) + (new Random(System.currentTimeMillis()).nextInt(5) * 0.1));
            location.setZ((location.getZ() - 0.5) + (new Random(System.currentTimeMillis() + 1).nextInt(5) * 0.1));

            particles.add(new Particle(location, text));

            healthMap.remove(entity);
            healthMap.put(entity, entity.getHealth());
        }
    }
    @EventTarget
    public void onTick(EventTick event) {
        List<Particle> particlesToRemove = new ArrayList<>();
        for (Particle particle : particles) {
            particle.ticks++;

            if (particle.ticks <= 10) {
                particle.location.setY( (particle.location.getY() + particle.ticks * 0.005));
            }

            if (particle.ticks > 20) {
                particlesToRemove.add(particle);
            }
        }

        particles.removeAll(particlesToRemove);
    };
    public static double roundToPlace(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    class Particle {
        public Particle(Location location, String text) {
            this.location = location;
            this.text = text;
            this.ticks = 0;
        }

        public int ticks;
        public Location location;
        public String text;
    }

}
