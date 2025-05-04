package dev.tevarin.utils.player;

import net.minecraft.util.Vec3;

class VecRotation {
    final Vec3 vec3;
    final Rotation rotation;

    public VecRotation(Vec3 Vec3, Rotation Rotation) {
        vec3 = Vec3;
        rotation = Rotation;
    }

    public Vec3 getVec3() {
        return vec3;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vec3 getVec() {
        return vec3;
    }
}
