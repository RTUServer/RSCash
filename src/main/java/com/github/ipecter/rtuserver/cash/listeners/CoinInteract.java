package com.github.ipecter.rtuserver.cash.listeners;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.cash.cash.Coin;
import com.github.ipecter.rtuserver.cash.cash.PlayerCash;
import com.github.ipecter.rtuserver.cash.managers.ConfigManager;
import com.github.ipecter.rtuserver.lib.util.support.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CoinInteract implements Listener {

    private final ConfigManager config = RSCash.getInstance().getConfigManager();
    private final CashManager cash = RSCash.getInstance().getCashManager();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        ItemStack itemStack = e.getItem();
        if (itemStack == null) return;
        Player player = e.getPlayer();
        String id = ItemUtil.fromItemStack(itemStack);
        if (config.getCoinMap().containsKey(id)) {
            Coin coin = config.getCoinMap().get(id);
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
            if (itemAmount <= 0) {
                itemStack.setAmount(itemAmount); //TODO: 0이 되면 사라지는지 체크
            } else {
                itemStack.setAmount(itemAmount);
            }
            cash.setPlayerCash(player.getUniqueId(), new PlayerCash(coin.getCash(), newCashAmount));
        }
    }
}
