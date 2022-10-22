package xyz.jpenilla.wanderingtrades.util;

import java.lang.reflect.Method;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.paper.utils.Components;
import org.incendo.interfaces.paper.utils.PaperUtils;
import org.jetbrains.annotations.Nullable;
import xyz.jpenilla.pluginbase.legacy.ComponentUtil;
import xyz.jpenilla.pluginbase.legacy.PaperComponentUtil;

@DefaultQualifier(NonNull.class)
public final class InventoryFactory {
    private static @MonotonicNonNull Method PAPER_CREATE_MERCHANT;

    private InventoryFactory() {
    }

    public static Merchant createMerchant(
        final Player viewer,
        final Component title
    ) {
        if (PaperUtils.isPaper()) {
            return paperCreateMerchant(title);
        }
        return legacyCreateMerchant(viewer, title);
    }

    @SuppressWarnings("deprecation")
    private static Merchant legacyCreateMerchant(
        final Player viewer,
        final Component title
    ) {
        final @Nullable Locale locale = Translator.parseLocale(viewer.getLocale());
        return Bukkit.getServer().createMerchant(ComponentUtil.legacySerializer().serialize(
            GlobalTranslator.render(title, locale == null ? Locale.US : locale)
        ));
    }

    private static Merchant paperCreateMerchant(final Component title) {
        try {
            return (Merchant) paperCreateMerchantMethod().invoke(
                Bukkit.getServer(),
                PaperComponentUtil.toNative(title)
            );
        } catch (final ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Method paperCreateMerchantMethod() {
        if (PAPER_CREATE_MERCHANT == null) {
            try {
                PAPER_CREATE_MERCHANT = Server.class.getMethod(
                    "createMerchant",
                    Components.nativeAdventureComponentClass()
                );
            } catch (final ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
        return PAPER_CREATE_MERCHANT;
    }
}
