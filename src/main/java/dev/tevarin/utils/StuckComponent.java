package dev.tevarin.utils;


import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class StuckComponent implements IMinecraft {
    public static final MovementComponent  INSTANCE = new MovementComponent ();
    public static Boolean pre = false;
    public static boolean cancelMove = false;
    private static double motionX = 0.0;
    private static double motionY = 0.0;
    private static double motionZ = 0.0;
    private static float fallDistance = 0.0f;
    private static int moveTicks = 0;

    public static float getSpeed() {
        return (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMove() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }

    public static void strafe(float speed) {
        if (!isMove()) {
            return;
        }
        double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * (double)speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static void forward(double length) {
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(yaw) * length, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(yaw) * length);
    }

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void cancelMove() {
        if (mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        cancelMove = true;
        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;
        fallDistance = mc.thePlayer.fallDistance;
    }

    public static void resetMove() {
        cancelMove = false;
        moveTicks = 0;
    }
    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }
    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPost()) {
            pre = false;
        }
    }
    @EventTarget
    public void onUpdate(PreUpdateEvent event) {
        if (cancelMove) {
            if (moveTicks > 0) {
                return;
            }
            mc.thePlayer.motionX = motionX;
            mc.thePlayer.motionZ = motionZ;
            mc.thePlayer.motionY = motionY;
            mc.thePlayer.fallDistance = fallDistance;
        }
    }
    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer && cancelMove) {
            if (moveTicks > 0) {
                motionX = mc.thePlayer.motionX;
                motionZ = mc.thePlayer.motionZ;
                motionY = mc.thePlayer.motionY;
                fallDistance = mc.thePlayer.fallDistance;
                --moveTicks;
            }
        }
    }
    @EventTarget
    public void onTick(EventTick event) {
        if (mc.thePlayer == null) {
            resetMove();
            return;
        }
        pre = true;
        if (cancelMove) {
            if (GetC03StatusUtil.noMovePackets >= 20) {
                mc.thePlayer.motionX = motionX;
                mc.thePlayer.motionY = motionY;
                mc.thePlayer.motionZ = motionZ;
                mc.thePlayer.fallDistance = fallDistance;
                ++moveTicks;
            }
            if (moveTicks > 0) {
                return;
            }
            mc.thePlayer.motionX = motionX;
            mc.thePlayer.motionZ = motionZ;
            mc.thePlayer.motionY = motionY;
            mc.thePlayer.fallDistance = fallDistance;
        }
    }
    @EventTarget
    public void onMove(MoveEvent event) {
        if (cancelMove) {
            if (moveTicks > 0) {
                return;
            }
            event.setCancelled();
        }
    }
    @EventTarget
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == mc.thePlayer.getEntityId() && cancelMove) {
            mc.thePlayer.motionX = motionX;
            mc.thePlayer.motionY = motionY;
            mc.thePlayer.motionZ = motionZ;
            mc.thePlayer.fallDistance = fallDistance;
            ++moveTicks;
        }
    }
}