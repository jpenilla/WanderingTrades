package xyz.jpenilla.wanderingtrades.listener;

import lombok.Getter;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.inventory.MerchantRecipe;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TraderSpawnListener implements Listener {
    private final WanderingTrades wanderingTrades;
    @Getter private final List<UUID> traderBlacklistCache = new ArrayList<>();

    public TraderSpawnListener(WanderingTrades wt) {
        this.wanderingTrades = wt;
    }

    public static boolean randBoolean(double p) {
        return Math.random() < p;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPortal(EntityPortalEvent e) {
        if (e.getEntityType() == EntityType.WANDERING_TRADER) {
            wanderingTrades.getLog().warn("wt in portal");
            traderBlacklistCache.add(e.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() == EntityType.WANDERING_TRADER) {
            wanderingTrades.getLog().warn(e.getSpawnReason().toString());
            Bukkit.getScheduler().runTaskLater(wanderingTrades, () -> addTrades((WanderingTrader) e.getEntity(), false), 1L);
        }
    }

    public void addTrades(WanderingTrader wanderingTrader, boolean refresh) {
        if (!traderBlacklistCache.contains(wanderingTrader.getUniqueId())) {
            ArrayList<MerchantRecipe> newTrades = new ArrayList<>();
            Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                if (wanderingTrades.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                    newTrades.addAll(wanderingTrades.getStoredPlayers().getPlayerHeadsFromServer());
                }
                if (wanderingTrades.getCfg().isAllowMultipleSets()) {
                    ArrayList<TradeConfig> m = new ArrayList<>(wanderingTrades.getCfg().getTradeConfigs().values());
                    for (TradeConfig config : m) {
                        if (randBoolean(config.getChance())) {
                            newTrades.addAll(config.getTrades(false));
                        }
                    }
                } else {
                    List<Pair<String, Double>> weights = new ArrayList<>();
                    for (Map.Entry<String, TradeConfig> tradeConfig : wanderingTrades.getCfg().getTradeConfigs().entrySet()) {
                        weights.add(new Pair<>(tradeConfig.getKey(), tradeConfig.getValue().getChance()));
                    }

                    String chosenConfig = new EnumeratedDistribution<>(weights).sample();

                    if (chosenConfig != null) {
                        newTrades.addAll(wanderingTrades.getCfg().getTradeConfigs().get(chosenConfig).getTrades(false));
                    }
                }
                Bukkit.getScheduler().runTask(wanderingTrades, () -> {
                    if (!refresh) {
                        newTrades.addAll(wanderingTrader.getRecipes());
                    } /*else { TODO: Find a way to get the vanilla trades
                        if (!wanderingTrades.getCfg().isRemoveOriginalTrades()) {
                            ArrayList<MerchantRecipe> newRecipes = new ArrayList<>();
                            Collection<ItemStack> stacks = wanderingTrader.getLootTable().populateLoot(new Random(), new LootContext.Builder(wanderingTrader.getLocation()).lootedEntity(wanderingTrader).build());
                            for (ItemStack stack : stacks) {
                                wanderingTrades.getLog().info(stack.toString());
                                MerchantRecipe recipe = new MerchantRecipe(stack, 0, 1, true);
                                recipe.addIngredient(new ItemStack(Material.EMERALD, new Random().nextInt(5)));
                                newRecipes.add(recipe);
                            }
                            newTrades.addAll(newRecipes);
                        }
                    }*/
                    wanderingTrader.setRecipes(newTrades);
                });
            });
        } else {
            traderBlacklistCache.remove(wanderingTrader.getUniqueId());
        }
    }
}
