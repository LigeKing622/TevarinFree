package net.vialoadingbase.platform;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import net.vialoadingbase.ViaLoadingBase;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl
        implements ViaBackwardsPlatform {
    private final File directory;

    public ViaBackwardsPlatformImpl(File directory) {
        this.directory = directory;
        this.init(this.directory);
    }

    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }

    public boolean isOutdated() {
        return false;
    }

    public void disable() {
    }

    public File getDataFolder() {
        return this.directory;
    }
}

