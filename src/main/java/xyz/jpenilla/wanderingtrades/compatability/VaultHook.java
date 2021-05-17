package xyz.jpenilla.wanderingtrades.compatability;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class VaultHook {
    private Permission permissions;

    public VaultHook(final @NonNull WanderingTrades wanderingTrades) {
        wanderingTrades.setVaultPermissions(setupPermissions());
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            this.permissions = rsp.getProvider();
        }
        return permissions != null;
    }

    public Permission permissions() {
        return this.permissions;
    }
}
