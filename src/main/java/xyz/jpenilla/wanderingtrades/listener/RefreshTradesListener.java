package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.integration.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.util.Constants;

@NullMarked
public final class RefreshTradesListener implements Listener {
    private final WanderingTrades plugin;

    public RefreshTradesListener(final WanderingTrades instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onClick(final PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof AbstractVillager abstractVillager)) {
            return;
        }

        // Block Name Tags
        final PlayerInventory playerInventory = event.getPlayer().getInventory();
        if (playerInventory.getItemInMainHand().getType() == Material.NAME_TAG
            || playerInventory.getItemInOffHand().getType() == Material.NAME_TAG) {
            final PersistentDataContainer persistentDataContainer = abstractVillager.getPersistentDataContainer();
            if (persistentDataContainer.has(Constants.CONFIG_NAME, PersistentDataType.STRING)
                || persistentDataContainer.has(Constants.PROTECT, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }

        // Update trades
        final @Nullable WorldGuardHook worldGuard = this.plugin.worldGuardHook();
        if (worldGuard == null || worldGuard.passesWhiteBlackList(abstractVillager.getLocation())) {
            this.plugin.tradeApplicator().maybeRefreshTrades(abstractVillager);
        }
    }
}
