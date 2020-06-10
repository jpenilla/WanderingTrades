package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.PaperCommandManager;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {
    private final WanderingTrades plugin;

    public CommandHelper(WanderingTrades instance) {
        plugin = instance;
    }

    public void register() {
        PaperCommandManager mgr = plugin.getCommandManager();

        mgr.getCommandCompletions().registerAsyncCompletion("wtConfigs", c -> {
            ArrayList<String> completions = new ArrayList<>();
            Arrays.stream(plugin.getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> completions.add((String) completion));
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("angles", c -> {
            List<String> completions = Arrays.asList(
                    "30", "45", "60", "90", "120", "135", "150", "180",
                    "210", "225", "240", "270", "300", "315", "330", "360");
            if(c.getSender() instanceof Player) {
                completions.add(
                        String.valueOf(Math.round(((Player) c.getSender()).getLocation().getYaw() * 100) / 100)
                );
            }
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("wtWorlds", c -> {
            CommandSender s = c.getSender();
            List<String> completions = Bukkit.getWorlds().stream().map(world -> world.getName() + ":x,y,z").collect(Collectors.toList());
            if(s instanceof Player) {
                Location l = ((Player) s).getLocation();
                double x = (double) Math.round(l.getX() * 100) / 100;
                double y = (double) Math.round(l.getY() * 100) / 100;
                double z = (double) Math.round(l.getZ() * 100) / 100;
                completions.add(l.getWorld().getName() + ":" + x + "," + y + "," + z);
            }
            return completions;
        });

        CommandReplacements replacements = mgr.getCommandReplacements();
        replacements.addReplacements(
                "COMMAND_WT_HELP", plugin.getLang().get(Lang.COMMAND_WT_HELP),
                "COMMAND_WT_ABOUT", plugin.getLang().get(Lang.COMMAND_WT_ABOUT),
                "COMMAND_WT_RELOAD", plugin.getLang().get(Lang.COMMAND_WT_RELOAD),
                "COMMAND_WT_LIST", plugin.getLang().get(Lang.COMMAND_WT_LIST),
                "COMMAND_WT_EDIT", plugin.getLang().get(Lang.COMMAND_WT_EDIT),
                "COMMAND_WT_CONFIG", plugin.getLang().get(Lang.COMMAND_WT_CONFIG),
                "COMMAND_WT_PH_CONFIG", plugin.getLang().get(Lang.COMMAND_WT_PH_CONFIG),
                "COMMAND_SUMMON", plugin.getLang().get(Lang.COMMAND_SUMMON),
                "COMMAND_SUMMON_NOAI", plugin.getLang().get(Lang.COMMAND_SUMMON_NOAI),
                "COMMAND_VSUMMON", plugin.getLang().get(Lang.COMMAND_VSUMMON),
                "COMMAND_VSUMMON_NOAI", plugin.getLang().get(Lang.COMMAND_VSUMMON_NOAI)
        );
    }
}
