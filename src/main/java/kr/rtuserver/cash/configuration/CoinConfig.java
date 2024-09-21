package kr.rtuserver.cash.configuration;

import com.github.ipecter.rtuserver.lib.bukkit.api.RSPlugin;
import com.github.ipecter.rtuserver.lib.bukkit.api.config.RSConfiguration;
import com.github.ipecter.rtuserver.lib.bukkit.api.utility.compatible.ItemCompat;
import com.github.ipecter.rtuserver.lib.bukkit.api.utility.format.ComponentFormatter;
import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.Coin;
import lombok.Getter;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CoinConfig extends RSConfiguration {

    private final Map<String, Coin> map = new HashMap<>();
    private final CashConfig cashConfig = RSCash.getInstance().getCashConfig();

    public CoinConfig(RSPlugin plugin) {
        super(plugin, "Coin.yml", null);
        setup(this);
    }

    private void init() {
        map.clear();
        List<String> list = new ArrayList<>();
        for (String key : getConfig().getKeys(false)) {
            if (!cashConfig.getMap().containsKey(key)) {
                getPlugin().console(ComponentFormatter.mini("<red>Cash: " + key + "is not exist. Check Coin.yml.</red>"));
                getPlugin().console(ComponentFormatter.mini("<red>캐시: " + key + "는 존재하지 않습니다 Coin.yml을 확인해보세요</red>"));
                continue;
            }
            list.add(key);
        }
        for (String cash : list) {
            ConfigurationSection section = getConfigurationSection(cash);
            if (section == null) continue;
            for (String key : section.getKeys(false)) {
                if (ItemCompat.from(key) == null) {
                    getPlugin().console(ComponentFormatter.mini("<red>Item: " + key + "is not exist. Check Coin.yml.</red>"));
                    getPlugin().console(ComponentFormatter.mini("<red>아이템: " + key + "는 존재하지 않습니다 Coin.yml을 확인해보세요</red>"));
                    continue;
                }
                int value = section.getInt(key, 0);
                Coin coin = new Coin(cash, key, value);
                map.put(key, coin);
            }
        }
    }
}
