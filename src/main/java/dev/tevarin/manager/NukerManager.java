package dev.tevarin.manager;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventRenderBlock;
import dev.tevarin.event.impl.events.EventUpdate;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class NukerManager {
    public ArrayList<BlockPos> list = new ArrayList<BlockPos>();

    public void update() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
        list.clear();
    }

    @EventTarget
    public void onRenderBlock(EventRenderBlock e) {
        BlockPos pos = new BlockPos(e.x, e.y, e.z);
        if (!list.contains(pos) && e.block instanceof BlockBed) {
            list.add(pos);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        list.removeIf(pos -> !(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockBed));
    }
}
