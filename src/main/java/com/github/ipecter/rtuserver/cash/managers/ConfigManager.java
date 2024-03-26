package com.github.ipecter.rtuserver.cash.managers;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.Cash;
import com.github.ipecter.rtuserver.cash.cash.Coin;
import com.github.ipecter.rtuserver.cash.managers.config.DataConfig;
import com.github.ipecter.rtuserver.cash.managers.config.SettingConfig;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;
import com.github.ipecter.rtuserver.lib.util.common.FileUtil;
import com.github.ipecter.rtuserver.lib.util.data.JsonStorage;
import com.github.ipecter.rtuserver.lib.util.data.MongoInfo;
import com.github.ipecter.rtuserver.lib.util.data.MongoStorage;
import com.github.ipecter.rtuserver.lib.util.data.Storage;
import com.github.ipecter.rtuserver.lib.util.support.ItemUtil;
import com.google.common.io.Files;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    @Getter
    private final SettingConfig setting = new SettingConfig();
    @Getter
    private final DataConfig data = new DataConfig();
    private final Map<String, String> translationMap = new HashMap<>();

    private final List<String> storageList = new ArrayList<>();
    @Getter
    private final Map<String, Cash> cashMap = new HashMap<>();
    @Getter //<아이템ID, 코인>
    private final Map<String, Coin> coinMap = new HashMap<>();

    public void init() {
        initSetting(FileUtil.copyResource(RSCash.getInstance(), "Setting.yml"));
        initTranslation(FileUtil.copyResource(RSCash.getInstance(), "Translations", "Locale_" + setting.getLocale() + ".yml"));
        initCash(FileUtil.copyResource(RSCash.getInstance(), "Cash.yml"));
        initCoin(FileUtil.copyResource(RSCash.getInstance(), "Coin.yml"));
        initData(FileUtil.copyResource(RSCash.getInstance(), "Data.yml"));
    }

    private void initSetting(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        setting.setEnablePlugin(config.getBoolean("enablePlugin", setting.isEnablePlugin()));
        setting.setLocale(config.getString("locale", setting.getLocale()));
    }

    private void initCash(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        cashMap.clear();
        for (String key : config.getKeys(false)) {
            String displayName = config.getString(key + ".displayName", "");
            String description = config.getString(key + ".description", "");
            int maxCash = config.getInt(key + ".maxCash", 100000000);
            Cash cash = new Cash(key, displayName, description, maxCash);
            cashMap.put(key, cash);
        }
    }

    private void initCoin(File file) {
        RSCash instance = RSCash.getInstance();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        coinMap.clear();
        List<String> list = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            if (!cashMap.containsKey(key)) {
                instance.console(ComponentUtil.miniMessage("<red>Cash: " + key + "is not exist. Check Coin.yml.</red>"));
                instance.console(ComponentUtil.miniMessage("<red>캐시: " + key + "는 존재하지 않습니다 Coin.yml을 확인해보세요</red>"));
                continue;
            }
            list.add(key);
        }
        for (String cash : list) {
            ConfigurationSection section = config.getConfigurationSection(cash);
            if (section == null) continue;
            for (String key : section.getKeys(false)) {
                if (ItemUtil.fromId(key) == null) {
                    instance.console(ComponentUtil.miniMessage("<red>아이템: " + key + "is not exist. Check Coin.yml.</red>"));
                    instance.console(ComponentUtil.miniMessage("<red>아이템: " + key + "는 존재하지 않습니다 Coin.yml을 확인해보세요</red>"));
                    continue;
                }
                int value = section.getInt(key, 0);
                Coin coin = new Coin(cash, key, value);
                coinMap.put(key, coin);
            }
        }
    }

    private void initData(File file) {
        String dataFolder = RSCash.getInstance().getDataFolder().getPath();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        data.setSavePeriod(config.getInt("savePeriod", data.getSavePeriod()));
        data.setDatabaseUse(config.getBoolean("database.use", data.isDatabaseUse()));
        data.setDatabaseIp(config.getString("database.ip", data.getDatabaseIp()));
        data.setDatabasePort(config.getString("database.port", data.getDatabasePort()));
        data.setDatabaseUsername(config.getString("database.username", data.getDatabaseUsername()));
        data.setDatabasePassword(config.getString("database.password", data.getDatabasePassword()));
        data.setDatabaseName(config.getString("database.database", data.getDatabaseName()));
        Storage storage = RSCash.getInstance().getStorage();
        boolean notChanged = new ArrayList<>(cashMap.keySet()).equals(storageList);
        if (data.isDatabaseUse()) {
            if (notChanged && storage instanceof MongoStorage) return;
            if (storage != null) storage.close();
            RSCash.getInstance().setStorage(new MongoStorage(new MongoInfo(data.getDatabaseIp(), data.getDatabasePort(), data.getDatabaseUsername(), data.getDatabasePassword(), data.getDatabaseName())));
            RSCash.getInstance().console(ComponentUtil.miniMessage("Storage: MongoDB"));
        } else {
            for (String cash : cashMap.keySet()) {
                FileUtil.getResource(dataFolder + "/Data", cash + ".json");
            }
            File[] listFiles = FileUtil.getResourceFolder(dataFolder + "/Data").listFiles();
            File[] files = new File[cashMap.size()];
            assert listFiles != null;
            int index = 0;
            for (File f : listFiles) {
                if (cashMap.containsKey(Files.getNameWithoutExtension(f.getName()))) {
                    files[index] = f;
                    index++;
                }
            }
            if (notChanged && storage instanceof JsonStorage) return;
            if (storage != null) storage.close();
            RSCash.getInstance().setStorage(new JsonStorage(files, data.getSavePeriod()));
            RSCash.getInstance().console(ComponentUtil.miniMessage("Storage: JsonFile"));
        }
        storageList.clear();
        storageList.addAll(cashMap.keySet());
    }

    private void initTranslation(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        translationMap.clear();
        for (String key : config.getKeys(true)) {
            if (key.equals("prefix")) {
                String prefixText = config.getString("prefix", "");
                translationMap.put(key, prefixText.isEmpty() ? MiniMessage.miniMessage().serialize(RSCash.getInstance().getPrefix()) : prefixText);
            } else {
                translationMap.put(key, config.getString(key));
            }
        }
        FileUtil.copyResource(RSCash.getInstance(), "Translations", "Locale_KR.yml");
    }

    public String getTranslation(String key) {
        return translationMap.getOrDefault(key, "");
    }
}
