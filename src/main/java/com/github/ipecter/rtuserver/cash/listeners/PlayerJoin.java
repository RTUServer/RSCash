package com.github.ipecter.rtuserver.cash.listeners;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.listener.RSListener;
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
