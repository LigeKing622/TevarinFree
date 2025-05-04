package dev.tevarin.module;

import dev.Ethereal.module.impl.render.ItemTags;
import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventClick;
import dev.tevarin.event.impl.events.EventKey;
import dev.tevarin.event.impl.events.EventRender2D;
import dev.tevarin.module.impl.combat.*;
import dev.tevarin.module.impl.misc.*;
import dev.tevarin.module.impl.move.*;
import dev.tevarin.module.impl.player.*;
import dev.tevarin.module.impl.render.*;
import dev.tevarin.module.impl.world.*;
import dev.tevarin.value.Value;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();
    private final Map<String, Module> moduleMap = new HashMap<>();

    private boolean enabledNeededMod = true;
    public static List<Module> getModulesInType(Category t) {
        ArrayList<Module> output = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() != t)
                continue;
            output.add(m);
        }
        return output;
    }
    public void init() {
        Client.instance.eventManager.register(this);
        Client.instance.hudManager.init();

        // combat
        addModule(new KillAura());
        addModule(new Velocity());
        addModule(new SuperKnockBack());
        addModule(new AutoSoup());
        addModule(new AutoWeapon());
        addModule(new AntiFireBall());
        addModule(new TickBase());
        addModule(new Criticals());
        addModule(new Gapple());
        // movement
        addModule(new Sprint());
        addModule(new Speed());
        addModule(new NoWeb());
        addModule(new GuiMove());
        addModule(new NoLiquid());
        addModule(new NoSlow());
        addModule(new TargetStrafe());
        // player
        addModule(new MidPearl());
        addModule(new InvCleaner());
        addModule(new ChestStealer());
        addModule(new Antivoid());
        addModule(new FastPlace());
        addModule(new VClip());
        addModule(new Blink());
        addModule(new SpeedMine());
        addModule(new BalanceTimer());
        addModule(new AutoTool());
        addModule(new NoFall());

        // world
        addModule(new Disabler());
        addModule(new Scaffold());
        addModule(new ChestAura());
        addModule(new Stuck());
        addModule(new PlayerTracker());
        addModule(new Ambience());

        // render
        addModule(new ClickGUI());
        addModule(new HUD());
        addModule(new Chams());
        addModule(new TargetESP());
        addModule(new ItemTags());
        addModule(new BlockAnimation());
        addModule(new Camera());
        addModule(new ChinaHat());
        addModule(new BetterFPS());
        addModule(new Projectile());
        addModule(new Health());
        addModule(new XRay());
        addModule(new KillEffect());
        addModule(new ItemPhysics());
        addModule(new ESP());
        addModule(new MotionBlur());

        // misc
        addModule(new AntiBot());
        addModule(new Teams());
        addModule(new Protocol());
        addModule(new AutoPlay());
        addModule(new MemoryFix());
        addModule(new MCF());

        sortModulesByName();
    }

    public void sortModulesByName() {
        List<Map.Entry<String, Module>> entryList = new ArrayList<>(moduleMap.entrySet());
        entryList.sort(Comparator.comparing(entry -> entry.getValue().getName()));

        moduleMap.clear();
        for (Map.Entry<String, Module> entry : entryList) {
            moduleMap.put(entry.getKey(), entry.getValue());
        }
    }


    public void addModule(Module module) {
        for (final Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(module);
                if (obj instanceof Value) module.getValues().add((Value) obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        moduleMap.put(module.getClass().getSimpleName(), module);
    }

    public Map<String, Module> getModuleMap() {
        return moduleMap;
    }

    public <T extends Module> T getModule(Class<T> cls) {
        return cls.cast(moduleMap.get(cls.getSimpleName()));
    }

    public Module getModule(String name) {
        for (Module module : moduleMap.values()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public boolean haveModules(Category category, String key) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory() == category)
                .anyMatch(module -> module.getName().toLowerCase().replaceAll(" ", "").contains(key));
    }

    @EventTarget
    public void onKey(EventKey e) {
        moduleMap.values().stream()
                .filter(module -> module.getKey() == e.getKey() && e.getKey() != -1)
                .forEach(Module::toggle);
    }

    @EventTarget
    public void onMouse(EventClick e) {
        moduleMap.values().stream()
                .filter(module -> module.getMouseKey() != -1 && module.getMouseKey() == e.getKey() && e.getKey() != -1)
                .forEach(Module::toggle);
    }

    public List<Module> getModsByPage(Category.Pages m) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory().pages == m)
                .collect(Collectors.toList());
    }

    public List<Module> getModsByCategory(Category m) {
        return moduleMap.values().stream()
                .filter(module -> module.getCategory() == m)
                .collect(Collectors.toList());
    }

    @EventTarget
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            moduleMap.values().stream()
                    .filter(Module::isDefaultOn)
                    .forEach(module -> module.setState(true));
        }
    }
}
