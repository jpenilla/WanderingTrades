package xyz.jpenilla.wanderingtrades;

import com.tcoded.folialib.FoliaLib;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PaperInterfaceListeners;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.ConfigManager;
import xyz.jpenilla.wanderingtrades.integration.VaultHook;
import xyz.jpenilla.wanderingtrades.integration.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.PlayerHeads;
import xyz.jpenilla.wanderingtrades.util.TradeApplicator;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

@NullMarked
public final class WanderingTrades extends JavaPlugin {
    private static @MonotonicNonNull WanderingTrades instance;

    private @MonotonicNonNull ConfigManager configManager;
    private @MonotonicNonNull PlayerHeads playerHeads;
    private @MonotonicNonNull Listeners listeners;
    private @MonotonicNonNull TradeApplicator tradeApplicator;
    private @MonotonicNonNull TradeSessionManager sessionManager;
    private @Nullable WorldGuardHook worldGuard = null;
    private @Nullable VaultHook vault = null;
    private final FoliaLib foliaLib = new FoliaLib(this);

    @Override
    public void onEnable() {
        instance = this;
        PaperInterfaceListeners.install(this);
        this.setupIntegrations();
        this.configManager = new ConfigManager(this);
        this.configManager.load();
        this.playerHeads = PlayerHeads.create(this);
        this.tradeApplicator = new TradeApplicator(this);
        this.sessionManager = new TradeSessionManager(this);
        this.listeners = Listeners.setup(this);
        if (!this.configManager.config().disableCommands()) {
            Commands.setup(this);
        }
        this.updateCheck();
        this.setupMetrics();
    }

    @Override
    public void onDisable() {
        this.closeInterfaces();
    }

    private void setupIntegrations() {
        this.foliaLib.getScheduler().runNextTick(task -> {
            if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
                this.vault = new VaultHook(this.getServer());
            }
        });
        if (this.getServer().getPluginManager().isPluginEnabled("WorldGuard")
            && this.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            this.worldGuard = new WorldGuardHook(this);
        }
    }

    private void updateCheck() {
        if (this.config().updateChecker()) {
            this.foliaLib.getScheduler().runNextTick(task ->
                new UpdateChecker(this, "jpenilla/WanderingTrades").checkVersion()
            );
        }
    }

    private void setupMetrics() {
        final Metrics metrics = new Metrics(this, 7597);
        metrics.addCustomChart(new SimplePie("player_heads", () -> this.configManager.playerHeadConfig().playerHeadsFromServer() ? "On" : "Off"));
        metrics.addCustomChart(new SimplePie("player_heads_per_trader", () -> String.valueOf(this.configManager.playerHeadConfig().playerHeadsFromServerAmount())));
        metrics.addCustomChart(new SimplePie("plugin_language", () -> this.config().language()));
        metrics.addCustomChart(new SimplePie("amount_of_trade_configs", () -> String.valueOf(this.configManager.tradeConfigs().size())));
    }

    public void reload() {
        this.closeInterfaces();

        this.configManager().reload();

        this.listeners().reload();
        this.playerHeads().configChanged();
    }

    private void closeInterfaces() {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            try {
                final Object openInventoryView = Player.class.getMethod("getOpenInventory").invoke(player);
                final Inventory inv = (Inventory) InventoryView.class.getMethod("getTopInventory").invoke(openInventoryView);
                if (inv.getHolder() instanceof InterfaceView<?, ?>) {
                    player.closeInventory();
                }
            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ConfigManager configManager() {
        return this.configManager;
    }

    public TradeSessionManager sessionManager() {
        return this.sessionManager;
    }

    public Config config() {
        return this.configManager.config();
    }

    public PlayerHeads playerHeads() {
        return this.playerHeads;
    }

    public TradeApplicator tradeApplicator() {
        return this.tradeApplicator;
    }

    public Listeners listeners() {
        return this.listeners;
    }

    public @Nullable WorldGuardHook worldGuardHook() {
        return this.worldGuard;
    }

    public @Nullable VaultHook vaultHook() {
        return this.vault;
    }

    public boolean isVaultPermissions() {
        return this.vault != null && this.vault.permissions() != null;
    }

    public void debug(final String message) {
        if (this.config().debug()) {
            this.getLogger().info("[DEBUG] " + message);
        }
    }

    public static WanderingTrades instance() {
        return instance;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }
}
