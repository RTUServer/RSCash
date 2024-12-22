package kr.rtuserver.cash.listeners;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends RSListener<RSCash> {

    public PlayerJoin(RSCash plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        getPlugin().getCashManager().addPlayer(e.getPlayer().getUniqueId());
    }
}
