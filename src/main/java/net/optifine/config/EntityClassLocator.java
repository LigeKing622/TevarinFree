package net.optifine.config;

import net.optifine.util.EntityUtils;
import net.minecraft.util.ResourceLocation;

public class EntityClassLocator implements IObjectLocator {
    public Object getObject(ResourceLocation loc) {
        Class oclass = EntityUtils.getEntityClassByName(loc.getResourcePath());
        return oclass;
    }
}
