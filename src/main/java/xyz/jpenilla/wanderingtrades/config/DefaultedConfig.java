package xyz.jpenilla.wanderingtrades.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

/**
 * Config which has a default file included in the plugin jar
 */
public abstract class DefaultedConfig {
    private static final boolean COMMENTS = canUseComments();

    protected final WanderingTrades plugin;
    protected final YamlConfiguration defaultConfig;

    protected DefaultedConfig(
        final WanderingTrades plugin,
        final String defaultConfigPathInJar
    ) {
        this.plugin = plugin;
        this.defaultConfig = this.defaultConfig(defaultConfigPathInJar);
    }

    protected abstract FileConfiguration config();

    private YamlConfiguration defaultConfig(final String file) {
        final @Nullable InputStream defaultConfigStream = this.plugin.getResource(file);
        if (defaultConfigStream != null) {
            try (final InputStreamReader reader = new InputStreamReader(defaultConfigStream)) {
                return YamlConfiguration.loadConfiguration(reader);
            } catch (final IOException ignore) {
                throw new IllegalStateException();
            }
        }
        throw new IllegalStateException();
    }

    protected void set(
        final String key,
        final Object value
    ) {
        final FileConfiguration config = this.config();
        final @Nullable Object prev = config.get(key, null);
        config.set(key, value);
        if (prev != null || value == null) {
            return;
        }
        if (!COMMENTS) {
            return;
        }
        final List<String> comments = this.defaultConfig.getComments(key);
        final List<String> inlineComments = this.defaultConfig.getInlineComments(key);
        if (!comments.isEmpty()) {
            config.setComments(key, comments);
        }
        if (!inlineComments.isEmpty()) {
            config.setInlineComments(key, inlineComments);
        }
    }

    private static boolean canUseComments() {
        try {
            MemorySection.class.getMethod("getComments", String.class);
        } catch (final NoSuchMethodException ignore) {
            return false;
        }
        return true;
    }
}
