package fun.ccmc.wt.util;

import fun.ccmc.wt.WanderingTrades;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWanderingTrades implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WanderingTrades plugin = WanderingTrades.plugin;

        if (args.length == 0) {
            String[] message = TextFormatting.colorize(
                    new String[]{
                            plugin.getName() + " &d&o" + plugin.getDescription().getVersion(),
                            "Commands&7: &d/wanderingtrades&7, &d/wanderingtrades reload"
                    }
            );

            sender.sendMessage(message);

            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if(sender.hasPermission("wanderingtrades.reload") || !(sender instanceof Player)) {
                    sender.sendMessage(TextFormatting.colorize("&d&oReloading " + plugin.getName() + " config... (This will reload config values only. Server restart required to enable/disable features)"));
                    Config.reload(plugin);
                    sender.sendMessage(TextFormatting.colorize("&dDone"));
                } else {
                    sender.sendMessage(TextFormatting.colorize("&4You do not have permission for that command"));
                }
                return true;
            } else {
                sender.sendMessage(TextFormatting.colorize("&4Subcommand does not exist"));
                return true;
            }
        }

        return false;
    }
}
