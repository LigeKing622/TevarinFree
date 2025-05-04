//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tevarin.utils.player;


import dev.tevarin.Client;
import dev.tevarin.event.impl.events.EventMove;
import dev.tevarin.event.impl.events.MoveEvent;
import dev.tevarin.module.impl.move.TargetStrafe;
import dev.tevarin.utils.IMinecraft;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

public class MovementUtil
        implements IMinecraft {
    public MovementUtil() {
    }

    public static double getDirection(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0F;
        }

        float forward = 1.0F;
        if (moveForward < 0.0) {
            forward = -0.5F;
        } else if (moveForward > 0.0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0F * forward;
        }

        if (moveStrafing < 0.0) {
            rotationYaw += 90.0F * forward;
        }

        return Math.toRadians((double) rotationYaw);
    }
    public static double getHorizontalMotion() {
        return Math.hypot(MovementUtil.mc.thePlayer.motionX, MovementUtil.mc.thePlayer.motionZ);
    }

    public static void strafe(EventMove event) {
        MovementUtil.strafe(event, MovementUtil.getHorizontalMotion());
    }

    public static void strafe(EventMove event, double speed) {
        float direction = (float)Math.toRadians(MovementUtil.getPlayerDirection());
        TargetStrafe targetStrafe = Client.instance.moduleManager.getModule(TargetStrafe.class);
        if (targetStrafe.shouldTargetStrafe()) {
            direction = targetStrafe.getDirection();
        }
        if (MovementUtil.isMoving2()) {
            MovementUtil.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            event.setX(MovementUtil.mc.thePlayer.motionX);
            MovementUtil.mc.thePlayer.motionZ = Math.cos(direction) * speed;
            event.setZ(MovementUtil.mc.thePlayer.motionZ);
        } else {
            MovementUtil.mc.thePlayer.motionX = 0.0;
            event.setX(0.0);
            MovementUtil.mc.thePlayer.motionZ = 0.0;
            event.setZ(0.0);
        }
    }

    public static void strafe(double speed) {
        float direction = (float)Math.toRadians(MovementUtil.getPlayerDirection());
        if (MovementUtil.isMoving2()) {
            MovementUtil.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            MovementUtil.mc.thePlayer.motionZ = Math.cos(direction) * speed;
        } else {
            MovementUtil.mc.thePlayer.motionX = 0.0;
            MovementUtil.mc.thePlayer.motionZ = 0.0;
        }
    }

    public static void strafe(MoveEvent event, double dir, double speed) {
        float direction = (float)Math.toRadians(dir);
        if (MovementUtil.isMoving2()) {
            MovementUtil.mc.thePlayer.motionX = -Math.sin(direction) * speed;
            event.setX(MovementUtil.mc.thePlayer.motionX);
            MovementUtil.mc.thePlayer.motionZ = Math.cos(direction) * speed;
            event.setZ(MovementUtil.mc.thePlayer.motionZ);
        } else {
            MovementUtil.mc.thePlayer.motionX = 0.0;
            event.setX(0.0);
            MovementUtil.mc.thePlayer.motionZ = 0.0;
            event.setZ(0.0);
        }
    }
    public static double getJumpHeight() {
        double jumpY = mc.thePlayer.getJumpUpwardsMotion();
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            jumpY += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
        }
        return jumpY;
    }
    public static int getSpeedAmplifier() {
        if (MovementUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return 1 + MovementUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        }
        return 0;
    }

    public static void jump(EventMove event) {
        mc.thePlayer.motionY = getJumpHeight();
        event.setY(mc.thePlayer.motionY);
    }
    public static float getPlayerDirection() {
        float direction = Client.mc.thePlayer.rotationYaw;
        if (Client.mc.thePlayer.moveForward > 0.0F) {
            if (Client.mc.thePlayer.moveStrafing > 0.0F) {
                direction -= 45.0F;
            } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
                direction += 45.0F;
            }
        } else if (Client.mc.thePlayer.moveForward < 0.0F) {
            if (Client.mc.thePlayer.moveStrafing > 0.0F) {
                direction -= 135.0F;
            } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
                direction += 135.0F;
            } else {
                direction -= 180.0F;
            }
        } else if (Client.mc.thePlayer.moveStrafing > 0.0F) {
            direction -= 90.0F;
        } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
            direction += 90.0F;
        }

        return direction;
    }

    public static float getPlayerDirection(float baseYaw) {
        float direction = baseYaw;
        if (Client.mc.thePlayer.moveForward > 0.0F) {
            if (Client.mc.thePlayer.moveStrafing > 0.0F) {
                direction -= 45.0F;
            } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
                direction += 45.0F;
            }
        } else if (Client.mc.thePlayer.moveForward < 0.0F) {
            if (Client.mc.thePlayer.moveStrafing > 0.0F) {
                direction -= 135.0F;
            } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
                direction += 135.0F;
            } else {
                direction -= 180.0F;
            }
        } else if (Client.mc.thePlayer.moveStrafing > 0.0F) {
            direction -= 90.0F;
        } else if (Client.mc.thePlayer.moveStrafing < 0.0F) {
            direction += 90.0F;
        }

        return direction;
    }
    public static boolean isMoving2() {
        boolean forward = Keyboard.isKeyDown(MovementUtil.mc.gameSettings.keyBindForward.getKeyCode());
        boolean back = Keyboard.isKeyDown(MovementUtil.mc.gameSettings.keyBindBack.getKeyCode());
        boolean left = Keyboard.isKeyDown(MovementUtil.mc.gameSettings.keyBindLeft.getKeyCode());
        boolean right = Keyboard.isKeyDown(MovementUtil.mc.gameSettings.keyBindRight.getKeyCode());
        return forward || back || left || right;
    }
    public static boolean isMoving() {
        return Client.mc.thePlayer.moveStrafing != 0.0F || Client.mc.thePlayer.moveForward != 0.0F;
    }
}
