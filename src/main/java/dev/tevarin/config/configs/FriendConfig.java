package dev.tevarin.config.configs;

import com.google.gson.JsonObject;
import dev.tevarin.Client;
import dev.tevarin.config.Config;

public class FriendConfig extends Config {
    public FriendConfig() {
        super("friends.json");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();
        for (String name : Client.instance.friendManager.getFriends()) {
            JsonObject friendObject = new JsonObject();
            friendObject.addProperty("name", name);
            object.add(String.valueOf(Client.instance.friendManager.getFriends().indexOf(name)), friendObject);
        }
        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
        for (String name : Client.instance.friendManager.getFriends()) {
            if (object.has(name)) {
                JsonObject friendObject = object.get(name).getAsJsonObject();

                Client.instance.friendManager.add(friendObject.get("name").getAsString());
            }
        }
    }
}
