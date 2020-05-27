package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import fun.ccmc.wanderingtrades.util.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
    public class SummonTrader extends BaseCommand {
        @Default
        @Description("Summons a Wandering Trader with the specified config. Ignores whether the config is disabled.")
        @CommandCompletion("@wtConfigs @wtWorlds")
        @Syntax("<tradeConfig> [world:x,y,z]")
        public void onSummon(CommandSender sender, String tradeConfig, @Optional Location location) {
            Location loc = resolveLocation(sender, location);
            summonTrader(sender, tradeConfig, loc, false);
        }

        @Subcommand("noai|n")
        @Description("Same as /wt summon but with AI disabled")
        public class NoAI extends BaseCommand {
            @Default
            @CommandCompletion("@wtConfigs @range:360 @wtWorlds")
            @Syntax("<tradeConfig> [rotation] [world:x,y,z]")
            public void onSummonNoAI(CommandSender sender, String tradeConfig, @Optional Float rotation, @Optional Location location) {
                Location loc = resolveLocation(sender, location);
                if(rotation != null) {
                    loc.setYaw(rotation);
                }
                summonTrader(sender, tradeConfig, loc, true);
            }
        }
    }

    @Subcommand("summonvillager|sv")
    @CommandPermission("wanderingtrades.summonvillager")
    public class SummonVillager extends BaseCommand {
        @Default
        @Description("Summons a Villager with the specified config. Ignores whether the config is disabled.")
        @CommandCompletion("@wtConfigs * * @wtWorlds")
        @Syntax("<tradeConfig> <profession> <type> [world:x,y,z]")
        public void onVillagerSummon(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Location location) {
            Location loc = resolveLocation(sender, location);
            summonVillager(sender, tradeConfig, loc, type, profession, false);
        }

        @Subcommand("noai|n")
        @Description("Same as /wt summonvillager but with AI disabled")
        public class NoAI extends BaseCommand {
            @Default
            @CommandCompletion("@wtConfigs * * @range:360 @wtWorlds")
            @Syntax("<tradeConfig> <profession> <type> [rotation] [world:x,y,z]")
            public void onSummonNoAI(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Float rotation, @Optional Location location) {
                Location loc = resolveLocation(sender, location);
                if(rotation != null) {
                    loc.setYaw(rotation);
                }
                summonVillager(sender, tradeConfig, loc, type, profession, true);
            }
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

    @Subcommand("addhand|ah")
    @CommandPermission("wanderingtrades.addhand")
    @Description("Creates a template trade in the specified config with your held item as the result")
    @CommandCompletion("@wtConfigs tradeName @range:20 @boolean")
    @Syntax("<tradeConfig> <tradeName> <maxUses> <experienceReward>")
    public void onAddHand(Player p, String tradeConfig, String tradeName, int maxUses, boolean experienceReward) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        if(!hand.getType().equals(Material.AIR)) {
            try {
                TradeConfig tc = plugin.getCfg().getTradeConfigs().get(tradeConfig);
                if (!tc.writeTrade(tradeConfig, hand, tradeName, maxUses, experienceReward)) {
                    Chat.sendMsg(p, "&4There is already a trade with that name");
                } else {
                    Chat.sendCenteredMessage(p, "&a&oSuccessfully added template trade");
                    onReload(p);
                }
            } catch (NullPointerException e) {
                Chat.sendCenteredMessage(p, "&4&oThere are no trade configs with that name loaded.");
                onList(p);
            }
        }
    }

    private Location resolveLocation(CommandSender sender, Location loc) {
        Location location;
        if(loc != null) {
            location = loc;
        } else if(sender instanceof Player) {
            location = ((Player) sender).getLocation();
        } else {
            throw new InvalidCommandArgument("Console must provide world and coordinates", true);
        }
        return location;
    }

    private void summonTrader(CommandSender sender, String tradeConfig, Location loc, boolean disableAI) {
        try {
            ArrayList<MerchantRecipe> recipes = plugin.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            loc.getWorld().spawn(loc, WanderingTrader.class, wt -> {
                wt.setRecipes(recipes);
                wt.setAI(!disableAI);

                PersistentDataContainer p = wt.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "wtConfig");
                p.set(key, PersistentDataType.STRING, tradeConfig);
            });
        } catch (NullPointerException e) {
            Chat.sendCenteredMessage(sender, "&4&oThere are no trade configs with that name loaded.");
            onList(sender);
        }
    }

    private void summonVillager(CommandSender sender, String tradeConfig, Location loc, Villager.Type type, Villager.Profession profession, boolean disableAI) {
        try {
            ArrayList<MerchantRecipe> recipes = plugin.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            loc.getWorld().spawn(loc, Villager.class, v -> {
                v.setVillagerType(type);
                v.setProfession(profession);
                v.setVillagerLevel(5);
                v.setRecipes(recipes);
                v.setAI(!disableAI);

                PersistentDataContainer p = v.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "wtConfig");
                p.set(key, PersistentDataType.STRING, tradeConfig);
            });
        } catch (NullPointerException e) {
            Chat.sendCenteredMessage(sender, "&4&oThere are no trade configs with that name loaded.");
            onList(sender);
        }
    }
}
