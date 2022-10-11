package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.gui.ConfigEditGui;
import xyz.jpenilla.wanderingtrades.gui.PlayerHeadConfigGui;
import xyz.jpenilla.wanderingtrades.gui.TradeConfigListGui;
import xyz.jpenilla.wanderingtrades.gui.TradeListGui;

public final class CommandConfig extends BaseCommand {
    public CommandConfig(WanderingTrades plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = commandManager.commandBuilder("wt");

        /* List Trade Configs Command */
        final Command<CommandSender> list = wt
                .meta(CommandMeta.DESCRIPTION, plugin.langConfig().get(Lang.COMMAND_WT_LIST))
                .literal("list")
                .permission("wanderingtrades.list")
                .handler(context -> onList(context.getSender()))
                .build();

        /* Trade Config Edit Command */
        final Command<CommandSender> edit = wt
                .meta(CommandMeta.DESCRIPTION, plugin.langConfig().get(Lang.COMMAND_WT_EDIT))
                .literal("edit")
                .argument(TradeConfigArgument.optional("trade_config"))
                .permission("wanderingtrades.edit")
                .senderType(Player.class)
                .handler(c -> commandManager.taskRecipe().begin(c).synchronous(context -> {
                    final TradeConfig config = context.<TradeConfig>getOptional("trade_config").orElse(null);
                    if (config == null) {
                        new TradeConfigListGui(this.plugin).open((Player) context.getSender());
                    } else {
                        new TradeListGui(this.plugin, config).open((Player) context.getSender());
                    }
                }).execute())
                .build();

        /* Plugin Config Edit Command */
        final Command<CommandSender> editConfig = wt
                .meta(CommandMeta.DESCRIPTION, plugin.langConfig().get(Lang.COMMAND_WT_CONFIG))
                .literal("editconfig")
                .permission("wanderingtrades.edit")
                .senderType(Player.class)
                .handler(c -> commandManager.taskRecipe().begin(c).synchronous(context -> {
                    new ConfigEditGui(this.plugin).open((Player) context.getSender());
                }).execute())
                .build();

        /* Player Head Config Edit Command */
        final Command<CommandSender> editPlayerHeadConfig = wt
                .meta(CommandMeta.DESCRIPTION, plugin.langConfig().get(Lang.COMMAND_WT_PH_CONFIG))
                .literal("editplayerheads")
                .permission("wanderingtrades.edit")
                .senderType(Player.class)
                .handler(c -> commandManager.taskRecipe().begin(c).synchronous(context -> {
                    new PlayerHeadConfigGui(this.plugin).open((Player) context.getSender());
                }).execute())
                .build();

        /* Held ItemStack Rename Command */
        final Command<CommandSender> nameHeldItem = commandManager.commandBuilder("namehelditem")
                .meta(CommandMeta.DESCRIPTION, "Sets the display name of the held ItemStack.")
                .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                        ArgumentDescription.of("The MiniMessage string to use as a name."))
                .permission("wanderingtrades.namehand")
                .senderType(Player.class)
                .handler(c -> commandManager.taskRecipe().begin(c).synchronous(context -> {
                    final Player player = (Player) context.getSender();
                    if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        player.getInventory().setItemInMainHand(new ItemBuilder(player.getInventory().getItemInMainHand()).setName(context.get("name")).build());
                    }
                }).execute())
                .build();

        commandManager.register(List.of(list, edit, editConfig, editPlayerHeadConfig, nameHeldItem));
    }

    private void onList(CommandSender sender) {
        this.chat.send(sender, this.plugin.langConfig().get(Lang.COMMAND_LIST_LOADED));
        final List<String> toSort = new ArrayList<>(this.plugin.config().tradeConfigs().keySet());
        toSort.sort(null);
        int index = 1;
        for (final String cfg : toSort) {
            this.chat.send(sender, String.format(" <gray>%s.</gray> <hover:show_text:'<green>Click to edit'><click:run_command:/wanderingtrades edit %s>%s", index++, cfg, cfg));
        }
    }
}
