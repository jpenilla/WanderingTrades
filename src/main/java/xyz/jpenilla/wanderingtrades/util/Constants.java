package xyz.jpenilla.wanderingtrades.util;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@NullMarked
public final class Constants {
    private Constants() {
    }

    public static final NamespacedKey CONFIG_NAME = new NamespacedKey(WanderingTrades.instance(), "wtConfig");
    public static final NamespacedKey PROTECT = new NamespacedKey(WanderingTrades.instance(), "wtProtect");
    public static final NamespacedKey PREVENT_INVISIBILITY = new NamespacedKey(WanderingTrades.instance(), "wtPreventInvisibility");
    public static final NamespacedKey REFRESH_NATURAL = new NamespacedKey(WanderingTrades.instance(), "wtRefreshNatural");
    public static final NamespacedKey LAST_REFRESH = new NamespacedKey(WanderingTrades.instance(), "wt_last_refresh_time");

    /**
     * Key temporarily set on Wandering Traders PDC when spawned by WanderingTrades command or going through a portal, to avoid
     * our CreatureSpawnEvent listener from handling already handled traders.
     */
    public static final NamespacedKey TEMPORARY_BLACKLISTED = new NamespacedKey(WanderingTrades.instance(), "wtTemporaryBlacklist");

    public static final Component PREFIX_COMPONENT = text()
        .append(text("[", WHITE))
        .append(text("W", color(0x6B0BDE)))
        .append(text("T", color(0xBA0DFA)))
        .append(text("]", WHITE))
        .append(space())
        .clickEvent(runCommand("/wanderingtrades help"))
        .build();

    public static final String PREFIX_MINIMESSAGE = miniMessage().serialize(PREFIX_COMPONENT);

    public static final class Permissions {
        private Permissions() {
        }

        public static final String WANDERINGTRADES_HEADAVAILABLE = "wanderingtrades.headavailable";
        public static final String TRADER_SPAWN_NOTIFICATIONS = "wanderingtrades.trader-spawn-notifications";
    }
}
