package xyz.jpenilla.wanderingtrades.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LangConfig {
    private final WanderingTrades plugin;
    private final Map<Lang, String> messages = new EnumMap<>(Lang.class);

    public LangConfig(WanderingTrades plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File f = new File(plugin.getDataFolder() + "/lang/" + plugin.config().language() + ".yml");
        if (plugin.config().updateLang() || !f.exists()) {
            try {
                plugin.saveResource("lang/" + plugin.config().language() + ".yml", true);
            } catch (IllegalArgumentException ignored) {
                this.plugin.getLogger().warning("Invalid/missing language file name");
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        messages.clear();
        config.getKeys(false).forEach(message -> messages.put(Lang.valueOf(message), config.getString(message)));
    }

    public String get(Lang key) {
        try {
            return Objects.requireNonNull(messages.get(key));
        } catch (NullPointerException e) {
            this.plugin.getLogger().warning("The message '" + key + "' is missing from your lang file. Use 'updateLang: true' in config.yml to fix this");
            return "";
        }
    }

    public List<String> getList(Lang key) {
        return Arrays.asList(get(key).split("\\|"));
    }
}
