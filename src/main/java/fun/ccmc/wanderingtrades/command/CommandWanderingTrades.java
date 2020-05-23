package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import fun.ccmc.wanderingtrades.util.Chat;
import fun.ccmc.wanderingtrades.util.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@CommandAlias("wanderingtrades|wt")
public class CommandWanderingTrades extends BaseCommand {
    private static JavaPlugin plugin;

    public CommandWanderingTrades(JavaPlugin p) {
        plugin = p;
    }

    @Default
    @HelpCommand
    @Description("WanderingTrades Help")
    public void onHelp(CommandSender sender, CommandHelp help) {
        String m = "&f---&a[ &d&l" + plugin.getName() + " Help &a]&f---";
        Chat.sendMsg(sender, m);
        help.showHelp();
    }

    @Subcommand("about")
    @Description("About WanderingTrades")
    public static void onAbout(CommandSender sender) {
        String[] m = new String[]{
                "&a==========================",
                plugin.getName() + " &d&o" + plugin.getDescription().getVersion(),
                "&7By &bjmp",
                "&a=========================="
        };
        Chat.sendCenteredMessage(sender, m);
    }

    @Subcommand("reload")
    @CommandPermission("wanderingtrades.reload")
    @Description("Reloads all config files for WanderingTrades")
    public static void onReload(CommandSender sender) {
        Chat.sendCenteredMessage(sender, "&d&oReloading " + plugin.getName() + " config...");
        Config.reload(plugin);
        Chat.sendCenteredMessage(sender, "&aDone.");
    }
}
