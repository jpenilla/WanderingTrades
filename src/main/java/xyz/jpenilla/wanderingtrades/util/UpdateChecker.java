package xyz.jpenilla.wanderingtrades.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public final class UpdateChecker {
    private static final Gson GSON = new GsonBuilder().create();

    private final WanderingTrades plugin;
    private final String githubRepo;

    public UpdateChecker(
        final WanderingTrades plugin,
        final String githubRepo
    ) {
        this.plugin = plugin;
        this.githubRepo = githubRepo;
    }

    public void checkVersion() {
        final JsonArray result;
        try (final InputStreamReader urlReader = new InputStreamReader(new URL("https://api.github.com/repos/" + this.githubRepo + "/releases").openStream(), Charsets.UTF_8)) {
            result = GSON.fromJson(urlReader, JsonArray.class);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.INFO, "Failed to look for updates", exception);
            return;
        }

        final Map<String, String> versionMap = new LinkedHashMap<>();
        result.forEach(element -> versionMap.put(element.getAsJsonObject().get("tag_name").getAsString(), element.getAsJsonObject().get("html_url").getAsString()));
        final List<String> versionList = new LinkedList<>(versionMap.keySet());
        final String currentVersion = "v" + this.plugin.getDescription().getVersion();
        if (versionList.get(0).equals(currentVersion)) {
            return; // Up to date, do nothing
        }
        if (currentVersion.contains("SNAPSHOT")) {
            this.plugin.getLogger().info("This server is running a development build of " + this.plugin.getName() + "! (" + currentVersion + ")");
            this.plugin.getLogger().info("The latest official release is " + versionList.get(0));
            return;
        }
        final int versionsBehind = versionList.indexOf(currentVersion);
        this.plugin.getLogger().info("There is an update available for " + this.plugin.getName() + "!");
        this.plugin.getLogger().info("This server is running version " + currentVersion + ", which is " + (versionsBehind == -1 ? "UNKNOWN" : versionsBehind) + " versions outdated.");
        this.plugin.getLogger().info("Download the latest version, " + versionList.get(0) + " from GitHub at the link below:");
        this.plugin.getLogger().info(versionMap.get(versionList.get(0)));
    }
}
