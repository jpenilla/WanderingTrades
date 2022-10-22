package xyz.jpenilla.wanderingtrades.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.WeightedRandom;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import static io.papermc.lib.PaperLib.isPaper;

@DefaultQualifier(NonNull.class)
public final class TradeApplicator {
    private final WanderingTrades plugin;

    public TradeApplicator(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void maybeRefreshTrades(final AbstractVillager abstractVillager) {
        final PersistentDataContainer persistentDataContainer = abstractVillager.getPersistentDataContainer();
        final @Nullable String configName = persistentDataContainer.get(Constants.CONFIG_NAME, PersistentDataType.STRING);
        final boolean refreshNatural = persistentDataContainer.has(Constants.REFRESH_NATURAL, PersistentDataType.STRING);
        if (configName == null && !refreshNatural) {
            return;
        }

        final long timeAtPreviousRefresh = persistentDataContainer.getOrDefault(Constants.LAST_REFRESH, PersistentDataType.LONG, 0L);
        final LocalDateTime nextAllowedRefresh = Instant.ofEpochMilli(timeAtPreviousRefresh)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .plusMinutes(this.plugin.config().refreshCommandTradersMinutes());

        if (timeAtPreviousRefresh == 0L || LocalDateTime.now().isAfter(nextAllowedRefresh)) {
            if (configName != null) {
                final TradeConfig tradeConfig = this.plugin.configManager().tradeConfigs().get(configName);
                abstractVillager.setRecipes(tradeConfig.getTrades(true));
            }
            if (refreshNatural && abstractVillager instanceof WanderingTrader wanderingTrader) {
                this.addTrades(wanderingTrader, true);
            }
            persistentDataContainer.set(Constants.LAST_REFRESH, PersistentDataType.LONG, System.currentTimeMillis());
        }
    }

    public void addTrades(final WanderingTrader wanderingTrader) {
        this.addTrades(wanderingTrader, false);
    }

    private void addTrades(final WanderingTrader wanderingTrader, final boolean refresh) {
        this.selectTrades(newTrades -> this.addSelectedTrades(wanderingTrader, refresh, newTrades));
    }

    public void selectTrades(final Consumer<List<MerchantRecipe>> mainThreadCallback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final List<MerchantRecipe> newTrades = this.selectTrades();

            this.plugin.getServer().getScheduler().runTask(
                this.plugin,
                () -> mainThreadCallback.accept(newTrades)
            );
        });
    }

    private List<MerchantRecipe> selectTrades() {
        final List<MerchantRecipe> newTrades = new ArrayList<>();

        if (this.plugin.configManager().playerHeadConfig().playerHeadsFromServer() && randBoolean(this.plugin.configManager().playerHeadConfig().playerHeadsFromServerChance())) {
            newTrades.addAll(this.plugin.playerHeads().randomlySelectPlayerHeads());
        }

        final Map<String, TradeConfig> tradeConfigs = Map.copyOf(this.plugin.configManager().tradeConfigs());

        if (this.plugin.config().allowMultipleSets()) {
            for (final TradeConfig config : tradeConfigs.values()) {
                if (randBoolean(config.chance())) {
                    newTrades.addAll(config.getTrades(false));
                }
            }
        } else {
            final WeightedRandom<String> configNames = new WeightedRandom<>();
            tradeConfigs.forEach((key, value) -> configNames.add(value.chance(), key));
            final @Nullable String chosenConfig = configNames.next();
            if (chosenConfig != null) {
                newTrades.addAll(tradeConfigs.get(chosenConfig).getTrades(false));
            }
        }

        return newTrades;
    }

    private void addSelectedTrades(
        final WanderingTrader wanderingTrader,
        final boolean refresh,
        final List<MerchantRecipe> newTrades
    ) {
        if (!wanderingTrader.isValid()) {
            return;
        }
        if (refresh) {
            if (!this.plugin.config().removeOriginalTrades()) {
                this.resetOffers(wanderingTrader);
                newTrades.addAll(wanderingTrader.getRecipes());
            }
        } else {
            newTrades.addAll(wanderingTrader.getRecipes());
        }
        wanderingTrader.setRecipes(newTrades);
    }

    private void resetOffers(final WanderingTrader wanderingTrader) {
        if (isPaper()) {
            wanderingTrader.resetOffers();
        } else {
            this.resetOffersUsingReflection(wanderingTrader);
        }
    }

    /**
     * Clear this {@link AbstractVillager}'s offers and acquire new ones.
     *
     * <p>Reflection-based implementation of Paper's Villager-resetOffers API</p>
     *
     * @param trader the trader to act on
     */
    private void resetOffersUsingReflection(final @NonNull AbstractVillager trader) {
        final List<MerchantRecipe> oldOffers = trader.getRecipes();
        try {
            VillagerReflection.resetOffers(trader);
        } catch (final Throwable throwable) {
            trader.setRecipes(oldOffers);
            Logging.logger().warn("Failed to reset trades! Please report this bug to the issue tracker at {} !", this.plugin.getDescription().getWebsite(), throwable);
        }
    }

    private static boolean randBoolean(final double p) {
        return Math.random() < p;
    }
}
