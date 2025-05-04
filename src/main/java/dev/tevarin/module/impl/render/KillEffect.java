package dev.tevarin.module.impl.render;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventAttack;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.ui.hud.notification.NotificationType;
import dev.tevarin.utils.render.animation.impl.ContinualAnimation;
import dev.tevarin.utils.sound.SoundUtil;
import dev.tevarin.value.impl.BoolValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.EnumParticleTypes;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public final class KillEffect extends Module {


    public KillEffect() {
        super("KillEffect", Category.Render);
    }

    private EntitySquid squid;
    private double percent = 0.0;
    private final ContinualAnimation anim = new ContinualAnimation();

    private final BoolValue lightning = new BoolValue("Lightning", true);
    private final BoolValue tipsKillsValue = new BoolValue("TipsKills", true);
    private final BoolValue explosion = new BoolValue("Explosion", true);
    private final BoolValue squidValue = new BoolValue("Squid", true);
    private final BoolValue killSoundValue = new BoolValue("KillSound", true);
    public int kills = 0;

    private EntityLivingBase target;

    @Override
    public void onEnable() {
        kills = 0;

        super.onEnable();
    }

    @Override
    public void onDisable() {
        kills = 0;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (isNull()) return;
        if (this.squidValue.getValue() && this.squid != null) {
            if (KillEffect.mc.theWorld.loadedEntityList.contains(this.squid)) {
                if (this.percent < 1.0) {
                    this.percent += Math.random() * 0.048;
                }
                if (this.percent >= 1.0) {
                    this.percent = 0.0;
                    for (int i = 0; i <= 8; ++i) {
                        KillEffect.mc.effectRenderer.emitParticleAtEntity(this.squid, EnumParticleTypes.FLAME);
                    }
                    KillEffect.mc.theWorld.removeEntity(this.squid);
                    this.squid = null;
                    return;
                }
            } else {
                this.percent = 0.0;
            }
            double easeInOutCirc = this.easeInOutCirc(1.0 - this.percent);
            this.anim.animate((float) easeInOutCirc, 450);
            this.squid.setPositionAndUpdate(this.squid.posX, this.squid.posY + (double) this.anim.getOutput() * 0.9, this.squid.posZ);
        }
        if (this.squid != null && squidValue.getValue()) {
            this.squid.squidPitch = 0.0f;
            this.squid.prevSquidPitch = 0.0f;
            this.squid.squidYaw = 0.0f;
            this.squid.squidRotation = 90.0f;
        }
        if (this.target != null && this.target.getHealth() <= 0.0f && !KillEffect.mc.theWorld.loadedEntityList.contains(this.target)) {
            ++kills;
            if (tipsKillsValue.getValue()) {
                NotificationManager.post(NotificationType.SUCCESS, "Kills +1", "Killed " + this.kills + " Players.  ");
            }
            if (killSoundValue.getValue()) {
                this.playSound(SoundType.KILL, 0.75f);
            }
            if (this.squidValue.getValue()) {
                this.squid = new EntitySquid(KillEffect.mc.theWorld);
                KillEffect.mc.theWorld.addEntityToWorld(-8, this.squid);
                this.squid.setPosition(this.target.posX, this.target.posY, this.target.posZ);
            }
            if (this.lightning.getValue()) {
                final EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ);
                mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000), entityLightningBolt);

                SoundUtil.playSound("ambient.weather.thunder");
            }
            if (this.explosion.getValue()) {
                for (int i = 0; i <= 8; i++) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                }
                SoundUtil.playSound("item.fireCharge.use");
            }
            this.target = null;
        }
    }

    public double easeInOutCirc(double x) {
        return x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        final Entity entity = event.getTarget();
        if (entity instanceof EntityLivingBase) {
            target = (EntityLivingBase) entity;
        }
    }

    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            try {
                AudioInputStream as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/minecraft/tevarin/" + st.getName()))));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public enum SoundType {
        KILL("kill.wav");

        final String music;

        SoundType(String fileName) {
            this.music = fileName;
        }

        public String getName() {
            return this.music;
        }
    }

    ;
}