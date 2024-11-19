package kr.rtuserver.cash;

import kr.rtuserver.cash.cash.PlayerCash;
import kr.rtuserver.framework.bukkit.api.utility.format.ComponentFormatter;

import java.util.UUID;

public class CashAPI {

    private static final RSCash instance = RSCash.getInstance();

    public static boolean addCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        int max = instance.getCashConfig().getMap().get(cash).maxCash();
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(playerCash + value, max)));
        return true;
    }

    public static boolean removeCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.max(playerCash - value, 0)));
        return true;
    }

    public static boolean setCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        int max = instance.getCashConfig().getMap().get(cash).maxCash();
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(Math.min(0, value), max)));
        return true;
    }

    public static Integer getMaxCash(UUID uuid, String cash) {
        if (!isExistCash(uuid, cash)) return null;
        return instance.getCashConfig().getMap().get(cash).maxCash();
    }

    public static Integer getCash(UUID uuid, String cash) {
        if (!isExistCash(uuid, cash)) return null;
        return instance.getCashManager().getPlayerCash(uuid, cash);
    }


    public static boolean isExistCash(UUID uuid, String cash) {
        if (instance.getCashConfig().getMap().containsKey(cash)) return true;
        instance.console(ComponentFormatter.mini("<red>존재하지 않은 재화 데이터에 접근을 시도하였습니다! (" + cash + ")</red>"));
        if (uuid != null)
            instance.getAdventure().player(uuid).sendMessage(ComponentFormatter.mini("<red>존재하지 않은 재화 데이터에 접근을 시도하였습니다! (" + cash + ")</red>"));
        return false;
    }

}
