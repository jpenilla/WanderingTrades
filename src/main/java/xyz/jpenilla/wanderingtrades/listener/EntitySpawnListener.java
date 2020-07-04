package xyz.jpenilla.wanderingtrades.listener;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntitySpawnListener implements Listener {
    private final WanderingTrades wanderingTrades;

    public EntitySpawnListener(WanderingTrades wt) {
        this.wanderingTrades = wt;
    }

    public static boolean randBoolean(double p) {
        return Math.random() < p;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (EntityType.WANDERING_TRADER.equals(e.getEntityType())) {
            if (!e.getEntity().getPersistentDataContainer().has(new NamespacedKey(wanderingTrades, "wtConfig"), PersistentDataType.STRING)) {
                ArrayList<MerchantRecipe> newTrades = new ArrayList<>();
                Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {
                    if (wanderingTrades.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                        newTrades.addAll(getPlayerHeadsFromServer());
                    }
                    if (wanderingTrades.getCfg().isAllowMultipleSets()) {
                        ArrayList<TradeConfig> m = new ArrayList<>(wanderingTrades.getCfg().getTradeConfigs().values());
                        for (TradeConfig config : m) {
                            if (randBoolean(config.getChance())) {
                                newTrades.addAll(config.getTrades(false));
                            }
                        }
                    } else {
                        ArrayList<String> keys = new ArrayList<>(wanderingTrades.getCfg().getTradeConfigs().keySet());

                        List<Pair<String, Double>> weights = keys.stream().map(config ->
                                new Pair<>(config, wanderingTrades.getCfg().getTradeConfigs().get(config).getChance()))
                                .collect(Collectors.toList());

                        String chosenConfig = new EnumeratedDistribution<>(weights).sample();

                        if (chosenConfig != null) {
                            newTrades.addAll(wanderingTrades.getCfg().getTradeConfigs().get(chosenConfig).getTrades(false));
                        }
                    }
                    Bukkit.getScheduler().runTask(wanderingTrades, () -> {
                        AbstractVillager trader = (AbstractVillager) e.getEntity();
                        newTrades.addAll(trader.getRecipes());
                        trader.setRecipes(newTrades);
                    });
                });
            }
        }
    }

    private ArrayList<MerchantRecipe> getPlayerHeadsFromServer() {
        ArrayList<UUID> offlinePlayers = new ArrayList<>(wanderingTrades.getStoredPlayers().getPlayers().keySet());
        Collections.shuffle(offlinePlayers);
        ArrayList<UUID> selectedPlayers = new ArrayList<>();
        for (int i = 0; i < wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount(); i++) {
            try {
                selectedPlayers.add(offlinePlayers.get(i));
            } catch (IndexOutOfBoundsException e) {
                wanderingTrades.getLog().debug("'playerHeadsFromServerAmount' in playerheads.yml is higher than the amount of recently active players. Not adding a head. Disable debug to hide this message.");
            }
        }

        ArrayList<MerchantRecipe> newTrades = new ArrayList<>();
        for (UUID player : selectedPlayers) {
            ItemStack head = new ItemBuilder(player)
                    .setName(wanderingTrades.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", wanderingTrades.getStoredPlayers().getPlayers().get(player)))
                    .setLore(wanderingTrades.getCfg().getPlayerHeadConfig().getLore())
                    .setAmount(wanderingTrades.getCfg().getPlayerHeadConfig().getHeadsPerTrade()).build();
            MerchantRecipe recipe = new MerchantRecipe(head, 0, wanderingTrades.getCfg().getPlayerHeadConfig().getMaxUses(), wanderingTrades.getCfg().getPlayerHeadConfig().isExperienceReward());
            recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient1());
            if (wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
                recipe.addIngredient(wanderingTrades.getCfg().getPlayerHeadConfig().getIngredient2());
            }
            newTrades.add(recipe);
        }
        return newTrades;
    }
}
