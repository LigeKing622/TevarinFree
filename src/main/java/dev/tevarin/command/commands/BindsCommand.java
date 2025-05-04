package dev.tevarin.command.commands;

import org.lwjglx.input.Keyboard;
import dev.tevarin.Client;
import dev.tevarin.command.Command;
import dev.tevarin.module.Module;
import dev.tevarin.utils.DebugUtil;

import java.util.ArrayList;
import java.util.List;

public class BindsCommand extends Command {
    public BindsCommand() {
        super("binds");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void run(String[] args, String originalMessag) {
        for (Module module : Client.instance.moduleManager.getModuleMap().values()) {

            if (module.getKey() == -1)
                continue;
            DebugUtil.log("§a[Binds]§f" + module.name + " :" + Keyboard.getKeyName(module.key));
        }
    }
}
