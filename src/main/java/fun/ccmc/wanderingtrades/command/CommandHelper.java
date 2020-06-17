package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BukkitMessageFormatter;
import co.aikar.commands.CommandReplacements;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        mgr.setFormat(MessageType.ERROR, new BukkitMessageFormatter(ChatColor.RED, ChatColor.WHITE, ChatColor.RED));
        mgr.setFormat(MessageType.SYNTAX, new BukkitMessageFormatter(ChatColor.LIGHT_PURPLE, ChatColor.GREEN, ChatColor.WHITE));
        mgr.setFormat(MessageType.INFO, new BukkitMessageFormatter(ChatColor.LIGHT_PURPLE, ChatColor.GREEN, ChatColor.WHITE));
        mgr.setFormat(MessageType.HELP, new BukkitMessageFormatter(ChatColor.LIGHT_PURPLE, ChatColor.GREEN, ChatColor.WHITE));

        mgr.getCommandCompletions().registerAsyncCompletion("wtConfigs", c -> {
            ArrayList<String> completions = new ArrayList<>();
            Arrays.stream(plugin.getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> completions.add((String) completion));
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("angles", c -> {
            List<String> completions = Arrays.asList(
                    "30", "45", "60", "90", "120", "135", "150", "180",
                    "210", "225", "240", "270", "300", "315", "330", "360");
            if (c.getSender() instanceof Player) {
                completions.add(
                        String.valueOf(Math.round(((Player) c.getSender()).getLocation().getYaw() * 100) / 100)
                );
            }
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("wtWorlds", c -> {
            CommandSender s = c.getSender();
            List<String> completions = Bukkit.getWorlds().stream().map(world -> world.getName() + ":x,y,z").collect(Collectors.toList());
            if (s instanceof Player) {
                Location l = ((Player) s).getLocation();
                double x = (double) Math.round(l.getX() * 100) / 100;
                double y = (double) Math.round(l.getY() * 100) / 100;
                double z = (double) Math.round(l.getZ() * 100) / 100;
                completions.add(l.getWorld().getName() + ":" + x + "," + y + "," + z);
            }
            return completions;
        });

        CommandReplacements replacements = mgr.getCommandReplacements();
        registerReplacements(replacements,
                Lang.COMMAND_WT_HELP, Lang.COMMAND_WT_ABOUT, Lang.COMMAND_WT_RELOAD, Lang.COMMAND_WT_LIST,
                Lang.COMMAND_WT_EDIT, Lang.COMMAND_WT_CONFIG, Lang.COMMAND_WT_PH_CONFIG, Lang.COMMAND_SUMMON,
                Lang.COMMAND_SUMMON_NOAI, Lang.COMMAND_VSUMMON, Lang.COMMAND_VSUMMON_NOAI
        );
    }

    private void registerReplacements(CommandReplacements replacements, Lang... keys) {
        LangConfig l = plugin.getLang();
        Arrays.stream(keys).forEach(key -> {
            replacements.addReplacement(key.toString(), l.get(key));
        });
    }
}
