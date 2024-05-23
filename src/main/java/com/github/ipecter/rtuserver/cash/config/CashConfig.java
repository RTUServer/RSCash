package com.github.ipecter.rtuserver.cash.config;

import com.github.ipecter.rtuserver.cash.cash.Cash;
import com.github.ipecter.rtuserver.lib.RSLib;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.config.RSConfiguration;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CashConfig extends RSConfiguration {

    private final Map<String, Cash> map = new HashMap<>();

    public CashConfig(RSPlugin plugin) {
        super(plugin, "Cash.yml", 1);
        setup(this);
    }

    private void init() {
        map.clear();
        for (String key : getConfig().getKeys(false)) {
            String displayName = getString(key + ".displayName", "");
            String description = getString(key + ".description", "");
            int maxCash = getInt(key + ".maxCash", 100000000);
            Cash cash = new Cash(key, displayName, description, maxCash);
            map.put(key, cash);
        }
    }
}
