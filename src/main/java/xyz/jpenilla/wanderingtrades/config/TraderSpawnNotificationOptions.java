package xyz.jpenilla.wanderingtrades.config;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record TraderSpawnNotificationOptions(
    boolean enabled,
    Players notifyPlayers,
    List<String> commands,
    List<String> perPlayerCommands
) {
    private static final String ENABLED = "enabled";
    private static final String NOTIFY_PLAYERS = "notifyPlayers";
    private static final String COMMANDS = "commands";
    private static final String PER_PLAYER_COMMANDS = "perPlayerCommands";

    void setTo(final DefaultedConfig config, final String path) {
        config.set(path + "." + ENABLED, this.enabled);
        config.set(path + "." + NOTIFY_PLAYERS, this.notifyPlayers.input());
        config.set(path + "." + COMMANDS, this.commands);
        config.set(path + "." + PER_PLAYER_COMMANDS, this.perPlayerCommands);
    }

    static TraderSpawnNotificationOptions createFrom(final @Nullable ConfigurationSection section) {
        Objects.requireNonNull(section, "section");
        return new TraderSpawnNotificationOptions(
            section.getBoolean(ENABLED),
            Players.parse(section.getString(NOTIFY_PLAYERS)),
            section.getStringList(COMMANDS),
            section.getStringList(PER_PLAYER_COMMANDS)
        );
    }

    public interface Players {
        String input();

        Collection<? extends Player> find(WanderingTrader entity);

        private static Players withInput(final String input, final Function<WanderingTrader, Collection<? extends Player>> func) {
            return new Players() {
                @Override
                public String input() {
                    return input;
                }

                @Override
                public Collection<? extends Player> find(final WanderingTrader entity) {
                    return func.apply(entity);
                }
            };
        }

        static Players parse(final @Nullable String value) {
            Objects.requireNonNull(value, "value");
            if (value.equalsIgnoreCase("all")) {
                return withInput(value, trader -> trader.getServer().getOnlinePlayers());
            } else if (value.equalsIgnoreCase("world")) {
                return withInput(value, trader -> trader.getWorld().getPlayers());
            }
            final boolean box = value.endsWith("box");
            try {
                final int radius = Integer.parseInt(box ? value.substring(0, value.length() - 3) : value);
                return withInput(
                    value,
                    trader -> trader.getLocation().getWorld().getNearbyEntities(
                        trader.getLocation(),
                        radius,
                        box ? radius : trader.getLocation().getWorld().getMaxHeight() - trader.getLocation().getWorld().getMinHeight(),
                        radius,
                        k -> k instanceof Player
                    ).stream().map(Player.class::cast).toList()
                );
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid players option, got '" + value + "', expected 'all', 'world', or an integer number for radius.");
            }
        }
    }
}
