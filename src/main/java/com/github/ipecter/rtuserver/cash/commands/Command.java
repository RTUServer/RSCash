package com.github.ipecter.rtuserver.cash.commands;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.Cash;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.cash.cash.PlayerCash;
import com.github.ipecter.rtuserver.cash.managers.ConfigManager;
import com.github.ipecter.rtuserver.lib.plugin.RSCommand;
import com.github.ipecter.rtuserver.lib.plugin.command.CommandData;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command extends RSCommand {
    private final ConfigManager config = RSCash.getInstance().getConfigManager();
    private final CashManager cashManager = RSCash.getInstance().getCashManager();

    public Command() {
        super("rscash");
    }

    @Override
    public void command(CommandData data) {
        String reload = config.getTranslation("command.reload");
        String modify = config.getTranslation("command.modify");
        String check = config.getTranslation("command.check");
        String arg0 = data.args(0).toLowerCase();
        switch (arg0) {
            case "reload" -> {
                if (hasPermission("rscash.reload")) {
                    config.init();
                    for (Player player : Bukkit.getOnlinePlayers()) cashManager.addPlayer(player.getUniqueId());
                    sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("reload")));
                } else
                    sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("noPermission")));
            }
            case "modify" -> {
                if (hasPermission("rscash.modify")) {
                    Player other = Bukkit.getPlayer(data.args(1));
                    if (other != null) {
                        String cash = data.args(2);
                        if (config.getCashMap().containsKey(cash)) {
                            Cash cashData = config.getCashMap().get(cash);
                            Integer playerData = cashManager.getPlayerCash(other.getUniqueId(), cash);
                            if (playerData != null) {
                                int value = playerData;
                                String arg3 = data.args(3);
                                if (arg3.matches("^([+\\-]?\\d+|[0-9]+)$")) {
                                    if (arg3.startsWith("+"))
                                        value = playerData + Integer.parseInt(arg3.substring(1));
                                    else if (arg3.startsWith("-"))
                                        value = playerData - Integer.parseInt(arg3.substring(1));
                                    else value = Integer.parseInt(arg3);
                                    if (value < 0) {
                                        sendMessage(ComponentUtil.formatted(getSender(), replacePlayerAndCash(other, cashData, config.getTranslation("prefix") + config.getTranslation("modify.overMin")).replace("{previous}", String.valueOf(playerData)).replace("{current}", String.valueOf(0))));
                                        cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cash, 0));
                                    } else if (value > cashData.getMaxCash()) {
                                        sendMessage(ComponentUtil.formatted(getSender(), replacePlayerAndCash(other, cashData, config.getTranslation("prefix") + config.getTranslation("modify.overMax")).replace("{previous}", String.valueOf(playerData)).replace("{current}", String.valueOf(cashData.getMaxCash()))));
                                        cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cash, cashData.getMaxCash()));
                                    } else {
                                        sendMessage(ComponentUtil.formatted(getSender(), replacePlayerAndCash(other, cashData, config.getTranslation("prefix") + config.getTranslation("modify.success")).replace("{previous}", String.valueOf(playerData)).replace("{current}", String.valueOf(value))));
                                        cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cash, value));
                                    }
                                } else
                                    sendMessage(ComponentUtil.formatted(getSender(), replacePlayer(other, config.getTranslation("prefix") + config.getTranslation("modify.wrongFormat"))));
                            } else {
                                sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.playerData")));
                            }
                        } else {
                            sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.cashData")));
                        }
                    } else {
                        sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.player")));
                    }
                } else
                    sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("noPermission")));
            }
            case "check" -> {
                if (hasPermission("rscash.check")) {
                    Player other = Bukkit.getPlayer(data.args(1));
                    if (other != null) {
                        String cash = data.args(2);
                        if (config.getCashMap().containsKey(cash)) {
                            Integer value = cashManager.getPlayerCash(other.getUniqueId(), cash);
                            if (value != null) {
                                sendMessage(ComponentUtil.formatted(getSender(), replacePlayerAndCash(other, config.getCashMap().get(cash), config.getTranslation("prefix") + config.getTranslation("check.success")).replace("{value}", String.valueOf(value))));
                            } else {
                                sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.playerData")));
                            }
                        } else {
                            sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.cashData")));
                        }
                    } else {
                        sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("notFound.player")));
                    }
                } else
                    sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("noPermission")));
            }
            default -> wrongUsage();
        }
    }

    private void wrongUsage() {
        sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("prefix") + config.getTranslation("wrongUsage.wrongUsage")));
        sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("wrongUsage.modify")));
        sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("wrongUsage.check")));
        if (isOp()) sendMessage(ComponentUtil.formatted(getSender(), config.getTranslation("wrongUsage.reload")));
    }

    private String replacePlayer(Player player, String message) {
        return message.replace("{playerName}", player.getName()).replace("{playerDisplayName}", player.getDisplayName());
    }

    private String replacePlayerAndCash(Player player, Cash cash, String message) {
        return message.replace("{playerName}", player.getName()).replace("{playerDisplayName}", player.getDisplayName()).replace("{cashName}", cash.getName()).replace("{cashDisplayName}", cash.getDisplayName());
    }

    @Override
    public List<String> tabComplete(CommandData data) {
        if (data.length(1)) {
            List<String> list = new ArrayList<>();
            if (hasPermission("rscash.reload")) list.add("reload");
            if (hasPermission("rscash.check")) list.add("check");
            if (hasPermission("rscash.modify")) list.add("modify");
            return list;
        }
        if (data.length(2)) {
            String cmd = data.args(0);
            if (List.of("check", "modify").contains(cmd) && hasPermission("rscash." + cmd)) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
        }
        if (data.length(3)) {
            String cmd = data.args(0);
            if (List.of("check", "modify").contains(cmd) && hasPermission("rscash." + cmd)) {
                return new ArrayList<>(config.getCashMap().keySet());
            }
        }
        if (data.length(4)) {
            String cmd = data.args(0);
            if (cmd.equalsIgnoreCase("modify") && hasPermission("rscash.modify")) {
                return List.of("+1", "1", "-1");
            }
        }
        return List.of();
    }
}
