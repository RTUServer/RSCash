package com.github.ipecter.rtuserver.cash;

import com.github.ipecter.rtuserver.cash.cash.PlayerCash;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;

import java.util.UUID;

public class CashAPI {

    public static boolean addCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        RSCash instance = RSCash.getInstance();
        int max = instance.getConfigManager().getCashMap().get(cash).getMaxCash();
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(playerCash + value, max)));
        return true;
    }

    public static boolean removeCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        RSCash instance = RSCash.getInstance();
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.max(playerCash - value, 0)));
        return true;
    }

    public static boolean setCash(UUID uuid, String cash, int value) {
        if (!isExistCash(uuid, cash)) return false;
        RSCash instance = RSCash.getInstance();
        int max = instance.getConfigManager().getCashMap().get(cash).getMaxCash();
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(Math.min(0, value), max)));
        return true;
    }
    public static Integer getMaxCash(UUID uuid, String cash) {
        if (!isExistCash(uuid, cash)) return null;
        return RSCash.getInstance().getConfigManager().getCashMap().get(cash).getMaxCash();
    }

    public static Integer getCash(UUID uuid, String cash) {
        if (!isExistCash(uuid, cash)) return null;
        return RSCash.getInstance().getCashManager().getPlayerCash(uuid, cash);
    }


    public static boolean isExistCash(UUID uuid, String cash) {
        if (!RSCash.getInstance().getConfigManager().getCashMap().containsKey(cash)) {
            RSCash.getInstance().console(ComponentUtil.miniMessage("<red>존재하지 않은 재화 데이터에 접근을 시도하였습니다! (" + cash + ")</red>"));
            if (uuid != null) RSCash.getInstance().getAdventure().player(uuid).sendMessage(ComponentUtil.miniMessage("<red>존재하지 않은 재화 데이터에 접근을 시도하였습니다! (" + cash + ")</red>"));
        }
        return true;
    }

}
