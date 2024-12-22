package kr.rtuserver.cash;

import kr.rtuserver.cash.cash.PlayerCash;

import java.util.UUID;

public class CashAPI {

    private static final RSCash instance = RSCash.getInstance();

    public static boolean add(UUID uuid, String cash, long value) {
        if (!exist(cash)) return false;
        if (value == 0) return true;
        if (value < 0) return subtract(uuid, cash, -value);
        long max = instance.getCashConfig().getMap().get(cash).limitMax();
        long min = instance.getCashConfig().getMap().get(cash).limitMin();
        Long playerCash = get(uuid, cash);
        if (playerCash == null) return false;
        long result = Math.max(Math.min(addSafely(playerCash, value), max), min);
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, result));
        return true;
    }

    public static boolean subtract(UUID uuid, String cash, long value) {
        if (!exist(cash)) return false;
        if (value == 0) return true;
        if (value < 0) return add(uuid, cash, -value);
        long max = instance.getCashConfig().getMap().get(cash).limitMax();
        long min = instance.getCashConfig().getMap().get(cash).limitMin();
        Long playerCash = get(uuid, cash);
        if (playerCash == null) return false;
        long result = Math.min(Math.max(subtractSafely(playerCash, value), min), max);
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, result));
        return true;
    }

    public static boolean set(UUID uuid, String cash, long value) {
        if (!exist(cash)) return false;
        long max = instance.getCashConfig().getMap().get(cash).limitMax();
        long min = instance.getCashConfig().getMap().get(cash).limitMin();
        instance.getCashManager().setPlayerCash(uuid, new PlayerCash(cash, Math.max(Math.min(value, max), min)));
        return true;
    }

    public static Long max(String cash) {
        if (!exist(cash)) return null;
        return instance.getCashConfig().getMap().get(cash).limitMax();
    }

    public static Long min(String cash) {
        if (!exist(cash)) return null;
        return instance.getCashConfig().getMap().get(cash).limitMin();
    }

    public static Long get(UUID uuid, String cash) {
        if (!exist(cash)) return null;
        return instance.getCashManager().getPlayerCash(uuid, cash);
    }


    public static boolean exist(String cash) {
        if (cash == null || cash.isEmpty()) return false;
        return instance.getCashConfig().getMap().containsKey(cash);
    }

    private static long addSafely(long a, long b) {
        if (b > 0 && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        } else if (b < 0 && a < Long.MIN_VALUE - b) {
            return Long.MIN_VALUE;
        }
        return a + b;
    }

    private static long subtractSafely(long a, long b) {
        if (b > 0 && a < Long.MIN_VALUE + b) {
            return Long.MIN_VALUE;
        } else if (b < 0 && a > Long.MAX_VALUE + b) {
            return Long.MAX_VALUE;
        }
        return a - b;
    }

}
