package dev.tevarin.utils.player;

import com.google.common.base.Predicates;

import dev.tevarin.Client;
import dev.tevarin.utils.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import org.lwjglx.util.vector.Vector2f;


import java.util.List;

import static dev.tevarin.utils.player.RotationUtil.mc;


public final class RayCastUtil {
    public static MovingObjectPosition rayCast(Vector2f rotation, double range) {
        return RayCastUtil.rayCast(rotation, range, 0.0f);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand) {
        return RayCastUtil.rayCast(rotation, range, expand, Client.mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand, Entity entity, boolean throughWall, float predict, float predictPlayer) {
//        final float partialTicks = mc.getTimer().renderPartialTicks;
        MovingObjectPosition objectMouseOver;
        if (entity != null && mc.theWorld != null) {
            objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y, predictPlayer);
            double d1 = range;
            final Vec3 vec3 = entity.getPositionEyes(predictPlayer);

            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && !throughWall) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
                // RayCastUtil.rayCast(new Rotation(mc.player.rotationYaw, mc.player.rotationPitch), 3, 0F, false)
            }

            final Vec3 vec31 = mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
            final Vec3 vec32 = vec3.add(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                if (entity1 instanceof FakePlayer) continue;
                if (entity1.getUniqueID().equals(mc.thePlayer.getUniqueID())) continue;
                float predict2 = predict;
                Entity target = entity1;
                if (entity1 instanceof EntityPlayer player && player.fakePlayer != null) {
                    target = player.fakePlayer;
                    predict2 -= 1F;
                }

                final float f1 = target.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(f1, f1, f1);

                if (predict2 != 0) {
                    axisalignedbb = axisalignedbb.offset(
                            (target.posX - target.lastTickPosX) * predict2,
                            (target.posY - target.lastTickPosY) * predict2,
                            (target.posZ - target.lastTickPosZ) * predict2
                    );
                }

                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }

            return objectMouseOver;
        }

        return null;
    }


    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, Entity entity) {
        float partialTicks = Client.mc.timer.renderPartialTicks;
        if (entity != null && Client.mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec31 = Client.mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            List<Entity> list = Client.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (Entity entity1 : list) {
                double d3;
                float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d2 >= 0.0)) continue;
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d3 = vec3.distanceTo(movingobjectposition.hitVec)) < d2) && d2 != 0.0) continue;
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
                d2 = d3;
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
            return objectMouseOver;
        }
        return null;
    }

}

