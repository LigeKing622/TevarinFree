package dev.tevarin.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tevarin.Client;
import dev.tevarin.config.Config;
import dev.tevarin.module.Module;
import dev.tevarin.value.Value;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ColorValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
public class ModuleConfig extends Config {
    public ModuleConfig() {
        super("modules.json");
    }


    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();

        for (Module module : Client.instance.moduleManager.getModuleMap().values()) {

            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("state", module.getState());
            moduleObject.addProperty("key", module.getKey());

            JsonObject valuesObject = new JsonObject();

            for (Value<?> value : module.getValues()) {
                if (value instanceof NumberValue) {
                    valuesObject.addProperty(value.getName(), ((NumberValue) value).getValue());
                } else if (value instanceof BoolValue) {
                    valuesObject.addProperty(value.getName(), ((BoolValue) value).getValue());
                } else if (value instanceof ModeValue) {
                    valuesObject.addProperty(value.getName(), ((ModeValue) value).getConfigValue());
                } else if (value instanceof ColorValue) {
                    valuesObject.addProperty(value.getName(), ((ColorValue) value).getColor());
                }
            }

            moduleObject.add("values", valuesObject);
            object.add(module.getName(), moduleObject);
        }

        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
        for (Module module : Client.instance.moduleManager.getModuleMap().values()) {


            if (object.has(module.getName())) {

                JsonObject moduleObject = object.get(module.getName()).getAsJsonObject();

                if (moduleObject.has("state")) {
                    module.setState(moduleObject.get("state").getAsBoolean());
                }

                if (moduleObject.has("key")) {
                    module.setKey(moduleObject.get("key").getAsInt());
                }

                if (moduleObject.has("values")) {
                    JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();

                    for (Value<?> value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());
                            if (value instanceof NumberValue) {
                                ((NumberValue) value).setValue(theValue.getAsNumber().doubleValue());
                            } else if (value instanceof BoolValue) {
                                ((BoolValue) value).setValue(theValue.getAsBoolean());
                            } else if (value instanceof ModeValue) {
                                ((ModeValue) value).setMode(theValue.getAsString());
                            } else if (value instanceof ColorValue) {
                                Color color = new Color(theValue.getAsInt());
                                ((ColorValue) value).setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()).getRGB());
                            }
                        }
                    }
                }
            }
        }
    }
}
