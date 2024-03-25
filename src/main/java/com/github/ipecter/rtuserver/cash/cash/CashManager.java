package com.github.ipecter.rtuserver.cash.cash;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.managers.ConfigManager;
import com.github.ipecter.rtuserver.lib.util.data.Storage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class CashManager {

    private final ConfigManager config = RSCash.getInstance().getConfigManager();
    private final Storage storage = RSCash.getInstance().getStorage();

    public void addPlayer(UUID uuid) {
        for (String cash : config.getCashMap().keySet()) {
            JsonObject result = storage.get(cash, Pair.of("uuid", uuid.toString()));
            if (result == null) {
                JsonObject object = new JsonObject();
                object.addProperty("uuid", uuid.toString());
                object.addProperty("value", 0);
                storage.add(cash, object);
            } else {
                JsonElement element = result.get("value");
                if (element == null || element.isJsonNull()) {
                    storage.set(cash, Pair.of("uuid", uuid.toString()), Pair.of("value", 0));
                }
            }
        }
    }

    public void setPlayerCash(UUID uuid, PlayerCash playerCash) {
        storage.set(playerCash.getName(), Pair.of("uuid", uuid.toString()), Pair.of("value", playerCash.getCash()));
    }

    public Integer getPlayerCash(UUID uuid, String cash) {
        JsonObject result = storage.get(cash, Pair.of("uuid", uuid.toString()));
        if (result == null || result.isJsonNull()) return null;
        JsonElement element = result.get("value");
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }
        return null;
    }
}
