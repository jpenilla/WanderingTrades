package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.Chat;
import org.bukkit.command.CommandSender;

@CommandAlias("wanderingtrades|wt")
public class CommandWanderingTrades extends BaseCommand {
    private final WanderingTrades plugin;

    public CommandWanderingTrades(WanderingTrades p) {
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
    public void onAbout(CommandSender sender) {
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
    public void onReload(CommandSender sender) {
        Chat.sendCenteredMessage(sender, "&d&oReloading " + plugin.getName() + " config...");
        plugin.getCfg().reload();
        Chat.sendCenteredMessage(sender, "&aDone.");
    }
}
