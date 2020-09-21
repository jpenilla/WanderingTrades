package xyz.jpenilla.wanderingtrades.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LangConfig {
    private final WanderingTrades plugin;
    private final Map<Lang, String> messages = new EnumMap<>(Lang.class);

    public LangConfig(WanderingTrades plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File f = new File(plugin.getDataFolder() + "/lang/" + plugin.getCfg().getLanguage() + ".yml");
        if (plugin.getCfg().isUpdateLang() || !f.exists()) {
            try {
                plugin.saveResource("lang/" + plugin.getCfg().getLanguage() + ".yml", true);
            } catch (IllegalArgumentException ignored) {
                plugin.getLog().warn("Invalid/missing language file name");
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        messages.clear();
        config.getKeys(false).forEach(message -> messages.put(Lang.valueOf(message), config.getString(message)));
    }

    public String get(Lang key) {
        try {
            return messages.get(key);
        } catch (NullPointerException e) {
            plugin.getLog().warn("The message '" + key + "' is missing from your lang file. Use 'updateLang: true' in config.yml to fix this");
            return "";
        }
    }

    public List<String> getList(Lang key) {
        return Arrays.asList(get(key).split("\\|"));
    }
}
