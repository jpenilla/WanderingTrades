package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.PaperCommandManager;
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

        mgr.getCommandCompletions().registerCompletion("wtWorlds", c -> {
            CommandSender s = c.getSender();
            ArrayList<String> completions = new ArrayList<>();
            for(World w : Bukkit.getWorlds()) {
                completions.add(w.getName() + ":x,y,z");
            }
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
