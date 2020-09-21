package xyz.jpenilla.wanderingtrades.command;

import co.aikar.commands.BukkitMessageFormatter;
import co.aikar.commands.CommandReplacements;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelper {
    private final WanderingTrades plugin;
    private final PaperCommandManager mgr;

    public CommandHelper(WanderingTrades instance) {
        plugin = instance;
        mgr = new PaperCommandManager(plugin);
        mgr.setFormat(MessageType.ERROR, new BukkitMessageFormatter(ChatColor.BLUE, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setFormat(MessageType.SYNTAX, new BukkitMessageFormatter(ChatColor.BLUE, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setFormat(MessageType.INFO, new BukkitMessageFormatter(ChatColor.BLUE, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setFormat(MessageType.HELP, new BukkitMessageFormatter(ChatColor.BLUE, ChatColor.AQUA, ChatColor.WHITE));
        mgr.setHelpFormatter(new HelpFormatter(plugin, mgr));
        mgr.enableUnstableAPI("help");
        mgr.setDefaultHelpPerPage(4);
        mgr.registerDependency(Chat.class, plugin.getChat());
        reload();
        mgr.registerCommand(new CommandWanderingTrades(plugin));
    }

    public void reload() {
        mgr.getCommandCompletions().registerAsyncCompletion("wtConfigs", c -> ImmutableList.copyOf(plugin.getCfg().getTradeConfigs().keySet()));

        mgr.getCommandCompletions().registerCompletion("angles", c -> {
            final List<String> completions = new ArrayList<>(Arrays.asList(
                    "30", "45", "60", "90", "120", "135", "150", "180",
                    "210", "225", "240", "270", "300", "315", "330", "360"));
            if (c.getSender() instanceof Player) {
                completions.add(String.valueOf(Math.round(((Player) c.getSender()).getLocation().getYaw() * 100) / 100));
            }
            return completions;
        });

        mgr.getCommandCompletions().registerCompletion("wtWorlds", c -> {
            final CommandSender s = c.getSender();
            final ImmutableList.Builder<String> list = ImmutableList.builder();
            list.addAll(Bukkit.getWorlds().stream().map(world -> world.getName() + ":x,y,z").collect(Collectors.toList()));
            if (s instanceof Player) {
                final Location l = ((Player) s).getLocation();
                list.add(String.format("%s:%s,%s,%s",
                        l.getWorld().getName(),
                        (double) Math.round(l.getX() * 100) / 100,
                        (double) Math.round(l.getY() * 100) / 100,
                        (double) Math.round(l.getZ() * 100) / 100)
                );
            }
            return list.build();
        });

        CommandReplacements replacements = mgr.getCommandReplacements();
        registerReplacements(replacements,
                Lang.COMMAND_WT_HELP, Lang.COMMAND_WT_ABOUT, Lang.COMMAND_WT_RELOAD, Lang.COMMAND_WT_LIST,
                Lang.COMMAND_WT_EDIT, Lang.COMMAND_WT_CONFIG, Lang.COMMAND_WT_PH_CONFIG, Lang.COMMAND_SUMMON,
                Lang.COMMAND_SUMMON_NOAI, Lang.COMMAND_VSUMMON, Lang.COMMAND_VSUMMON_NOAI, Lang.COMMAND_SUMMON_NATURAL
        );
    }

    private void registerReplacements(CommandReplacements replacements, Lang... keys) {
        for (Lang key : keys) {
            replacements.addReplacement(key.toString(), plugin.getLang().get(key));
        }
    }
}
