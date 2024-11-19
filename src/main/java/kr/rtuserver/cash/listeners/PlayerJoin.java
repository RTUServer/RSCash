package kr.rtuserver.cash.listeners;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
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
