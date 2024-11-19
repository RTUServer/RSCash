package kr.rtuserver.cash.cash;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.framework.bukkit.api.storage.Storage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.UUID;

public class CashManager {

    private final CashConfig cashConfig = RSCash.getInstance().getCashConfig();

    public void addPlayer(UUID uuid) {
        Storage storage = RSCash.getInstance().getStorage();
        for (String cash : cashConfig.getMap().keySet()) {
            List<JsonObject> result = storage.get(cash, Pair.of("uuid", uuid.toString()));
            if (result.isEmpty()) {
                JsonObject object = new JsonObject();
                object.addProperty("uuid", uuid.toString());
                object.addProperty("value", 0);
                storage.add(cash, object);
            } else {
                JsonElement element = result.get(0).get("value");
                if (element == null || element.isJsonNull()) {
                    storage.set(cash, Pair.of("uuid", uuid.toString()), Pair.of("value", 0));
                }
            }
        }
    }

    public void setPlayerCash(UUID uuid, PlayerCash playerCash) {
        Storage storage = RSCash.getInstance().getStorage();
        storage.set(playerCash.name(), Pair.of("uuid", uuid.toString()), Pair.of("value", playerCash.cash()));
    }

    public Integer getPlayerCash(UUID uuid, String cash) {
        Storage storage = RSCash.getInstance().getStorage();
        List<JsonObject> result = storage.get(cash, Pair.of("uuid", uuid.toString()));
        if (result.isEmpty() || result.get(0).isJsonNull()) return null;
        JsonElement element = result.get(0).get("value");
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }
        return null;
    }
}
