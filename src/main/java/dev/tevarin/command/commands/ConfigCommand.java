package dev.tevarin.command.commands;

import dev.tevarin.Client;
import dev.tevarin.command.Command;
import dev.tevarin.utils.DebugUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "cfg");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void run(String[] args, String originalMessag) {
        if (args.length == 2) {
            switch (args[0]) {
                case "save": {
                    String name = args[1];
                    if (!name.isEmpty()) {
                        Client.instance.configManager.saveUserConfig(name + ".json");
                        DebugUtil.log(EnumChatFormatting.GREEN + "Config " + name + " Saved!");
                    } else {
                        DebugUtil.log(EnumChatFormatting.RED + "?");
                    }
                    break;
                }

                case "load": {
                    String name = args[1];
                    if (!name.isEmpty()) {
                        Client.instance.configManager.loadUserConfig(name + ".json");
                        DebugUtil.log(EnumChatFormatting.GREEN + "Config " + name + " Loaded!");
                    } else {
                        DebugUtil.log(EnumChatFormatting.RED + "?");
                    }
                    break;
                }
            }

        } else {
            DebugUtil.log(EnumChatFormatting.RED + "Usage: config save/load <name>");
        }
    }
}
