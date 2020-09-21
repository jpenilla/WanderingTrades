package xyz.jpenilla.wanderingtrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.gui.ConfigEditGui;
import xyz.jpenilla.wanderingtrades.gui.PlayerHeadConfigGui;
import xyz.jpenilla.wanderingtrades.gui.TradeConfigListGui;
import xyz.jpenilla.wanderingtrades.gui.TradeListGui;
import xyz.jpenilla.wanderingtrades.util.Constants;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("wanderingtrades|wt")
public class CommandWanderingTrades extends BaseCommand {
    private final WanderingTrades wanderingTrades;
    @Dependency
    private Chat chat;

    public CommandWanderingTrades(WanderingTrades p) {
        wanderingTrades = p;
    }

    @Default
    @HelpCommand
    @Description("%COMMAND_WT_HELP")
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("about")
    @Description("%COMMAND_WT_ABOUT")
    public void onAbout(CommandSender sender) {
        ArrayList<String> list = new ArrayList<>();
        final String header = chat.getCenteredMessage("<gradient:white:blue>=============</gradient><gradient:blue:white>=============");
        list.add(header);
        list.add(chat.getCenteredMessage("<hover:show_text:'<rainbow>click me!'><click:open_url:" + wanderingTrades.getDescription().getWebsite() + ">" + wanderingTrades.getName() + " <gradient:blue:green>" + wanderingTrades.getDescription().getVersion()));
        list.add(chat.getCenteredMessage("<gray>By <gradient:gold:yellow>jmp"));
        list.add(header);
        chat.sendParsed(sender, list);
    }

