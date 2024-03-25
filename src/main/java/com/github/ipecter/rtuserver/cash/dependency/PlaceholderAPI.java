package com.github.ipecter.rtuserver.cash.dependency;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.lib.plugin.RSPapi;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPI extends RSPapi {

    private final CashManager cashManager = RSCash.getInstance().getCashManager();

    public PlaceholderAPI(RSPlugin plugin) {
        super(plugin);
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
