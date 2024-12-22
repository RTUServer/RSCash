package kr.rtuserver.cash.listeners;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.cash.Coin;
import kr.rtuserver.cash.cash.PlayerCash;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.cash.configuration.CoinConfig;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.utility.compatible.ItemCompat;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CoinInteract extends RSListener<RSCash> {

    private final CashConfig cashConfig;
    private final CoinConfig coinConfig;
    private final CashManager cash;

    public CoinInteract(RSCash plugin) {
        super(plugin);
        this.cashConfig = plugin.getCashConfig();
        this.coinConfig = plugin.getCoinConfig();
        this.cash = plugin.getCashManager();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack itemStack = e.getItem();
        if (itemStack == null || itemStack.getType().isEmpty()) return;
        Player player = e.getPlayer();
        String id = ItemCompat.to(itemStack);
        if (cashConfig.getMap().containsKey(id)) {
            Coin coin = coinConfig.getMap().get(id);
            Long cashAmount = cash.getPlayerCash(player.getUniqueId(), coin.cash());
            PlayerInventory inventory = player.getInventory();
            ItemStack coinItem = e.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
            int amount = coinItem.getAmount();
            long newCashAmount = cashAmount;
            int itemAmount;
            if (player.isSneaking()) {
                newCashAmount += (amount * coin.value());
                itemAmount = 0;
            } else {
                newCashAmount += coin.value();
                itemAmount = amount - 1;
            }
            itemStack.setAmount(itemAmount);
            if (itemStack.getAmount() == itemAmount)
                cash.setPlayerCash(player.getUniqueId(), new PlayerCash(coin.cash(), newCashAmount));
        }
    }
}
