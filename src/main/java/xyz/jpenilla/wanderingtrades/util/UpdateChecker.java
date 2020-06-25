package xyz.jpenilla.wanderingtrades.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final WanderingTrades plugin;
    private final int resourceId;

    public UpdateChecker(WanderingTrades plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public static void updateCheck(String version) {
        updateCheck(version, false);
    }

    public static void updateCheck(String version, boolean startup) {
        if (WanderingTrades.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
            if (startup) {
                WanderingTrades.getInstance().getLog().info("You are running the latest version of " + WanderingTrades.getInstance().getName() + "! :)");
            }
        } else if (WanderingTrades.getInstance().getDescription().getVersion().contains("SNAPSHOT")) {
            WanderingTrades.getInstance().getLog().info("[!] You are running a development build of " + WanderingTrades.getInstance().getName() + " (" + WanderingTrades.getInstance().getDescription().getVersion() + ") [!]");
        } else {
            WanderingTrades.getInstance().getLog().info("[!] You are running an outdated version of " + WanderingTrades.getInstance().getName() + " (" + WanderingTrades.getInstance().getDescription().getVersion() + ") [!]");
            WanderingTrades.getInstance().getLog().info("Version " + version + " is available at https://www.spigotmc.org/resources/wanderingtrades.79068/");
        }
    }

    public void getVersion(final Consumer<String> consumer) {
        class GetVersionTask extends BukkitRunnable {
            @Override
            public void run() {
                try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                } catch (IOException exception) {
                    plugin.getLog().info("Cannot look for updates: " + exception.getMessage());
                }
            }
        }
        new GetVersionTask().runTaskAsynchronously(plugin);
    }
}