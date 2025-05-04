package dev.tevarin.module.impl.combat;



import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventRender3D;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.player.Blink;
import dev.tevarin.module.impl.world.Scaffold;
import dev.tevarin.utils.DebugUtil;
import dev.tevarin.utils.PredictionUtil;
import dev.tevarin.utils.math.TimerUtils;
import dev.tevarin.utils.player.RayCastUtil;
import dev.tevarin.utils.player.RotationNew;
import dev.tevarin.utils.player.RotationUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.lwjglx.util.vector.Vector2f;

import java.awt.*;
import java.util.Comparator;
import java.util.Optional;


// 记得备份 不用 这个雪球 老狗屎了
public class AutoProjectile extends Module {
    public NumberValue minRangeValue = new NumberValue("Min Range", 3, 2, 10, 0.1);
    public NumberValue maxRangeValue = new NumberValue("Max Range", 8, 2, 16, 0.1);

    public NumberValue delayValue = new NumberValue("Delay", 500, 0, 1000, 50);
    public NumberValue predictValue = new NumberValue("Predict", 1.5, 0, 2, 0.1);

    public BoolValue autoSwitchValue = new BoolValue("AutoSwitch", true);


    TimerUtils delay = new TimerUtils();

    int currentSlot = -1;

    EntityLivingBase target;
    boolean she;

    public AutoProjectile() {
        super("AutoKnockBack", Category.Combat);
    }

    @Override
    public void onEnable() {
        this.she = false;
    }

    @Override
    public void onDisable() {
        this.she = false;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if (!delay.hasTimeElapsed(delayValue.getValue().intValue())
                || !KillAura.targets.isEmpty()
                || Client.instance.rotationManager.active
                || Client.instance.moduleManager.getModule(Scaffold.class).getState()
                || Client.instance.moduleManager.getModule(Gapple.class).getState()
                || Client.instance.moduleManager.getModule(Blink.class).getState()
        )
            return;
        Optional<EntityOtherPlayerMP> target = getTarget(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue());
        this.target = null;
        if (target.isEmpty()) return;
        if (mc.thePlayer.inventory.getCurrentItem()!= null && mc.thePlayer.inventory.getCurrentItem().getItem() == Items.snowball || mc.thePlayer.inventory.getCurrentItem()!= null && mc.thePlayer.inventory.getCurrentItem().getItem() == Items.egg || autoSwitchValue.getValue() && getThrowSlot() != -1) {
            this.target = target.get();
            EntityOtherPlayerMP player = target.get();
            double distance = mc.thePlayer.getDistance(player);
            float predict = (float) distance * predictValue.getValue().floatValue();
            if (player.fakePlayer != null) {
                player = player.fakePlayer;
                predict -= 1;
            }
            RotationNew rotationNew = RotationUtil.toRotation(
                    player.getPositionEyes(predict), 0F
            );
            Vector2f targetRotation = new Vector2f(rotationNew.getYaw(), rotationNew.getPitch());
            MovingObjectPosition rayCast = RayCastUtil.rayCast(targetRotation, distance, 0f, mc.thePlayer, false, 0F, 0F);
            if (rayCast == null) {
                DebugUtil.print("RayTraceResult == null !!!!!!!!!!!");
                Thread.dumpStack();
                return;
            }
            if (rayCast == null) {
                //  DebugUtil.log("RayTraceResult == null !!!!!!!!!!!");
                Thread.dumpStack();
                return;
            }
            if (rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                return;
            }
            targetRotation.y = MathHelper.clamp_float(targetRotation.getY(), -90, 90);
            Client.instance.rotationManager.setRotation(targetRotation, 90.0f, true, false);
            this.she = true;
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (target == null ) return;
        final var alpha = target.hurtTime * 5;
        Color color = new Color(153, 246, 255, 100+ alpha );
        RenderUtil.boundingESPBoxFilled(PredictionUtil.PredictedTarget(target, 2), color);
    }
    @EventTarget
    public void onMotion(EventMotion event) {
        if (Client.instance.moduleManager.getModule(Blink.class).getState() || Client.instance.moduleManager.getModule(Scaffold.class).getState()|| Client.instance.moduleManager.getModule(Gapple.class).getState()) return;
        if (event.getEventState() == EventMotion.EventState.POST) {
            if (autoSwitchValue.getValue()) {
                if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() == Items.snowball || mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() == Items.egg || mc.thePlayer.inventory.getCurrentItem() == null) {
                    if (currentSlot != -1) {
                        mc.thePlayer.inventory.currentItem = currentSlot;
                        currentSlot = -1;
                    }
                } else if (she && target != null && KillAura.targets.isEmpty() && mc.thePlayer.inventory.getCurrentItem()!= null && mc.thePlayer.inventory.getCurrentItem().getItem() != Items.bow) {
                    int slot = getThrowSlot();
                    if (slot != -1) {
                        currentSlot = mc.thePlayer.inventory.currentItem;
                        mc.thePlayer.inventory.currentItem = slot;
                    }
                }
            }
            if (mc.thePlayer.inventory.getCurrentItem() != null &&mc.thePlayer.inventory.getCurrentItem().getItem() == Items.snowball || mc.thePlayer.inventory.getCurrentItem() != null &&mc.thePlayer.inventory.getCurrentItem().getItem() == Items.egg) {
                if (she) {
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                    delay.reset();
                    this.she = false;
                }
            }
        }
    }


        int getThrowSlot () {
            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i).getItem() == Items.snowball || mc.thePlayer.inventory.getStackInSlot(i).getItem() == Items.egg) {
                    return i;
                }
            }
            return -1;
        }


    public static Optional<EntityOtherPlayerMP> getTarget(float min, float max) {
        final double minRange = min * min;
        final double maxRange = max * max;
        return mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityOtherPlayerMP)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityOtherPlayerMP) entity)

                .filter(entityLivingBase -> mc.thePlayer.getDistanceSq(entityLivingBase) <= maxRange && mc.thePlayer.getDistanceSq(entityLivingBase) >= minRange)
                .min(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSq(entity)));
    }
}