package kr.rtuserver.cash.dependency;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.framework.bukkit.api.dependencies.RSPlaceholder;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPI extends RSPlaceholder<RSCash> {

    private final CashManager cashManager;

    public PlaceholderAPI(RSCash plugin) {
        super(plugin);
        this.cashManager = plugin.getCashManager();
    }

    @Override
    public String request(OfflinePlayer offlinePlayer, String[] params) {
        if (params[0] != null) {
            Integer value = cashManager.getPlayerCash(offlinePlayer.getUniqueId(), params[0]);
            if (value != null) {
                return String.valueOf(value);
            } else return "ERROR: Not found player data";
        }
        return "ERROR: Not found cash name";
    }
}
