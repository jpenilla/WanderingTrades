package fun.ccmc.wanderingtrades.config;

import fun.ccmc.wanderingtrades.WanderingTrades;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class LangConfig {
    private final WanderingTrades plugin;
    private final HashMap<String, String> messages = new HashMap<>();

    public LangConfig(WanderingTrades plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveResource("lang/" + plugin.getCfg().getLanguage() + ".yml", plugin.getCfg().isUpdateLang());
        File f = new File(plugin.getDataFolder() + "/lang/" + plugin.getCfg().getLanguage() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);

        messages.clear();
        config.getKeys(false).forEach(message -> {
            messages.put(message, config.getString(message));
        });
    }

    public String getString(Lang key) {
        try {
            return messages.get(key.toString());
        } catch (NullPointerException e) {
            plugin.getLog().warn("The message '" + key + "' is missing from your lang file. Use 'updateLang: true' in config.yml to fix this");
            return "";
        }
    }
}
