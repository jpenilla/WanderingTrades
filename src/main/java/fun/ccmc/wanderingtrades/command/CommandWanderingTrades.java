package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ShowCommandHelp;
import co.aikar.commands.annotation.*;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.Chat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Subcommand("summon|s")
    @CommandPermission("wanderingtrades.summon")
    @Description("Summons a Wandering Trader with the specified config. Ignores whether the config is disabled.")
    @CommandCompletion("configName *")
    @Syntax("<tradeConfig> [world:x,y,z]")
    public void onSummon(CommandSender sender, String tradeConfig, @Optional Location location) {
        Location loc;
        if(location != null) {
            loc = location;
        } else if(sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            Chat.sendMsg(sender, "&4Console must provide world and coordinates");
            throw new ShowCommandHelp();
        }
        try {
            ArrayList<MerchantRecipe> recipes = plugin.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            loc.getWorld().spawn(loc, WanderingTrader.class, wt -> {
                wt.setRecipes(recipes);
            });
        } catch (NullPointerException e) {
            Chat.sendCenteredMessage(sender, "&4&oThere are no trade configs with that name loaded.");
            onList(sender);
        }
    }

    @Subcommand("summonvillager|sv")
    @CommandPermission("wanderingtrades.summonvillager")
    @Description("Summons a Villager with the specified config. Ignores whether the config is disabled.")
    @CommandCompletion("configName * * *")
    @Syntax("<tradeConfig> <profession> <type> [world:x,y,z]")
    public void onVillagerSummon(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Location location) {
        Location loc;
        if(location != null) {
            loc = location;
        } else if(sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            Chat.sendMsg(sender, "&4Console must provide world and coordinates");
            throw new ShowCommandHelp();
        }
        try {
            ArrayList<MerchantRecipe> recipes = plugin.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            loc.getWorld().spawn(loc, Villager.class, v -> {
                v.setVillagerType(type);
                v.setProfession(profession);
                v.setVillagerLevel(5);
                v.setRecipes(recipes);
            });
        } catch (NullPointerException e) {
            Chat.sendCenteredMessage(sender, "&4&oThere are no trade configs with that name loaded.");
            onList(sender);
        }
    }

    @Subcommand("list|l")
    @CommandPermission("wanderingtrades.list")
    @Description("Lists the loaded trade configs.")
    public void onList(CommandSender sender) {
        Object[] objects = plugin.getCfg().getTradeConfigs().keySet().toArray();
        String[] strings = Arrays.stream(objects).toArray(String[]::new);
        String string = String.join("&7, &r", strings);
        Chat.sendMsg(sender, "&d&oLoaded Trade Configs:");
        Chat.sendMsg(sender, string);
    }
}
