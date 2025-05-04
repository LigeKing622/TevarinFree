package dev.tevarin.ui.hud;


import dev.tevarin.Client;
import dev.tevarin.ui.hud.impl.*;
import dev.tevarin.value.Value;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HUDManager {
    public Map<String, HUD> hudObjects = new HashMap<String, HUD>();

    public void init() {
        this.add(new ModuleList());
        this.add(new Watermark());
        this.add(new TargetHUD());;
        this.add(new Effects());
        this.add(new Scoreboard());
        this.add(new Chest());
        this.add(new Armor());
    }

    private void add(HUD hud) {
        this.hudObjects.put(hud.getClass().getSimpleName(), hud);
        for (Field field : hud.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object obj = field.get(hud);
                if (!(obj instanceof Value)) continue;
                hud.m.getValues().add((Value) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Client.instance.moduleManager.getModuleMap().put(hud.getClass().getSimpleName(), hud.m);
    }
}