    @Subcommand("reload")
    @CommandPermission("wanderingtrades.reload")
    @Description("%COMMAND_WT_RELOAD")
    public void onReload(CommandSender sender) {
        chat.sendParsed(sender, chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD)));
        wanderingTrades.getCfg().load();
        wanderingTrades.getLang().load();
        wanderingTrades.getListeners().reload();
        wanderingTrades.getCommandHelper().reload();
        wanderingTrades.getStoredPlayers().load();
        chat.sendParsed(sender, chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD_DONE)));
    }

    @Subcommand("list|l")
    @CommandPermission("wanderingtrades.list")
    @Description("%COMMAND_WT_LIST")
    public void onList(CommandSender sender) {
        List<String> configs = new ArrayList<>(wanderingTrades.getCfg().getTradeConfigs().keySet());
        StringBuilder sb = new StringBuilder();
        for (String cfg : configs) {
            sb.append("<hover:show_text:'<rainbow>Click to edit'><click:run_command:/wanderingtrades edit ");
            sb.append(cfg);
            sb.append(">");
            sb.append(cfg);
            if (configs.indexOf(cfg) != configs.size() - 1) {
                sb.append("</click></hover><gray>,</gray> ");
            }
        }
        chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_LIST_LOADED));
        chat.sendParsed(sender, sb.toString());
    }

    @CommandPermission("wanderingtrades.edit")
    @Subcommand("edit|e")
    @Description("%COMMAND_WT_EDIT")
    @CommandCompletion("@wtConfigs")
    @Syntax("<tradeConfig>")
    public void onEditTrades(Player p, @Optional @Values("@wtConfigs") String tradeConfig) {
        if (tradeConfig == null) {
            new TradeConfigListGui().open(p);
        } else {
            new TradeListGui(tradeConfig).open(p);
        }
    }

    @CommandPermission("wanderingtrades.edit")
    @Subcommand("editconfig|ec")
    @Description("%COMMAND_WT_CONFIG")
    public void onEditConfig(Player p) {
        new ConfigEditGui().open(p);
    }

    @CommandPermission("wanderingtrades.edit")
    @Subcommand("editplayerheads|eph")
    @Description("%COMMAND_WT_PH_CONFIG")
    public void onEditPH(Player p) {
        new PlayerHeadConfigGui().open(p);
    }

    private Location resolveLocation(CommandSender sender, Location loc) {
        Location location;
        if (loc != null) {
            location = loc;
        } else if (sender instanceof Player) {
            location = ((Player) sender).getLocation();
        } else {
            throw new InvalidCommandArgument(wanderingTrades.getLang().get(Lang.COMMAND_ERROR_CONSOLE_NEEDS_COORDS), true);
        }
        return location;
    }

    private void summonTrader(CommandSender sender, String tradeConfig, Location loc, boolean disableAI) {
        try {
            List<MerchantRecipe> recipes = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            final WanderingTrader wt = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
            wanderingTrades.getListeners().getTraderSpawnListener().getTraderBlacklistCache().add(wt.getUniqueId());
            wt.setRecipes(recipes);
            wt.setAI(!disableAI);

            PersistentDataContainer p = wt.getPersistentDataContainer();

            TradeConfig t = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig);
            if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                wt.setCustomName(MiniMessageUtil.miniMessageToLegacy(t.getCustomName()));
                wt.setCustomNameVisible(true);
            }
            if (t.isInvincible()) {
                wt.setInvulnerable(true);
                wt.setRemoveWhenFarAway(false);
                wt.setPersistent(true);
                p.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }

            p.set(Constants.CONFIG, PersistentDataType.STRING, tradeConfig);
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
                onList(sender);
            } else {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
        }
    }

    private void summonVillagerTrader(CommandSender sender, String tradeConfig, Location loc, Villager.Type type, Villager.Profession profession, boolean disableAI) {
        try {
            List<MerchantRecipe> recipes = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig).getTrades(true);
            final Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
            v.setVillagerType(type);
            v.setProfession(profession);
            v.setVillagerLevel(5);
            v.setRecipes(recipes);
            v.setAI(!disableAI);

            PersistentDataContainer p = v.getPersistentDataContainer();

            TradeConfig t = wanderingTrades.getCfg().getTradeConfigs().get(tradeConfig);
            if (t.getCustomName() != null && !t.getCustomName().equalsIgnoreCase("NONE")) {
                v.setCustomName(MiniMessageUtil.miniMessageToLegacy(t.getCustomName()));
                v.setCustomNameVisible(true);
            }
            if (t.isInvincible()) {
                v.setInvulnerable(true);
                v.setRemoveWhenFarAway(false);
                v.setPersistent(true);
                p.set(Constants.PROTECT, PersistentDataType.STRING, "true");
            }

            p.set(Constants.CONFIG, PersistentDataType.STRING, tradeConfig);
        } catch (NullPointerException | IllegalStateException ex) {
            if (ex instanceof NullPointerException) {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG));
                onList(sender);
            } else {
                chat.sendParsed(sender, wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_MALFORMED_CONFIG));
            }
        }
    }

    @Subcommand("name")
    @Description("Set the name of entities in line of sight")
    @CommandPermission("wanderingtrades.name")
    public void onName(Player player, String name) {
        for (Entity e : player.getNearbyEntities(10, 10, 10)) {
            if (e instanceof LivingEntity) {
                if (isLookingAt(player, (LivingEntity) e)) {
                    e.setCustomName(MiniMessageUtil.miniMessageToLegacy(name));
                    e.setCustomNameVisible(true);
                }
            }
        }
    }

    @Subcommand("namehand")
    @Description("Set the name of the held item")
    @CommandPermission("wanderingtrades.namehand")
    public void onNameHand(Player player, String name) {
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            player.getInventory().setItemInMainHand(new ItemBuilder(player.getInventory().getItemInMainHand()).setName(name).build());
        }
    }

    private boolean isLookingAt(Player player, LivingEntity entity) {
        Location eye = player.getEyeLocation();
        Vector toEntity = entity.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.99D;
    }

    @Subcommand("summonnatural|sn")
    @Description("%COMMAND_SUMMON_NATURAL")
    @Syntax("<ai> <protect> <refresh> [rotation] [world:x,y,z]")
    @CommandCompletion("true|false true|false true|false @angles @wtWorlds")
    @CommandPermission("wanderingtrades.summonnatural")
    public void onSummonNatural(CommandSender sender, boolean ai, boolean protect, boolean refresh, @Optional Float rotation, @Optional Location location) {
        Location loc = resolveLocation(sender, location);
        if (rotation != null) {
            loc.setYaw(rotation);
        }
        final WanderingTrader wt = (WanderingTrader) loc.getWorld().spawnEntity(loc, EntityType.WANDERING_TRADER);
        PersistentDataContainer persistentDataContainer = wt.getPersistentDataContainer();
        if (refresh) {
            persistentDataContainer.set(Constants.REFRESH_NATURAL, PersistentDataType.STRING, "true");
        }
        if (!ai) {
            wt.setAI(false);
        }
        if (protect) {
            persistentDataContainer.set(Constants.PROTECT, PersistentDataType.STRING, "true");
        }
    }

    @Subcommand("summon|s")
    @CommandPermission("wanderingtrades.summon")
    public class SummonTrader extends BaseCommand {
        @Default
        @Description("%COMMAND_SUMMON")
        @CommandCompletion("@wtConfigs @wtWorlds")
        @Syntax("<tradeConfig> [world:x,y,z]")
        public void onSummon(CommandSender sender, String tradeConfig, @Optional Location location) {
            Location loc = resolveLocation(sender, location);
            summonTrader(sender, tradeConfig, loc, false);
        }

        @Subcommand("noai|n")
        public class NoAI extends BaseCommand {
            @Default
            @Description("")
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
        @Description("%COMMAND_VSUMMON")
        @CommandCompletion("@wtConfigs * * @wtWorlds")
        @Syntax("<tradeConfig> <profession> <type> [world:x,y,z]")
        public void onVillagerSummon(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Location location) {
            Location loc = resolveLocation(sender, location);
            summonVillagerTrader(sender, tradeConfig, loc, type, profession, false);
        }

        @Subcommand("noai|n")
        public class NoAI extends BaseCommand {
            @Default
            @Description("%COMMAND_VSUMMON_NOAI")
            @CommandCompletion("@wtConfigs * * @angles @wtWorlds")
            @Syntax("<tradeConfig> <profession> <type> [rotation] [world:x,y,z]")
            public void onSummonNoAI(CommandSender sender, String tradeConfig, Villager.Profession profession, Villager.Type type, @Optional Float rotation, @Optional Location location) {
                Location loc = resolveLocation(sender, location);
                if (rotation != null) {
                    loc.setYaw(rotation);
                }
                summonVillagerTrader(sender, tradeConfig, loc, type, profession, true);
            }
        }
    }
}
