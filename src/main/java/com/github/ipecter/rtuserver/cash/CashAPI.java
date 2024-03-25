package com.github.ipecter.rtuserver.cash;

import com.github.ipecter.rtuserver.cash.cash.PlayerCash;

import java.util.UUID;

public class CashAPI {

    public static boolean addCash(UUID uuid, String cash, int value) {
        if (!isExistCash(cash)) return false;
        RSCash instance = RSCash.getInstance();
        int max = instance.getConfigManager().getCashMap().get(cash).getMaxCash();
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(playerCash + value, max)));
        return true;
    }

    public static boolean removeCash(UUID uuid, String cash, int value) {
        if (!isExistCash(cash)) return false;
        RSCash instance = RSCash.getInstance();
        Integer playerCash = getCash(uuid, cash);
        if (playerCash == null) return false;
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.max(playerCash - value, 0)));
        return true;
    }

    public static boolean setCash(UUID uuid, String cash, int value) {
        if (!isExistCash(cash)) return false;
        RSCash instance = RSCash.getInstance();
        int max = instance.getConfigManager().getCashMap().get(cash).getMaxCash();
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.min(value, max)));
        return true;
    }

    public static Integer getCash(UUID uuid, String cash) {
        if (!isExistCash(cash)) return null;
        return RSCash.getInstance().getCashManager().getPlayerCash(uuid, cash);
    }


    public static boolean isExistCash(String cash) {
        return RSCash.getInstance().getConfigManager().getCashMap().containsKey(cash);
    }

}
