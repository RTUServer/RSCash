package com.github.ipecter.rtuserver.cash;

import com.github.ipecter.rtuserver.cash.commands.Command;
import com.github.ipecter.rtuserver.cash.dependency.PlaceholderAPI;
import com.github.ipecter.rtuserver.cash.listeners.CoinInteract;
import com.github.ipecter.rtuserver.cash.listeners.PlayerJoin;
import com.github.ipecter.rtuserver.cash.managers.ConfigManager;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.lib.RSLib;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;
import com.github.ipecter.rtuserver.lib.util.data.Storage;
import lombok.Getter;
import lombok.Setter;

public class RSCash extends RSPlugin {

    @Getter
    private static RSCash instance;

    @Getter
    private final ConfigManager configManager = new ConfigManager();

    @Getter
    private CashManager cashManager;

    @Getter
    @Setter
    private Storage storage;

    public RSCash() {
        super(ComponentUtil.miniMessage("<gradient:#f7ff66:#66ff87>【 RSCash 】</gradient>"));
    }

    @Override
    public void enable() {
        instance = this;

        configManager.init();
        cashManager = new CashManager();

        registerEvent(new PlayerJoin());
        registerEvent(new CoinInteract());
        registerCommand(new Command());

        if (RSLib.getInstance().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).register();
        console(ComponentUtil.miniMessage("<green>Enable!</green>"));
    }

    @Override
    public void disable() {
        if (RSLib.getInstance().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).unregister();
        console(ComponentUtil.miniMessage("<red>Disable!</red>"));
    }
}
