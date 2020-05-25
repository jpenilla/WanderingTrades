package fun.ccmc.wanderingtrades;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import fun.ccmc.wanderingtrades.command.CommandWanderingTrades;
import fun.ccmc.wanderingtrades.command.TabCompletions;
import fun.ccmc.wanderingtrades.listener.VillagerAcquireTradeEventListener;
import fun.ccmc.wanderingtrades.util.Config;
import fun.ccmc.wanderingtrades.util.Log;
import fun.ccmc.wanderingtrades.util.UpdateChecker;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class WanderingTrades extends JavaPlugin {
    @Getter private Config cfg;
    @Getter private Log log;
    @Getter private PaperCommandManager commandManager;
    @Getter @Setter private TabCompletions tabCompletions;

    @Override
    public void onEnable() {
        log = new Log(this);
        log.info("&d[STARTING]");

        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new CommandWanderingTrades(this));

        cfg = new Config(this);

        if(cfg.isPluginEnabled()) {
            getServer().getPluginManager().registerEvents(new VillagerAcquireTradeEventListener(this), this);
        }

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);

        new UpdateChecker(this, 79068).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                log.info("&aYou are running the latest version of " + this.getName() + "! :)");
            } else if(this.getDescription().getVersion().contains("SNAPSHOT")) {
                log.info("&e[!] &6You are running a development build of " + this.getName() + " &e[!]");
            } else {
                log.info("&e[!] &6You are running an outdated version of " + this.getName() + " (" + this.getDescription().getVersion() + ") &e[!]");
                log.info("&bVersion " + version + " is available at &b&ohttps://www.spigotmc.org/resources/wanderingtrades.79068/");
            }
        });

        log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
    }
}
