package xyz.jpenilla.wanderingtrades.compat;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class VaultCompat {
    private final WanderingTrades wanderingTrades;
    @Getter private Permission perms;

    public VaultCompat(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
        wanderingTrades.setVaultPermissions(setupPermissions());
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = wanderingTrades.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            perms = rsp.getProvider();
        }
        return perms != null;
    }
}
