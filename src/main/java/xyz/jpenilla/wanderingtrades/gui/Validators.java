package xyz.jpenilla.wanderingtrades.gui;

import java.util.function.BiFunction;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.util.Components;

@DefaultQualifier(NonNull.class)
public final class Validators {
    private final BaseInterface baseInterface;
    private final WanderingTrades plugin;

    Validators(
        final BaseInterface baseInterface,
        final WanderingTrades plugin
    ) {
        this.baseInterface = baseInterface;
        this.plugin = plugin;
    }

    public boolean validateIntRange(final Player player, final String s) {
        if (s.contains(":")) {
            try {
                String[] split = s.split(":");
                return this.validateIntGTE0(player, split[0]) && this.validateIntGTE0(player, split[1]);
            } catch (Exception e) {
                return false;
            }
        } else {
            return this.validateIntGTE0(player, s);
        }
    }

    private boolean validateInt(
        final String input,
        final IntPredicate validator
    ) {
        try {
            int i = Integer.parseInt(input);
            return validator.test(i);
        } catch (final NumberFormatException ex) {
            return false;
        }
    }

    private boolean validateDouble(
        final String input,
        final DoublePredicate validator
    ) {
        try {
            double d = Double.parseDouble(input);
            return validator.test(d);
        } catch (final NumberFormatException ex) {
            return false;
        }
    }

    public boolean validateIntGT0(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= 1) {
                return true;
            }
            this.plugin.chat().send(player, Messages.MESSAGE_NUMBER_GT_0);
            return false;
        });
    }

    public boolean validateIntGTE0(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= 0) {
                return true;
            }
            this.plugin.chat().send(player, Messages.MESSAGE_NUMBER_GTE_0);
            return false;
        });
    }

    public boolean validateIntGTEN1(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= -1) {
                return true;
            }
            this.plugin.chat().send(player, Messages.MESSAGE_NUMBER_GTE_N1);
            return false;
        });
    }

    public boolean validateDouble0T1(final Player player, final String input) {
        return this.validateDouble(input, d -> {
            if (d < 0 || d > 1) {
                this.plugin.chat().send(player, Messages.MESSAGE_NUMBER_0T1);
                return false;
            }
            return true;
        });
    }

    public String confirmYesNo(final Player player, final String s) {
        return this.confirmYesNo(player, Components.valuePlaceholder(s));
    }

    public BiFunction<Player, String, String> confirmYesNo(final Function<String, Component> formatValue) {
        return (player, s) -> this.confirmYesNo(player, Components.valuePlaceholder(formatValue.apply(s)));
    }

    private String confirmYesNo(final Player player, final TagResolver placeholder) {
        this.plugin.chat().send(player, Messages.MESSAGE_YOU_ENTERED.withPlaceholders(placeholder));
        this.plugin.chat().send(player, Messages.MESSAGE_YES_NO);
        return "";
    }

    public void editCancelled(final Player player, final String s) {
        this.plugin.chat().send(player, Messages.MESSAGE_EDIT_CANCELLED);
        this.baseInterface.open(player);
    }
}
