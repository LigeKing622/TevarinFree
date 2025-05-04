package dev.tevarin.module.impl.move;


import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.EventMove;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.Gapple;
import dev.tevarin.module.impl.combat.KillAura;
import dev.tevarin.module.impl.world.Scaffold;
import dev.tevarin.utils.player.MoveUtil;
import dev.tevarin.utils.player.MovementUtil;
import dev.tevarin.utils.player.PlayerUtil;
import dev.tevarin.utils.player.RotationUtil;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;


public class Speed extends Module {
    private float speed = 0.08F;
    private final ModeValue mode = new ModeValue("Mode",new String[]{ "Entity", "Grim"} ,"Entity");
    private final NumberValue range = new NumberValue("CheckRange", 1, 0.1, 5,0.1);
    private final NumberValue boostAmount = new NumberValue("BoostAmount", 1, 0.1, 2,0.1);
    int onGroundticks = 0;

    public Speed() {
        super("Speed",Category.Movement);

    }

    @EventTarget
    public void onUpdateEvent(EventUpdate event) {
        switch (mode.get()) {
            case "Entity":
                if(isNull())return;
                if (mc.thePlayer.moveForward == 0.0f && mc.thePlayer.moveStrafing == 0.0f) {
                    return;
                }
                double collisions = 0;
                for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
                    if (canCauseSpeed(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.get()) {
                        collisions = boostAmount.get();
                    }
                }
                double yaw = Math.toRadians(mc.thePlayer.movementYaw);
                double boost = this.speed * collisions;
                for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
                    if (canCauseSpeed(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.get()) {
                        mc.thePlayer.addVelocity(-Math.sin(yaw) * boost, 0.0, Math.cos(yaw) * boost);
                    }
                }
                break;
        }
    }

    private boolean canCauseSpeed(Entity entity) {
        return entity != mc.thePlayer && entity instanceof EntityPlayer;
    }


    @EventTarget
    public void onMotionEvent(EventMotion e) {
        if (Client.instance.getModuleManager().getModule(Gapple.class).getState()){
            return;
        }
        this.setSuffix(mode.get());
        switch (mode.get()) {

            case "Grim" :
                AxisAlignedBB playerBox = Speed.mc.thePlayer.boundingBox.expand(1.0, 1.0, 1.0);
                int c = 0;
                for (Entity entity : Speed.mc.theWorld.loadedEntityList) {
                    if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityBoat) && !(entity instanceof EntityMinecart) && !(entity instanceof EntityFishHook) || entity instanceof EntityArmorStand || entity.getEntityId() == Speed.mc.thePlayer.getEntityId() || !playerBox.intersectsWith(entity.boundingBox) || entity.getEntityId() == -8 || entity.getEntityId() == -1337) continue;
                    ++c;
                }
                if (c > 0 && MoveUtil.isMoving()) {
                    double strafeOffset = (double)Math.min(c, 3) * 0.04;
                    float yaw = this.getMoveYaw();
                    double mx = -Math.sin(Math.toRadians(yaw));
                    double mz = Math.cos(Math.toRadians(yaw));
                    Speed.mc.thePlayer.addVelocity(mx * strafeOffset, 0.0, mz * strafeOffset);
                    if (c < 4 && KillAura.target != null && this.shouldFollow()) {
                        Speed.mc.gameSettings.keyBindLeft.pressed = true;
                        break;
                    }
                    Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                    break;
                }
                Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                break;
        }
    }
    public boolean shouldFollow() {
        return getState() && Speed.mc.gameSettings.keyBindJump.isKeyDown();
    }
    private float getMoveYaw() {
        EntityPlayerSP thePlayer = Speed.mc.thePlayer;
        float moveYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing == 0.0f) {
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing != 0.0f) {
            moveYaw = thePlayer.moveForward > 0.0f ? (moveYaw += thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f) : (moveYaw -= thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f);
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveStrafing != 0.0f && thePlayer.moveForward == 0.0f) {
            moveYaw += thePlayer.moveStrafing > 0.0f ? -70.0f : 70.0f;
        }
        if (KillAura.target != null && Speed.mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw = Client.instance.rotationManager.rotation.getX();
        }
        return moveYaw;
    }
    public void onDisable() {
        this.onGroundticks = 0;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }
}
