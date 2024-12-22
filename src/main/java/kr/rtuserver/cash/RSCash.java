package kr.rtuserver.cash;

import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.commands.Command;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.cash.configuration.CoinConfig;
import kr.rtuserver.cash.dependency.PlaceholderAPI;
import kr.rtuserver.cash.listeners.CoinInteract;
import kr.rtuserver.cash.listeners.PlayerJoin;
import kr.rtuserver.cash.skript.ExprCash;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
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

        registerPermission("rscash.modify", PermissionDefault.OP);
        registerPermission("rscash.check", PermissionDefault.OP);

        cashConfig = new CashConfig(this);
        coinConfig = new CoinConfig(this);

        cashManager = new CashManager(this);

        registerEvent(new PlayerJoin(this));
        registerEvent(new CoinInteract(this));
        registerCommand(new Command(this));

        if (getFramework().isEnabledDependency("Skript")) {
            ExprCash.register();
        }

        if (getFramework().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).register();
    }

    @Override
    public void disable() {
        if (getFramework().isEnabledDependency("PlaceholderAPI")) new PlaceholderAPI(this).unregister();
    }
}
