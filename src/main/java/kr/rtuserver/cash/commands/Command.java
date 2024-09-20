package kr.rtuserver.cash.commands;

import com.github.ipecter.rtuserver.lib.bukkit.api.RSPlugin;
import com.github.ipecter.rtuserver.lib.bukkit.api.command.RSCommand;
import com.github.ipecter.rtuserver.lib.bukkit.api.command.RSCommandData;
import com.github.ipecter.rtuserver.lib.bukkit.api.utility.player.PlayerChat;
import kr.rtuserver.cash.RSCash;
import kr.rtuserver.cash.cash.Cash;
import kr.rtuserver.cash.cash.CashManager;
import kr.rtuserver.cash.cash.PlayerCash;
import kr.rtuserver.cash.config.CashConfig;
import kr.rtuserver.cash.config.CoinConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Command extends RSCommand {

    private final CashManager cashManager = RSCash.getInstance().getCashManager();
    private final CashConfig cashConfig = RSCash.getInstance().getCashConfig();
    private final CoinConfig coinConfig = RSCash.getInstance().getCoinConfig();

    public Command(RSPlugin plugin) {
        super(plugin, "rscash", true);
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
                        Integer playerData = cashManager.getPlayerCash(other.getUniqueId(), cashName);
                        if (playerData != null) {
                            int value = playerData;
                            String arg3 = data.args(3);
                            if (arg3.matches("^([+\\-]?\\d+|[0-9]+)$")) {
                                if (arg3.startsWith("+")) value = playerData + Integer.parseInt(arg3.substring(1));
                                else if (arg3.startsWith("-")) value = playerData - Integer.parseInt(arg3.substring(1));
                                else value = Integer.parseInt(arg3);
                                if (value < 0) {
                                    chat.announce(getAudience(), replaceModify(other, cashData, playerData, 0, getMessage().get("modify.overMin")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, 0));
                                } else if (value > cashData.maxCash()) {
                                    chat.announce(getAudience(), replaceModify(other, cashData, playerData, cashData.maxCash(), getMessage().get("modify.overMax")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, cashData.maxCash()));
                                } else {
                                    chat.announce(getAudience(), replaceModify(other, cashData, playerData, value, getMessage().get("modify.success")));
                                    cashManager.setPlayerCash(other.getUniqueId(), new PlayerCash(cashName, value));
                                }
                            } else
                                chat.send(getAudience(), replacePlayer(other, getMessage().get("modify.wrongFormat")));
                        } else chat.announce(getAudience(), getMessage().get("notFound.playerData"));
                    } else chat.announce(getAudience(), getMessage().get("notFound.cashData"));
                } else chat.announce(getAudience(), getMessage().getCommon("notFound.onlinePlayer"));
                return true;
            } else chat.announce(getAudience(), getMessage().getCommon("noPermission"));
        } else if (data.equals(0, check)) {
            if (hasPermission("rscash.check")) {
                Player other = Bukkit.getPlayer(playerName);
                if (other != null) {
                    String cash = data.args(2);
                    if (cashConfig.getMap().containsKey(cash)) {
                        Integer value = cashManager.getPlayerCash(other.getUniqueId(), cash);
                        if (value != null)
                            chat.send(getAudience(), replaceCheck(other, cashConfig.getMap().get(cash), value, getMessage().get("check.success")));
                        else chat.announce(getAudience(), getMessage().get("notFound.playerData"));
                    } else chat.announce(getAudience(), getMessage().get("notFound.cashData"));
                } else chat.announce(getAudience(), getMessage().getCommon("notFound.onlinePLayer"));
                return true;
            } else chat.announce(getAudience(), getMessage().getCommon("noPermission"));
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

    private String replaceModify(Player player, Cash cash, int previous, int current, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.name())
                .replace("{cashDisplayName}", cash.displayName())
                .replace("{previous}", String.valueOf(previous))
                .replace("{current}", String.valueOf(current));
    }

    private String replaceCheck(Player player, Cash cash, int value, String message) {
        return message
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{cashName}", cash.name())
                .replace("{cashDisplayName}", cash.displayName())
                .replace("{value}", String.valueOf(value));
    }

    @Override
    public List<String> tabComplete(RSCommandData data) {
        String modify = getMessage().get("modify");
        String check = getMessage().get("check");
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
