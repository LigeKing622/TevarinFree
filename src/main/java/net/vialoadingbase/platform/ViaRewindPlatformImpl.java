package net.vialoadingbase.platform;

import com.viaversion.viarewind.api.ViaRewindPlatform;
import net.vialoadingbase.ViaLoadingBase;
import dev.tevarin.Client;
import net.minecraft.item.ItemSword;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl
        implements ViaRewindPlatform {
    public ViaRewindPlatformImpl(File directory) {
        this.init(new File(directory, "viarewind.yml"));
    }

    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    public boolean isSword() {
        if (Client.mc.thePlayer == null || Client.mc.theWorld == null) {
            return false;
        }
        return Client.mc.thePlayer.getCurrentEquippedItem() != null && Client.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }
}

