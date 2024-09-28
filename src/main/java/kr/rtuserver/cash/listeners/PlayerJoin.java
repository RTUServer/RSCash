package kr.rtuserver.cash.listeners;

import kr.rtuserver.lib.bukkit.api.RSPlugin;
import kr.rtuserver.lib.bukkit.api.listener.RSListener;
import kr.rtuserver.cash.RSCash;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends RSListener {
    public PlayerJoin(RSPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        RSCash.getInstance().getCashManager().addPlayer(e.getPlayer().getUniqueId());
    }
}
