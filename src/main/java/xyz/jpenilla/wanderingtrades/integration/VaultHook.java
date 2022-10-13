package xyz.jpenilla.wanderingtrades.integration;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {
    private final Server server;
    private Permission permissions;

    public VaultHook(final Server server) {
        this.server = server;
        this.setupPermissions();
    }

    private void setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = this.server.getServicesManager()
            .getRegistration(Permission.class);
        if (rsp != null) {
            this.permissions = rsp.getProvider();
        }
    }

    public Permission permissions() {
        return this.permissions;
    }
}
