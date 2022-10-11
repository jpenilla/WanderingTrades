package xyz.jpenilla.wanderingtrades.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
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

    public void refreshTrades(final WanderingTrader wanderingTrader) {
        this.addTrades(wanderingTrader, true);
    }

    public void addTrades(final WanderingTrader wanderingTrader) {
        this.addTrades(wanderingTrader, false);
    }

    private void addTrades(final WanderingTrader wanderingTrader, final boolean refresh) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final List<MerchantRecipe> newTrades = this.selectTrades();

            this.plugin.getServer().getScheduler().runTask(
                this.plugin,
                () -> this.addSelectedTrades(wanderingTrader, refresh, newTrades)
            );
        });
    }

    private List<MerchantRecipe> selectTrades() {
        final List<MerchantRecipe> newTrades = new ArrayList<>();

        if (this.plugin.config().playerHeadConfig().playerHeadsFromServer() && randBoolean(this.plugin.config().playerHeadConfig().playerHeadsFromServerChance())) {
            newTrades.addAll(this.plugin.playerHeads().randomlySelectPlayerHeads());
        }

        final Map<String, TradeConfig> tradeConfigs = Map.copyOf(this.plugin.config().tradeConfigs());

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
            this.plugin.getLogger().log(
                Level.WARNING,
                String.format("Failed to reset trades! Please report this bug to the issue tracker at %s !", this.plugin.getDescription().getWebsite()),
                throwable
            );
        }
    }

    private static boolean randBoolean(final double p) {
        return Math.random() < p;
    }
}
