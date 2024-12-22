package kr.rtuserver.cash.configuration;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.Cash;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CashConfig extends RSConfiguration<RSCash> {

    private final Map<String, Cash> map = new HashMap<>();

    public CashConfig(RSCash plugin) {
        super(plugin, "Cash.yml", null);
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
        getPlugin().getConfigurations().initStorage(new ArrayList<>(map.keySet()));
    }

    @Override
    public void reload() {
        if (isChanged()) {
            getPlugin().getConfigurations().initStorage(new ArrayList<>(map.keySet()));
        }
    }
}
