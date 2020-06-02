package fun.ccmc.wanderingtrades.listener;

import fun.ccmc.jmplib.Chat;
import fun.ccmc.jmplib.SkullCreator;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.jmplib.WeightedRandom;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.TradeConfig;
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
import java.util.stream.IntStream;

public class AcquireTradeListener implements Listener {
    private final WanderingTrades plugin;

    public AcquireTradeListener(WanderingTrades p) {
        plugin = p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (e.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            if (e.getEntity().getRecipes().size() == 0) {
                AbstractVillager trader = e.getEntity();

                ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

                if (plugin.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                    newTrades.addAll(getPlayerHeadsFromServer());
                }

                if (plugin.getCfg().isAllowMultipleSets()) {
                    HashMap<String, TradeConfig> m = new HashMap<>(plugin.getCfg().getTradeConfigs());
                    Iterator it = m.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (randBoolean(((TradeConfig) pair.getValue()).getChance())) {
                            newTrades.addAll(((TradeConfig) pair.getValue()).getTrades(false));
                        }
                        it.remove();
                    }
                } else {
                    ArrayList<String> keys = new ArrayList<>(plugin.getCfg().getTradeConfigs().keySet());
                    WeightedRandom<String> weightedRandom = new WeightedRandom<>();
                    keys.forEach(config -> weightedRandom.addEntry(config, plugin.getCfg().getTradeConfigs().get(config).getChance()));
                    String chosenConfig = weightedRandom.getRandom();
                    if (chosenConfig != null) {
                        newTrades.addAll(plugin.getCfg().getTradeConfigs().get(chosenConfig).getTrades(false));
                    }
                }

                trader.setRecipes(newTrades);
            }
            if (plugin.getCfg().isRemoveOriginalTrades()) {
                e.setCancelled(true);
            }
        }
    }

    private ArrayList<MerchantRecipe> getPlayerHeadsFromServer() {
        ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

        ArrayList<OfflinePlayer> offlinePlayers = new ArrayList<>(Arrays.asList(plugin.getServer().getOfflinePlayers()));
        Collections.shuffle(offlinePlayers);

        ArrayList<OfflinePlayer> selectedPlayers = new ArrayList<>();
        IntStream.range(0, plugin.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerAmount()).forEach(i -> {
            try {
                if (!TextUtil.containsCaseInsensitive(offlinePlayers.get(i).getName(), plugin.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
                    selectedPlayers.add(offlinePlayers.get(i));
                } else {
                    while (!inBounds(i, selectedPlayers)) {
                        Random r = new Random();
                        int num = r.ints(0, (offlinePlayers.size() + 1)).findFirst().getAsInt();
                        if (!TextUtil.containsCaseInsensitive(offlinePlayers.get(num).getName(), plugin.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
                            selectedPlayers.add(offlinePlayers.get(num));
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                plugin.getLog().warn("'playerHeadsFromServerAmount' in playerheads.yml is higher than the amount of players that ever joined this server! Player heads from the server will not be added to Wandering Traders until this is corrected.");
            }
        });

        selectedPlayers.forEach(player -> {
            ItemStack head = SkullCreator.itemFromUuid(player.getUniqueId());
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(TextUtil.colorize(plugin.getCfg().getPlayerHeadConfig().getName().replace("{PLAYER}", player.getName())));
            meta.setLore(TextUtil.colorize(plugin.getCfg().getPlayerHeadConfig().getLore()));
            head.setItemMeta(meta);
            head.setAmount(plugin.getCfg().getPlayerHeadConfig().getAmountOfHeadsPerTrade());
            MerchantRecipe recipe = new MerchantRecipe(head, 0, plugin.getCfg().getPlayerHeadConfig().getMaxUses(), plugin.getCfg().getPlayerHeadConfig().isExperienceReward());
            recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient1());
            if (plugin.getCfg().getPlayerHeadConfig().getIngredient2() != null) {
                recipe.addIngredient(plugin.getCfg().getPlayerHeadConfig().getIngredient2());
            }
            newTrades.add(recipe);
        });
        return newTrades;
    }

    public static boolean randBoolean(double p) {
        return Math.random() < p;
    }

    public static boolean inBounds(int index, List l) {
        return (index >= 0) && (index < l.size());
    }
}
