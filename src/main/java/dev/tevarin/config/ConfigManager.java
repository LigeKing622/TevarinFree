package dev.tevarin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrayList;
import dev.tevarin.Client;
import dev.tevarin.config.configs.FriendConfig;
import dev.tevarin.config.configs.HudConfig;
import dev.tevarin.config.configs.ModuleConfig;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
public class ConfigManager {
    public static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(Client.mc.mcDataDir, "Tevarin");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        if (!dir.exists()) {
            dir.mkdir();
        }

        configs.add(new ModuleConfig());
        configs.add(new HudConfig());
        configs.add(new FriendConfig());
    }

    public void loadConfig(final String name) {
        JsonParser jsonParser = new JsonParser();
        final File file = new File(ConfigManager.dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals(name)) {
                    try {
                        config.loadConfig(jsonParser.parse((Reader) new FileReader(file)).getAsJsonObject());
                    } catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            this.saveConfig(name);
        }
    }

    public void loadUserConfig(final String name) {
        JsonParser jsonParser = new JsonParser();
        final File file = new File(ConfigManager.dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (final Config config : ConfigManager.configs) {
                if (config.getName().equals("modules.json")) {
                    try {
                        config.loadConfig(jsonParser.parse((Reader) new FileReader(file)).getAsJsonObject());
                    } catch (FileNotFoundException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else {
            System.out.println("Config " + name + " doesn't exist, creating a new one...");
            this.saveUserConfig(name);
        }
    }

    public void saveConfig(String name) {
        File file = new File(dir, name);

        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void saveUserConfig(String name) {
        File file = new File(dir, name);

        try {
            System.out.println("Saving config: " + name);
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals("modules.json")) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void loadAllConfig() {
        System.out.println("Loading all configs...");
        configs.forEach(it -> loadConfig(it.getName()));
    }

    public void saveAllConfig() {
        System.out.println("Saving all configs...");
        configs.forEach(it -> saveConfig(it.getName()));
    }

    private static final String EXTENSION = ".json";

    public List<String> getConfigs() {
        Stream<Path> filesStream;

        try {
            filesStream = walk(dir.toPath());
        } catch (IOException e) {
            return Collections.emptyList();
        }

        return filesStream // @off
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(s -> s.endsWith(EXTENSION))
                .map(s -> s.substring(0, s.length() - EXTENSION.length()))
                .collect(Collectors.toCollection(ObjectArrayList::new)); // @on
    }

}
