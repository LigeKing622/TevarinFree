package dev.tevarin.module.impl.render;

import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.util.ResourceLocation;

public final class Camera extends Module {
    public static Camera INSTANCE;

    public Camera() {
        super("Camera", Category.Render);
        INSTANCE = this;
    }

    public final ModeValue capeMode = new ModeValue("Cape", new String[]{"Astolfo"}, "Astolfo");
    public final BoolValue cameraClipValue = new BoolValue("CameraClip", false);
    public final BoolValue motionCamera = new BoolValue("Motion Camera", false);
    public final BoolValue noHurtCameraValue = new BoolValue("NoHurtCamera", false);
    public final BoolValue betterBobbingValue = new BoolValue("BetterBobbing", false);
    public final BoolValue noFovValue = new BoolValue("NoFov", false);
    public final BoolValue nofire = new BoolValue("No Fire", true);
    public final NumberValue interpolation = new NumberValue("Motion Interpolation", 0.15f, 0.05f, 0.5f, 0.05f, () -> motionCamera.get());
    public final NumberValue fovValue = new NumberValue("Fov", 1.0, 0.0, 4.0, 0.1);

    public ResourceLocation getCape() {
        return new ResourceLocation("tevarin/images/cape/" + capeMode.get().toLowerCase() + ".png");
    }

    public boolean isOptifineCape() {
        return capeMode.is("Optifine");
    }
}