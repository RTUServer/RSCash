package kr.rtuserver.cash.dependency;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.dependencies.RSPlaceholder;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPI extends RSPlaceholder {

    private final CashManager cashManager = RSCash.getInstance().getCashManager();

    public PlaceholderAPI(RSPlugin plugin) {
        super(plugin);
    }

    @Override
    public String request(OfflinePlayer offlinePlayer, String[] params) {
        if (params[0] != null) {
            Long value = cashManager.getPlayerCash(offlinePlayer.getUniqueId(), params[0]);
            if (value != null) {
                return String.valueOf(value);
            } else return "ERROR: Not found player data";
        }
        return "ERROR: Not found cash name";
    }
}
