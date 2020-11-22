package xyz.jpenilla.wanderingtrades.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class Constants {
    public static final NamespacedKey CONFIG_NAME = new NamespacedKey(WanderingTrades.getInstance(), "wtConfig");
    public static final NamespacedKey PROTECT = new NamespacedKey(WanderingTrades.getInstance(), "wtProtect");
    public static final NamespacedKey REFRESH_NATURAL = new NamespacedKey(WanderingTrades.getInstance(), "wtRefreshNatural");
    public static final NamespacedKey LAST_REFRESH = new NamespacedKey(WanderingTrades.getInstance(), "wt_last_refresh_time");

    public static final Component PREFIX_COMPONENT =
            Component.text()
                    .append(Component.text("[", NamedTextColor.WHITE))
                    .append(Component.text("W", TextColor.color(0x6B0BDE)))
                    .append(Component.text("T", TextColor.color(0xBA0DFA)))
                    .append(Component.text("]", NamedTextColor.WHITE))
                    .append(Component.space())
                    .clickEvent(ClickEvent.runCommand("/wanderingtrades help"))
                    .build();
}
