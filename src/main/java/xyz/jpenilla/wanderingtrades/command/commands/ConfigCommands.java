package xyz.jpenilla.wanderingtrades.command.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.gui.ListTradeConfigsInterface;
import xyz.jpenilla.wanderingtrades.gui.ListTradesInterface;
import xyz.jpenilla.wanderingtrades.gui.MainConfigInterface;
import xyz.jpenilla.wanderingtrades.gui.PlayerHeadConfigInterface;
import xyz.jpenilla.wanderingtrades.util.Constants;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static xyz.jpenilla.wanderingtrades.command.argument.TradeConfigParser.tradeConfigParser;

public final class ConfigCommands extends BaseCommand {
    public ConfigCommands(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.commandManager.commandBuilder("wt");

        /* List Trade Configs Command */
        final Command<CommandSender> list = wt
            .commandDescription(Messages.COMMAND_LIST_DESCRIPTION.asDescription())
            .literal("list")
            .permission("wanderingtrades.list")
            .handler(this::executeList)
            .build();

        /* Trade Config Edit Command */
        final Command<Player> edit = wt
            .commandDescription(Messages.COMMAND_EDIT_DESCRIPTION.asDescription())
            .literal("edit")
            .optional("trade_config", tradeConfigParser())
            .permission("wanderingtrades.edit")
            .senderType(Player.class)
            .handler(context -> {
                final TradeConfig config = context.<TradeConfig>optional("trade_config").orElse(null);
                if (config == null) {
                    new ListTradeConfigsInterface(this.plugin).open(context.sender());
                } else {
                    new ListTradesInterface(this.plugin, config).open(context.sender());
                }
            })
            .build();

        /* Plugin Config Edit Command */
        final Command<Player> editConfig = wt
            .commandDescription(Messages.COMMAND_EDITCONFIG_DESCRIPTION.asDescription())
            .literal("editconfig")
            .permission("wanderingtrades.edit")
            .senderType(Player.class)
            .handler(context -> new MainConfigInterface(this.plugin).open(context.sender()))
            .build();

        /* Player Head Config Edit Command */
        final Command<Player> editPlayerHeadConfig = wt
            .commandDescription(Messages.COMMAND_EDITPLAYERHEADS_DESCRIPTION.asDescription())
            .literal("editplayerheads")
            .permission("wanderingtrades.edit")
            .senderType(Player.class)
            .handler(context -> new PlayerHeadConfigInterface(this.plugin).open(context.sender()))
            .build();

        // Needed for 1.19+ as run_command click events can no longer be used to send chat messages
        this.commandManager.command(wt
            .literal("accept-input")
            .required("input", greedyStringParser())
            .permission("wanderingtrades.edit")
            .senderType(Player.class)
            .handler(context -> {
                final Player player = context.sender();
                if (!player.isConversing()) {
                    this.plugin.audiences().player(player).sendMessage(text("Error. This command is meant for use by click events.", NamedTextColor.RED));
                    return;
                }
                player.acceptConversationInput(context.get("input"));
            }));

        /* Held ItemStack Rename Command */
        final Command<Player> nameHeldItem = this.commandManager.commandBuilder("namehelditem")
            .commandDescription(Description.of("Sets the display name of the held ItemStack."))
            .required("name", greedyStringParser(), Description.of("The MiniMessage string to use as a name."))
            .permission("wanderingtrades.namehand")
            .senderType(Player.class)
            .handler(context -> {
                final Player player = context.sender();
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    player.getInventory().setItemInMainHand(
                        ItemBuilder.create(player.getInventory().getItemInMainHand())
                            .miniMessageContext()
                            .customName(context.get("name"))
                            .exitAndBuild()
                    );
                }
            })
            .build();

        this.commands.register(List.of(list, edit, editConfig, editPlayerHeadConfig, nameHeldItem));
    }

    private void executeList(final CommandContext<CommandSender> context) {
        this.chat.send(
            context.sender(),
            Component.textOfChildren(Constants.PREFIX_COMPONENT, Messages.COMMAND_LIST_LOADED)
        );
        final List<TradeConfig> toSort = new ArrayList<>(this.plugin.configManager().tradeConfigs().values());
        toSort.sort(Comparator.comparing(TradeConfig::configName));
        int index = 1;
        for (final TradeConfig cfg : toSort) {
            final String color = cfg.enabled() ? "green" : "red";
            this.chat.send(
                context.sender(),
                String.format(" <gray>%s.</gray> <hover:show_text:'<green>Click to edit'><click:run_command:/wanderingtrades edit %s><%s>%s", index++, cfg.configName(), color, cfg.configName())
            );
        }
    }
}
