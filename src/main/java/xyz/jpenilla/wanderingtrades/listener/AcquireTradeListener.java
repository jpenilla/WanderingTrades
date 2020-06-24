package xyz.jpenilla.wanderingtrades.listener;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.*;
import java.util.stream.Collectors;

public class AcquireTradeListener implements Listener {
    private final WanderingTrades plugin;

    public AcquireTradeListener(WanderingTrades p) {
        plugin = p;
    }

    public static boolean randBoolean(double p) {
        return Math.random() < p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (e.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            if (e.getEntity().getRecipes().size() == 0) {
                AbstractVillager trader = e.getEntity();
                ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    if (plugin.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                        newTrades.addAll(getPlayerHeadsFromServer());
                    }

                    if (plugin.getCfg().isAllowMultipleSets()) {
                        ArrayList<TradeConfig> m = new ArrayList<>(plugin.getCfg().getTradeConfigs().values());
                        for (TradeConfig config : m) {
                            if (randBoolean(config.getChance())) {
                                newTrades.addAll(config.getTrades(false));
                            }
                        }
                    } else {
                        ArrayList<String> keys = new ArrayList<>(plugin.getCfg().getTradeConfigs().keySet());

                        List<Pair<String, Double>> weights = keys.stream().map(config ->
                                new Pair<>(config, plugin.getCfg().getTradeConfigs().get(config).getChance()))
                                .collect(Collectors.toList());

                        String chosenConfig = new EnumeratedDistribution<>(weights).sample();

                        if (chosenConfig != null) {
                            newTrades.addAll(plugin.getCfg().getTradeConfigs().get(chosenConfig).getTrades(false));
                        }
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        trader.setRecipes(newTrades);
                    });
                });
            }
            if (plugin.getCfg().isRemoveOriginalTrades()) {
                e.setCancelled(true);
            }
        }
    }

    private ArrayList<MerchantRecipe> getPlayerHeadsFromServer() {
        ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

        ArrayList<UUID> offlinePlayers = new ArrayList<>(plugin.getStoredPlayers().getPlayers().keySet());
        Collections.shuffle(offlinePlayers);

        ArrayList<UUID> selectedPlayers = new ArrayList<>();
        for (int i = 0; i < plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount(); i++) {
            try {
                selectedPlayers.add(offlinePlayers.get(i));
            } catch (IndexOutOfBoundsException e) {
                plugin.getLog().debug("'playerHeadsFromServerAmount' in playerheads.yml is higher than the amount of recently active players. Not adding a head. Disable debug to hide this message.");
            }
        }

        for (UUID player : selectedPlayers) {
            ItemStack head = new ItemBuilder(player)
                    .setName(plugin.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", plugin.getStoredPlayers().getPlayers().get(player)))
                    .setLore(plugin.getCfg().getPlayerHeadConfig().getLore())
                    .setAmount(plugin.getCfg().getPlayerHeadConfig().getHeadsPerTrade()).build();
            MerchantRecipe recipe = new MerchantRecipe(head, 0, plugin.getCfg().getPlayerHeadConfig().getMaxUses(), plugin.getCfg().getPlayerHeadConfig().isExperienceReward());
            recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient1());
            if (plugin.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
                recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient2());
            }
            newTrades.add(recipe);
        }
        return newTrades;
    }
}
