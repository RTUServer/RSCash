package kr.rtuserver.cash;

import com.github.ipecter.rtuserver.lib.bukkit.api.RSPlugin;
import com.github.ipecter.rtuserver.lib.bukkit.api.core.RSFramework;
import com.google.inject.Inject;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.commands.Command;
import kr.rtuserver.cash.config.CashConfig;
import kr.rtuserver.cash.config.CoinConfig;
import kr.rtuserver.cash.dependency.PlaceholderAPI;
import kr.rtuserver.cash.listeners.CoinInteract;
import kr.rtuserver.cash.listeners.PlayerJoin;
import lombok.Getter;
import org.bukkit.permissions.PermissionDefault;

public class RSCash extends RSPlugin {

    @Getter
    private static RSCash instance;
    @Inject
    private RSFramework framework;
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

        if (framework.isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).register();
    }

    @Override
    public void disable() {
        if (framework.isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).unregister();
    }
}
