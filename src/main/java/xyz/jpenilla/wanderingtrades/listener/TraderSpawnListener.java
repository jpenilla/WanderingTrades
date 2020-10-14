package xyz.jpenilla.wanderingtrades.listener;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.Crafty;
import xyz.jpenilla.jmplib.RandomCollection;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
            traderBlacklistCache.add(e.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() == EntityType.WANDERING_TRADER) {
            Bukkit.getScheduler().runTaskLater(wanderingTrades, () -> addTrades((WanderingTrader) e.getEntity(), false), 1L);
        }
    }

    public void addTrades(WanderingTrader wanderingTrader, boolean refresh) {
        if (!traderBlacklistCache.contains(wanderingTrader.getUniqueId())) {
            Bukkit.getScheduler().runTaskAsynchronously(wanderingTrades, () -> {

                List<MerchantRecipe> newTrades = new ArrayList<>();
                if (wanderingTrades.getCfg().getPlayerHeadConfig().isPlayerHeadsFromServer() && randBoolean(wanderingTrades.getCfg().getPlayerHeadConfig().getPlayerHeadsFromServerChance())) {
                    newTrades.addAll(wanderingTrades.getStoredPlayers().getPlayerHeadsFromServer());
                }
                if (wanderingTrades.getCfg().isAllowMultipleSets()) {
                    ImmutableList.copyOf(wanderingTrades.getCfg().getTradeConfigs().values()).forEach(config -> {
                        if (randBoolean(config.getChance())) {
                            newTrades.addAll(config.getTrades(false));
                        }
                    });
                } else {
                    RandomCollection<String> configNames = new RandomCollection<>();
                    wanderingTrades.getCfg().getTradeConfigs().forEach((key, value) -> configNames.add(value.getChance(), key));
                    String chosenConfig = configNames.next();
                    if (chosenConfig != null) {
                        newTrades.addAll(wanderingTrades.getCfg().getTradeConfigs().get(chosenConfig).getTrades(false));
                    }
                }

                Bukkit.getScheduler().runTask(wanderingTrades, () -> {
                    if (!refresh) {
                        newTrades.addAll(wanderingTrader.getRecipes());
                    } else if (wanderingTrades.getMajorMinecraftVersion() < 17 && !wanderingTrades.getCfg().isRemoveOriginalTrades()) { // TODO -> Check on new Minecraft Version/NMS Mappings
                        resetOffers(wanderingTrader);
                        newTrades.addAll(wanderingTrader.getRecipes());
                    }
                    wanderingTrader.setRecipes(newTrades);
                });

            });
        } else {
            traderBlacklistCache.remove(wanderingTrader.getUniqueId());
        }
    }

    /**
     * Clear this {@link AbstractVillager}'s offers and acquire new ones.
     * <p>
     * Reflection-based implementation of
     * <a href="https://github.com/pl3xgaming/Purpur/blob/de30a3e5e293a5ece224bac0bb2301095ad6ddbf/patches/api/0016-Villager-resetOffers.patch#L19">Purpur's Villager-resetOffers API</a>
     *
     * @param trader the trader to act on
     */
    private void resetOffers(@NonNull AbstractVillager trader) {
        try {
            // TODO -> Check on new Minecraft Version/NMS Mappings
            String updateTradesMethodName = "eW";
            switch (wanderingTrades.getMajorMinecraftVersion()) {
                case 14:
                    updateTradesMethodName = "eh";
                    break;
                case 15:
                    updateTradesMethodName = "eC";
                    break;
                case 16:
                    updateTradesMethodName = "eW";
                    break;
            }

            Class<?> _CraftAbstractVillager = Crafty.needCraftClass("entity.CraftAbstractVillager");
            Class<?> _EntityVillagerAbstract = Crafty.needNmsClass("EntityVillagerAbstract");
            MethodHandle _getHandle = Crafty.findMethod(_CraftAbstractVillager, "getHandle", _EntityVillagerAbstract);
            Method _resetTrades = _EntityVillagerAbstract.getDeclaredMethod(updateTradesMethodName);
            Field _trades = Crafty.needField(_EntityVillagerAbstract, "trades");

            Object nmsTrader = Objects.requireNonNull(_getHandle).bindTo(trader).invoke();
            _trades.set(nmsTrader, Crafty.needNmsClass("MerchantRecipeList").newInstance());

            _resetTrades.setAccessible(true);
            _resetTrades.invoke(nmsTrader);
        } catch (Throwable e) {
            trader.setRecipes(new ArrayList<>());
            e.printStackTrace();
            wanderingTrades.getLog().warn("Failed to reset trades! Please report this bug to the issue tracker  at " + wanderingTrades.getDescription().getWebsite() + " !");
        }
    }
}
