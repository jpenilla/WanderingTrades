package xyz.jpenilla.wanderingtrades.util;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public interface PlayerHeads {
    List<MerchantRecipe> randomlySelectPlayerHeads();

    void handleLogin(final Player player);

    void handleLogout(final Player player);

    void configChanged();

    static PlayerHeads create(final WanderingTrades plugin) {
        return new PlayerHeadsImpl(plugin);
    }
}
