package kr.rtuserver.cash.cash;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.framework.bukkit.api.storage.Storage;
import kr.rtuserver.framework.bukkit.api.utility.platform.JSON;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.UUID;

public class CashManager {

    private final RSCash plugin;
    private final CashConfig cashConfig;

    public CashManager(RSCash plugin) {
        this.plugin = plugin;
        this.cashConfig = plugin.getCashConfig();
    }

    public void addPlayer(UUID uuid) {
        Storage storage = plugin.getStorage();
        for (String cash : cashConfig.getMap().keySet()) {
            List<JsonObject> result = storage.get(cash, Pair.of("uuid", uuid.toString())).join();
            if (result.isEmpty()) {
                storage.add(cash, JSON.of("uuid", uuid.toString()).append("value", 0L).get());
            } else {
                JsonElement element = result.get(0).get("value");
                if (element == null || element.isJsonNull()) {
                    storage.set(cash, Pair.of("uuid", uuid.toString()), Pair.of("value", 0));
                }
            }
        }
    }

    public void setPlayerCash(UUID uuid, PlayerCash playerCash) {
        Storage storage = plugin.getStorage();
        storage.set(playerCash.name(), Pair.of("uuid", uuid.toString()), Pair.of("value", playerCash.cash())).join();
    }

    public Long getPlayerCash(UUID uuid, String cash) {
        Storage storage = plugin.getStorage();
        List<JsonObject> result = storage.get(cash, Pair.of("uuid", uuid.toString())).join();
        if (result.isEmpty() || result.get(0).isJsonNull()) return null;
        JsonElement element = result.get(0).get("value");
        if (element != null && !element.isJsonNull()) return element.getAsLong();
        return null;
    }
}
