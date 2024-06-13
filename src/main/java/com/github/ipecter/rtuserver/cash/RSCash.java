package com.github.ipecter.rtuserver.cash;

import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.cash.commands.Command;
import com.github.ipecter.rtuserver.cash.config.CashConfig;
import com.github.ipecter.rtuserver.cash.config.CoinConfig;
import com.github.ipecter.rtuserver.cash.dependency.PlaceholderAPI;
import com.github.ipecter.rtuserver.cash.listeners.CoinInteract;
import com.github.ipecter.rtuserver.cash.listeners.PlayerJoin;
import com.github.ipecter.rtuserver.lib.RSLib;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;
import lombok.Getter;
import org.bukkit.permissions.PermissionDefault;

public class RSCash extends RSPlugin {

    @Getter
    private static RSCash instance;
    @Getter
    private CashConfig cashConfig;
    @Getter
    private CoinConfig coinConfig;
    @Getter
    private CashManager cashManager;

    @Override
    public void enable() {
        instance = this;

        registerPermission(getName() + ".modify", PermissionDefault.OP);
        registerPermission(getName() + ".check", PermissionDefault.OP);

        cashConfig = new CashConfig(this);
        coinConfig = new CoinConfig(this);

        cashManager = new CashManager();

        registerEvent(new PlayerJoin(this));
        registerEvent(new CoinInteract(this));
        registerCommand(new Command(this));

        if (RSLib.getInstance().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).register();
    }

    @Override
    public void disable() {
        if (RSLib.getInstance().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).unregister();
    }
}
