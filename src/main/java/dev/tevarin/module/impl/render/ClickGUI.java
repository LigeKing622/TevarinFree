package dev.tevarin.module.impl.render;

import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.ui.gui.clickgui.NewClickgui.NewClickGui;
import org.lwjglx.input.Keyboard;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", Category.Render);
        setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {

        mc.displayGuiScreen(NewClickGui.INSTANCE);
        this.toggle();

    }
}
