package kr.rtuserver.cash.configuration;

import kr.rtuserver.cash.cash.Cash;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CashConfig extends RSConfiguration {

    private final Map<String, Cash> map = new HashMap<>();

    public CashConfig(RSPlugin plugin) {
        super(plugin, "Cash.yml", null);
        setup(this);
    }

    private void init() {
        map.clear();
        for (String key : getConfig().getKeys(false)) {
            String displayName = getString(key + ".displayName", "");
            String description = getString(key + ".description", "");
            long limitMax = getLong(key + ".limit.max", Long.MAX_VALUE);
            long limitMin = getLong(key + ".limit.min", Long.MIN_VALUE);
            Cash cash = new Cash(key, displayName, description, limitMax, limitMin);
            map.put(key, cash);
        }
        getPlugin().getConfigurations().initStorage(new ArrayList<>(map.keySet()));
    }

    @Override
    public void reload() {
        if (isChanged()) {
            getPlugin().getConfigurations().initStorage(new ArrayList<>(map.keySet()));
        }
    }
}
