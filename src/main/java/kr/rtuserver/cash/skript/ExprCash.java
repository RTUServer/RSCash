package kr.rtuserver.cash.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import kr.rtuserver.cash.CashAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprCash extends SimpleExpression<Long> {

    private Expression<String> cashType;
    private Expression<Player> player;

    public static void register() {
        Skript.registerExpression(ExprCash.class, Long.class, ExpressionType.PROPERTY,
                "%player%'s cash [named] %string%");
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        cashType = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "cash named " + cashType.toString(e, debug);
    }

    @Override
    protected Long @NotNull [] get(@NotNull Event e) {
        Player p = player.getSingle(e);
        String type = cashType.getSingle(e);
        if (p == null || type == null) return new Long[]{0L};
        return new Long[]{CashAPI.get(p.getUniqueId(), type)};
    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        List<Changer.ChangeMode> allowed = List.of(Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE);
        return allowed.contains(mode) ? new Class[]{Long.class} : null;
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, Changer.ChangeMode mode) {
        Player p = player.getSingle(e);
        String type = cashType.getSingle(e);
        if (p == null || type == null) return;
        if (delta[0] instanceof Long amount) {
            switch (mode) {
                case SET -> CashAPI.set(p.getUniqueId(), type, amount);
                case ADD -> CashAPI.add(p.getUniqueId(), type, amount);
                case REMOVE -> CashAPI.subtract(p.getUniqueId(), type, amount);
            }
        }
    }
}