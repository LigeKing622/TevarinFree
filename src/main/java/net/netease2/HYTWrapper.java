/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.netease2;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HYTWrapper {
    private static Minecraft mc = HYTWrapper.getMinecraft();
    public static final Logger logger = LogManager.getLogger(HYTWrapper.class);

    public static void runOnMainThread(Runnable var0) {
        HYTWrapper.getMinecraft().addScheduledTask(var0);
    }

    public static int getRenderDistance() {
        return HYTWrapper.getMinecraft().gameSettings.renderDistanceChunks;
    }

    public static File getFile(String... var0) {
        return new File(HYTWrapper.getMcDataDir(), Arrays.stream(var0).map(var0x -> new StringBuilder().insert(0, "/").append((String) var0x).toString()).collect(Collectors.joining()));
    }

    public static Minecraft getMinecraft() {
        if (mc == null) {
            mc = Minecraft.getMinecraft();
        }
        return mc;
    }

    public static World getWorld() {
        return HYTWrapper.getMinecraft().theWorld;
    }

    public static Entity getPlayer() {
        return HYTWrapper.getMinecraft().getRenderViewEntity();
    }

    public static File getMcDataDir() {
        return HYTWrapper.getMinecraft().mcDataDir;
    }

}

