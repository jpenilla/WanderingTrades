package fun.ccmc.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import fun.ccmc.jmplib.Chat;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import fun.ccmc.wanderingtrades.gui.ConfigGui;
import fun.ccmc.wanderingtrades.gui.PlayerHeadGui;
import fun.ccmc.wanderingtrades.gui.TradeConfigListGui;
import fun.ccmc.wanderingtrades.gui.TradeListGui;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

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
        Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_RELOAD));
        plugin.getCfg().load();
        plugin.getLang().load();
        plugin.getListeners().reload();
        plugin.getTabCompletions().register();
        Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_RELOAD_DONE));
    }

    @Subcommand("list|l")
    @CommandPermission("wanderingtrades.list")
    @Description("Lists the loaded trade configs.")
    public void onList(CommandSender sender) {
        List<String> configs = new ArrayList<>(plugin.getCfg().getTradeConfigs().keySet());
        String commaSeparatedConfigs = String.join("&7, &r", configs);
        Chat.sendMsg(sender, plugin.getLang().get(Lang.COMMAND_LIST_LOADED));
        Chat.sendMsg(sender, commaSeparatedConfigs);
    }

    @Subcommand("edit|e")
    @CommandPermission("wanderingtrades.edit")
    public class Edit extends BaseCommand {
        @Default
        @Subcommand("trade")
        @Description("Opens a GUI menu to edit and create trade configs")
        @CommandCompletion("@wtConfigs")
        @Syntax("<tradeConfig>")
        public void onEditTrades(Player p, @Optional @Values("@wtConfigs") String tradeConfig) {
            if(tradeConfig == null) {
                new TradeConfigListGui().open(p);
            } else {
                new TradeListGui(tradeConfig).open(p);
            }
        }

        @Subcommand("config|c")
        public class EditConfigCmd extends BaseCommand {
            @Default
            @Description("Opens a GUI menu to edit the config.yml settings")
            public void onEditConfig(Player p) {
                new ConfigGui().open(p);
            }
        }

        @Subcommand("playerheads|ph")
        public class EditPH extends BaseCommand {
            @Default
            @Description("Opens a GUI menu to edit the playerheads.yml settings")
            public void onEditPH(Player p) {
                new PlayerHeadGui().open(p);
            }
        }
    }

    private Location resolveLocation(CommandSender sender, Location loc) {
        Location location;
        if (loc != null) {
            location = loc;
        } else if (sender instanceof Player) {
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

                TradeConfig t = plugin.getCfg().getTradeConfigs().get(tradeConfig);
                if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                    wt.setCustomName(TextUtil.colorize(t.getCustomName()));
                    wt.setCustomNameVisible(true);
                }
                if (t.isInvincible()) {
                    wt.setInvulnerable(true);
                    wt.setRemoveWhenFarAway(false);
                    wt.setPersistent(true);
                    NamespacedKey k = new NamespacedKey(plugin, "wtProtect");
                    p.set(k, PersistentDataType.STRING, "true");
                }

                NamespacedKey key = new NamespacedKey(plugin, "wtConfig");
                p.set(key, PersistentDataType.STRING, tradeConfig);
            });
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
                onList(sender);
            } else {
                Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
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

                TradeConfig t = plugin.getCfg().getTradeConfigs().get(tradeConfig);
                if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                    v.setCustomName(TextUtil.colorize(t.getCustomName()));
                    v.setCustomNameVisible(true);
                }
                if (t.isInvincible()) {
                    v.setInvulnerable(true);
                    v.setRemoveWhenFarAway(false);
                    v.setPersistent(true);
                    NamespacedKey k = new NamespacedKey(plugin, "wtProtect");
                    p.set(k, PersistentDataType.STRING, "true");
                }

                NamespacedKey key = new NamespacedKey(plugin, "wtConfig");
                p.set(key, PersistentDataType.STRING, tradeConfig);
            });
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
                onList(sender);
            } else {
                Chat.sendCenteredMessage(sender, plugin.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
        }
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
            @CommandCompletion("@wtConfigs @angles @wtWorlds")
            @Syntax("<tradeConfig> [rotation] [world:x,y,z]")
            public void onSummonNoAI(CommandSender sender, String tradeConfig, @Optional Float rotation, @Optional Location location) {
                Location loc = resolveLocation(sender, location);
                if (rotation != null) {
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
            @CommandCompletion("@wtConfigs * * @angles @wtWorlds")
            @Syntax("<tradeConfig> <profession> <type> [rotation] [world:x,y,z]")
            public void onSummonNoAI(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Float rotation, @Optional Location location) {
                Location loc = resolveLocation(sender, location);
                if (rotation != null) {
                    loc.setYaw(rotation);
                }
                summonVillager(sender, tradeConfig, loc, type, profession, true);
            }
        }
    }
}
