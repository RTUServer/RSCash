package com.github.ipecter.rtuserver.cash.listeners;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.cash.cash.Coin;
import com.github.ipecter.rtuserver.cash.cash.PlayerCash;
import com.github.ipecter.rtuserver.cash.config.CashConfig;
import com.github.ipecter.rtuserver.cash.config.CoinConfig;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.listener.RSListener;
import com.github.ipecter.rtuserver.lib.util.support.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CoinInteract extends RSListener {

    private final CashConfig cashConfig = RSCash.getInstance().getCashConfig();
    private final CoinConfig coinConfig = RSCash.getInstance().getCoinConfig();
    private final CashManager cash = RSCash.getInstance().getCashManager();

    public CoinInteract(RSPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack itemStack = e.getItem();
        if (itemStack == null || itemStack.getType().isEmpty()) return;
        Player player = e.getPlayer();
        String id = ItemUtil.fromItemStack(itemStack);
        if (cashConfig.getMap().containsKey(id)) {
            Coin coin = coinConfig.getMap().get(id);
            Integer cashAmount = cash.getPlayerCash(player.getUniqueId(), coin.getCash());
            PlayerInventory inventory = player.getInventory();
            ItemStack coinItem = e.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
            int amount = coinItem.getAmount();
            int newCashAmount = cashAmount;
            int itemAmount;
            if (player.isSneaking()) {
                newCashAmount += (amount * coin.getValue());
                itemAmount = 0;
            } else {
                newCashAmount += coin.getValue();
                itemAmount = amount - 1;
            }
            itemStack.setAmount(itemAmount);
            if (itemStack.getAmount() == itemAmount)
                cash.setPlayerCash(player.getUniqueId(), new PlayerCash(coin.getCash(), newCashAmount));
        }
    }
}
