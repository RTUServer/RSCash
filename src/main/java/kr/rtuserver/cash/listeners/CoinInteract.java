package kr.rtuserver.cash.listeners;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.cash.Coin;
import kr.rtuserver.cash.cash.PlayerCash;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.cash.configuration.CoinConfig;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.utility.compatible.ItemCompat;
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
        String id = ItemCompat.to(itemStack);
        if (cashConfig.getMap().containsKey(id)) {
            Coin coin = coinConfig.getMap().get(id);
            Integer cashAmount = cash.getPlayerCash(player.getUniqueId(), coin.cash());
            PlayerInventory inventory = player.getInventory();
            ItemStack coinItem = e.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
            int amount = coinItem.getAmount();
            int newCashAmount = cashAmount;
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
