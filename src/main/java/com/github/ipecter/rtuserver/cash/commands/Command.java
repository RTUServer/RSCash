package com.github.ipecter.rtuserver.cash.commands;

import com.github.ipecter.rtuserver.cash.RSCash;
import com.github.ipecter.rtuserver.cash.cash.Cash;
import com.github.ipecter.rtuserver.cash.cash.CashManager;
import com.github.ipecter.rtuserver.cash.cash.PlayerCash;
import com.github.ipecter.rtuserver.cash.config.CashConfig;
import com.github.ipecter.rtuserver.cash.config.CoinConfig;
import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.command.CommandData;
import com.github.ipecter.rtuserver.lib.plugin.command.CommandType;
import com.github.ipecter.rtuserver.lib.plugin.command.RSCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command extends RSCommand {

    private final CashManager cashManager = RSCash.getInstance().getCashManager();
    private final CashConfig cashConfig = RSCash.getInstance().getCashConfig();
    private final CoinConfig coinConfig = RSCash.getInstance().getCoinConfig();

    public Command(RSPlugin plugin) {
        super(plugin, "rscash", CommandType.MAIN);
    }

    @Override
    public boolean command(CommandData data) {
        String modify = getMessage().get("modify");
        String check = getMessage().get("check");
        String playerName = data.args(1);
        String cashName = data.args(2);
        if (data.equals(0, modify)) {
            if (hasPermission("rscash.modify")) {
                Player other = Bukkit.getPlayer(playerName);
                if (other != null) {
                    if (cashConfig.getMap().containsKey(cashName)) {
                        Cash cashData = cashConfig.getMap().get(cashName);
                        Integer playerData = cashManager.getPlayerCash(other.getUniqueId(), cashName);
                        if (playerData != null) {
                            int value = playerData;
                            String arg3 = data.args(3);
                            if (arg3.matches("^([+\\-]?\\d+|[0-9]+)$")) {
                                if (arg3.startsWith("+")) value = playerData + Integer.parseInt(arg3.substring(1));
                                else if (arg3.startsWith("-")) value = playerData - Integer.parseInt(arg3.substring(1));
                                else value = Integer.parseInt(arg3);
                                if (value < 0) {
                                    sendAnnounce(replaceModify(other, cashData, playerData, 0, getMessage().get("modify.overMin")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, 0));
                                } else if (value > cashData.getMaxCash()) {
                                    sendAnnounce(replaceModify(other, cashData, playerData, cashData.getMaxCash(), getMessage().get("modify.overMax")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, cashData.getMaxCash()));
                                } else {
                                    sendAnnounce(replaceModify(other, cashData, playerData, value, getMessage().get("modify.success")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, value));
                                }
                            } else
                                sendMessage(replacePlayer(other, getMessage().get("modify.wrongFormat")));
                            ;
                        } else {
                            sendAnnounce(getMessage().get("notFound.playerData"));
                        }
                    } else {
                        sendAnnounce(getMessage().get("notFound.cashData"));
                    }
                } else {
                    sendAnnounce(getMessage().getCommon("notFound.playerOnline"));
                }
                return true;
            } else sendAnnounce(getMessage().getCommon("noPermission"));
        } else if (data.equals(0, check)) {
            if (hasPermission("rscash.check")) {
                Player other = Bukkit.getPlayer(playerName);
                if (other != null) {
                    String cash = data.args(2);
                    if (cashConfig.getMap().containsKey(cash)) {
                        Integer value = cashManager.getPlayerCash(other.getUniqueId(), cash);
                        if (value != null) {
                            sendMessage(replaceCheck(other, cashConfig.getMap().get(cash), value, getMessage().get("check.success")));
                        } else {
                            sendAnnounce(getMessage().get("notFound.playerData"));
                        }
                    } else {
                        sendAnnounce(getMessage().get("notFound.cashData"));
                    }
                } else {
                    sendAnnounce(getMessage().getCommon("notFound.playerOnline"));
                }
                return true;
            } else sendAnnounce(getMessage().getCommon("noPermission"));
        }
        return false;
    }

    @Override
    public void wrongUsage(CommandData data) {
        sendMessage(getMessage().get("wrongUsage.modify"));
        sendMessage(getMessage().get("wrongUsage.check"));
    }

    private String replacePlayer(Player player, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName());
    }

    private String replaceModify(Player player, Cash cash, int previous, int current, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.getName())
                .replace("{cashDisplayName}", cash.getDisplayName())
                .replace("{previous}", String.valueOf(previous))
                .replace("{current}", String.valueOf(current));
    }

    private String replaceCheck(Player player, Cash cash, int value, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.getName())
                .replace("{cashDisplayName}", cash.getDisplayName())
                .replace("{value}", String.valueOf(value));
    }

    @Override
    public List<String> tabComplete(CommandData data) {
        String modify = getMessage().get("modify");
        String check = getMessage().get("check");
        if (data.length(1)) {
            List<String> list = new ArrayList<>();
            if (hasPermission("rscash.modify")) list.add(modify);
            if (hasPermission("rscash.check")) list.add(check);
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
                return new ArrayList<>(cashConfig.getMap().keySet());
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
