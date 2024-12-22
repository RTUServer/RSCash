package kr.rtuserver.cash.commands;

import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.Cash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.cash.PlayerCash;
import kr.rtuserver.cash.configuration.CashConfig;
import kr.rtuserver.cash.configuration.CoinConfig;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command extends RSCommand<RSCash> {

    private final CashManager cashManager;
    private final CashConfig cashConfig;
    private final CoinConfig coinConfig;

    public Command(RSCash plugin) {
        super(plugin, "rscash", true);
        this.cashManager = plugin.getCashManager();
        this.cashConfig = plugin.getCashConfig();
        this.coinConfig = plugin.getCoinConfig();
    }

    @Override
    public boolean execute(RSCommandData data) {
        PlayerChat chat = PlayerChat.of(getPlugin());
        String modify = getCommand().get("modify");
        String check = getCommand().get("check");
        String playerName = data.args(1);
        String cashName = data.args(2);
        if (data.equals(0, modify)) {
            if (hasPermission("rscash.modify")) {
                Player other = Bukkit.getPlayer(playerName);
                if (other != null) {
                    if (cashConfig.getMap().containsKey(cashName)) {
                        Cash cashData = cashConfig.getMap().get(cashName);
                        Long playerData = cashManager.getPlayerCash(other.getUniqueId(), cashName);
                        if (playerData != null) {
                            long value;
                            String arg3 = data.args(3);
                            if (arg3.matches("^([+\\-]?\\d+|[0-9]+)$")) {

                                if (arg3.startsWith("+")) {
                                    try {
                                        value = Long.parseLong(arg3.substring(1));
                                        if ((playerData > Long.MAX_VALUE - value) || (playerData + value > cashData.limitMax())) {
                                            chat.announce(getAudience(), replaceModify(other, cashData, playerData, cashData.limitMax(), getMessage().get("modify.overMax")));
                                            cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, cashData.limitMax()));
                                        } else {
                                            chat.announce(getAudience(), replaceModify(other, cashData, playerData, playerData + value, getMessage().get("modify.success")));
                                            cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, playerData + value));
                                        }
                                    } catch (NumberFormatException e) {
                                        chat.announce(getAudience(), replacePlayer(other, getMessage().get("modify.wrongFormat")));
                                    }
                                } else if (arg3.startsWith("-")) {
                                    try {
                                        value = Long.parseLong(arg3.substring(1));
                                        if ((playerData < Long.MIN_VALUE + value) || (playerData - value < cashData.limitMin())) {
                                            chat.announce(getAudience(), replaceModify(other, cashData, playerData, cashData.limitMin(), getMessage().get("modify.overMin")));
                                            cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, cashData.limitMin()));
                                        } else {
                                            chat.announce(getAudience(), replaceModify(other, cashData, playerData, playerData - value, getMessage().get("modify.success")));
                                            cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, playerData - value));
                                        }
                                    } catch (NumberFormatException e) {
                                        chat.announce(getAudience(), replacePlayer(other, getMessage().get("modify.wrongFormat")));
                                    }
                                } else {
                                    try {
                                        value = Long.parseLong(arg3);
                                        chat.announce(getAudience(), replaceModify(other, cashData, playerData, value, getMessage().get("modify.success")));
                                        cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, value));
                                    } catch (NumberFormatException e) {
                                        chat.announce(getAudience(), replacePlayer(other, getMessage().get("modify.wrongFormat")));
                                    }
                                }

                            } else chat.announce(getAudience(), replacePlayer(other, getMessage().get("modify.wrongFormat")));
                        } else chat.announce(getAudience(), getMessage().get("notFound.playerData"));
                    } else chat.announce(getAudience(), getMessage().get("notFound.cashData"));
                } else chat.announce(getAudience(), getCommon().getMessage("notFound.onlinePlayer"));
                return true;
            } else chat.announce(getAudience(), getCommon().getMessage("noPermission"));
        } else if (data.equals(0, check)) {
            if (hasPermission("rscash.check")) {
                Player other = Bukkit.getPlayer(playerName);
                if (other != null) {
                    String cash = data.args(2);
                    if (cashConfig.getMap().containsKey(cash)) {
                        Long value = cashManager.getPlayerCash(other.getUniqueId(), cash);
                        if (value != null)
                            chat.announce(getAudience(), replaceCheck(other, cashConfig.getMap().get(cash), value, getMessage().get("check.success")));
                        else chat.announce(getAudience(), getMessage().get("notFound.playerData"));
                    } else chat.announce(getAudience(), getMessage().get("notFound.cashData"));
                } else chat.announce(getAudience(), getCommon().getMessage("notFound.onlinePLayer"));
                return true;
            } else chat.announce(getAudience(), getCommon().getMessage("noPermission"));
        }
        return false;
    }

    @Override
    public void reload(RSCommandData data) {
        cashConfig.reload();
        coinConfig.reload();
    }

    @Override
    public void wrongUsage(RSCommandData data) {
        PlayerChat chat = PlayerChat.of(getPlugin());
        if (hasPermission("rscash.modify")) chat.send(getAudience(), getMessage().get("wrongUsage.modify"));
        if (hasPermission("rscash.check")) chat.send(getAudience(), getMessage().get("wrongUsage.check"));
    }

    private String replacePlayer(Player player, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName());
    }

    private String replaceModify(Player player, Cash cash, long previous, long current, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.name())
                .replace("{cashDisplayName}", cash.displayName())
                .replace("{previous}", String.valueOf(previous))
                .replace("{current}", String.valueOf(current));
    }

    private String replaceCheck(Player player, Cash cash, long value, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.name())
                .replace("{cashDisplayName}", cash.displayName())
                .replace("{value}", String.valueOf(value));
    }

    @Override
    public List<String> tabComplete(RSCommandData data) {
        String modify = getCommand().get("modify");
        String check = getCommand().get("check");
        if (data.length(1)) {
            List<String> list = new ArrayList<>();
            if (hasPermission("rscash.modify")) list.add(modify);
            if (hasPermission("rscash.check")) list.add(check);
            return list;
        }
        if (data.length(2)) {
            List<String> list = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            if (data.equals(0, check) && hasPermission("rscash.check")) return list;
            if (data.equals(0, modify) && hasPermission("rscash.modify")) return list;
        }
        if (data.length(3)) {
            List<String> list = new ArrayList<>(cashConfig.getMap().keySet());
            if (data.equals(0, check) && hasPermission("rscash.check")) return list;
            if (data.equals(0, modify) && hasPermission("rscash.modify")) return list;
        }
        if (data.length(4)) {
            if (data.equals(0, modify) && hasPermission("rscash.modify")) return List.of("+1", "1", "-1");
        }
        return List.of();
    }
}
