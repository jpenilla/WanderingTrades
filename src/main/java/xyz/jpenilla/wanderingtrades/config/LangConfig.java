package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public final class LangConfig {
    private final WanderingTrades plugin;
    private final Map<Lang, String> messages = new EnumMap<>(Lang.class);

    public LangConfig(WanderingTrades plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        File f = new File(this.plugin.getDataFolder() + "/lang/" + this.plugin.config().language() + ".yml");
        if (this.plugin.config().updateLang() || !f.exists()) {
            try {
                this.plugin.saveResource("lang/" + this.plugin.config().language() + ".yml", true);
            } catch (IllegalArgumentException ignored) {
                this.plugin.getLogger().warning("Invalid/missing language file name");
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        this.messages.clear();
        config.getKeys(false).forEach(message -> this.messages.put(Lang.valueOf(message), config.getString(message)));
    }

    public String get(Lang key) {
        try {
            return Objects.requireNonNull(this.messages.get(key));
        } catch (NullPointerException e) {
            this.plugin.getLogger().warning("The message '" + key + "' is missing from your lang file. Use 'updateLang: true' in config.yml to fix this");
            return "";
        }
    }

    public List<String> getList(Lang key) {
        return Arrays.asList(this.get(key).split("\\|"));
    }
}
