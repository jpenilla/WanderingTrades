package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import fun.ccmc.wanderingtrades.WanderingTrades;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class TabCompletions {
    private WanderingTrades plugin;

    public TabCompletions(WanderingTrades instance) {
        plugin = instance;
    }

    public void register() {
        PaperCommandManager mgr = plugin.getCommandManager();

        mgr.getCommandCompletions().registerCompletion("wtConfigs", c -> {
            ArrayList<String> completions = new ArrayList<>();
            Arrays.stream(plugin.getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> completions.add((String) completion));
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("boolean", c -> {
            ArrayList<String> completions = new ArrayList<>();
            completions.add("true");
            completions.add("false");
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("angles", c -> ImmutableList.of(
                "30", "45", "60", "90", "120", "135", "150", "180",
                "210", "225", "240", "270", "300", "315", "330", "360"));

        mgr.getCommandCompletions().registerCompletion("wtWorlds", c -> {
            CommandSender s = c.getSender();
            ArrayList<String> completions = new ArrayList<>();
            String[] worlds = Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new);
            Arrays.stream(worlds).forEach(world -> {
                completions.add(world + ":x,y,z");
            });
            if(s instanceof Player) {
                Location l = ((Player) s).getLocation();
                double x = (double) Math.round(l.getX() * 100) / 100;
                double y = (double) Math.round(l.getY() * 100) / 100;
                double z = (double) Math.round(l.getZ() * 100) / 100;
                completions.add(l.getWorld().getName() + ":" + x + "," + y + "," + z);
            }
            return completions;
        });
    }
}
