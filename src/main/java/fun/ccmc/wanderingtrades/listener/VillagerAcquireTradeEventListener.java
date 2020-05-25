package fun.ccmc.wanderingtrades.listener;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VillagerAcquireTradeEventListener implements Listener {
    WanderingTrades plugin;
    public VillagerAcquireTradeEventListener(WanderingTrades p) {
        plugin = p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        plugin.getLog().debug("VillagerAcquireTradeEvent");

        if(e.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            if(e.getEntity().getRecipes().size() == 0) {
                AbstractVillager trader = e.getEntity();

                plugin.getLog().debug("First VillagerAcquireTradeEvent");
                plugin.getLog().debug(trader.getLocation().toString());

                ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

                if(plugin.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                    ArrayList<OfflinePlayer> offlinePlayers = new ArrayList<>(Arrays.asList(plugin.getServer().getOfflinePlayers()));
                    Collections.shuffle(offlinePlayers);
                    ArrayList<OfflinePlayer> selectedPlayers = new ArrayList<>();
                    for(int i = 0; i < plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount(); i++) {
                        selectedPlayers.add(offlinePlayers.get(i));
                    }
                    selectedPlayers.forEach(player -> {
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        SkullCreator.withName(head, player.getName());
                        ItemMeta meta = head.getItemMeta();
                        meta.setDisplayName(plugin.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", player.getName()));
                        meta.setLore(plugin.getCfg().getPlayerHeadConfig().getLore());
                        head.setItemMeta(meta);
                        head.setAmount(plugin.getCfg().getPlayerHeadConfig().getAmountOfHeadsPerTrade());
                        MerchantRecipe recipe = new MerchantRecipe(head, 0, plugin.getCfg().getPlayerHeadConfig().getMaxUses(), plugin.getCfg().getPlayerHeadConfig().isExperienceReward());
                        recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient1());
                        if(plugin.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
                            recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient2());
                        }
                        newTrades.add(recipe);
                    });
                }

                if(plugin.getCfg().isAllowMultipleSets()) {
                    HashMap<String, TradeConfig> m = new HashMap<>(plugin.getCfg().getTradeConfigs());
                    Iterator it = m.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        if(randBoolean(((TradeConfig) pair.getValue()).getChance())) {
                            newTrades.addAll(((TradeConfig) pair.getValue()).getTrades(false));
                        }
                        it.remove();
                    }
                } else {
                    ArrayList<String> keys = new ArrayList<>(plugin.getCfg().getTradeConfigs().keySet());
                    Collections.shuffle(keys);
                    boolean hasTrades = false;
                    for(String config : keys) {
                        if(randBoolean(plugin.getCfg().getTradeConfigs().get(config).getChance()) && !hasTrades) {
                            newTrades.addAll(plugin.getCfg().getTradeConfigs().get(config).getTrades(false));
                            hasTrades = true;
                        }
                    }
                }

                trader.setRecipes(newTrades);

                if(plugin.getCfg().isDebug()) {
                    int x = 0;
                    while(x < trader.getRecipes().size()) {
                        plugin.getLog().debug(x + " " + trader.getRecipe(x).getIngredients().toString() + " " + trader.getRecipe(x).getResult().toString());
                        x++;
                    }
                }
            }
            if(plugin.getCfg().isRemoveOriginalTrades()) {
                e.setCancelled(true);
            }
        }
    }

    public static boolean randBoolean(double p) {
        return Math.random() < p;
    }
}
