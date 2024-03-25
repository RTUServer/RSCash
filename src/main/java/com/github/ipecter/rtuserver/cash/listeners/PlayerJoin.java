package com.github.ipecter.rtuserver.cash.listeners;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.lib.plugin.RSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends RSListener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        RSCash.getInstance().getCashManager().addPlayer(e.getPlayer().getUniqueId());
    }
}
