package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Description;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.standard.StringArgument;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.gui.ConfigEditGui;
import xyz.jpenilla.wanderingtrades.gui.PlayerHeadConfigGui;
import xyz.jpenilla.wanderingtrades.gui.TradeConfigListGui;
import xyz.jpenilla.wanderingtrades.gui.TradeListGui;

import java.util.ArrayList;
import java.util.List;

import static xyz.jpenilla.wanderingtrades.command.CommandManager.metaWithDescription;

public class CommandConfig implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandConfig(WanderingTrades wanderingTrades, CommandManager mgr) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.getChat();

        /* Register TradeConfig name Argument */
        mgr.registerArgument("trade_config",
                mgr.argumentBuilder(String.class, "trade_config")
                        .withSuggestionsProvider((context, s) -> ImmutableList.copyOf(wanderingTrades.getCfg().getTradeConfigs().keySet()))
                        .withParser((context, input) -> {
                            if (wanderingTrades.getCfg().getTradeConfigs().containsKey(input.peek())) {
                                return ArgumentParseResult.success(input.remove());
                            }
                            return ArgumentParseResult.failure(new IllegalArgumentException(wanderingTrades.getLang().get(Lang.COMMAND_SUMMON_NO_CONFIG)));
                        }));
    }

    @Override
    public void registerCommands() {
        /* List Trade Configs Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_LIST)))
                        .literal("list")
                        .permission("wanderingtrades.list")
                        .handler(context -> onList(context.getSender()))
        );

        /* Trade Config Edit Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_EDIT)))
                        .literal("edit")
                        .argument(mgr.getArgument("trade_config").asOptional())
                        .permission("wanderingtrades.edit")
                        .senderType(Player.class)
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            final String config = context.<String>getOptional("trade_config").orElse(null);
                            if (config == null) {
                                new TradeConfigListGui().open((Player) context.getSender());
                            } else {
                                new TradeListGui(config).open((Player) context.getSender());
                            }
                        }).execute())
        );

        /* Plugin Config Edit Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_CONFIG)))
                        .literal("editconfig")
                        .permission("wanderingtrades.edit")
                        .senderType(Player.class)
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            new ConfigEditGui().open((Player) context.getSender());
                        }).execute())
        );

        /* Player Head Config Edit Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_PH_CONFIG)))
                        .literal("editplayerheads")
                        .permission("wanderingtrades.edit")
                        .senderType(Player.class)
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            new PlayerHeadConfigGui().open((Player) context.getSender());
                        }).execute())
        );

        /* Held ItemStack Rename Command */
        mgr.command(
                mgr.commandBuilder("namehelditem", metaWithDescription("Sets the display name of the held ItemStack."))
                        .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY),
                                Description.of("The MiniMessage string to use as a name."))
                        .permission("wanderingtrades.namehand")
                        .senderType(Player.class)
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            final Player player = (Player) context.getSender();
                            if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                player.getInventory().setItemInMainHand(new ItemBuilder(player.getInventory().getItemInMainHand()).setName(context.get("name")).build());
                            }
                        }).execute())
        );
    }

    private void onList(CommandSender sender) {
        chat.send(sender, wanderingTrades.getLang().get(Lang.COMMAND_LIST_LOADED));
        List<String> toSort = new ArrayList<>(ImmutableList.copyOf(wanderingTrades.getCfg().getTradeConfigs().keySet()));
        toSort.sort(null);
        int index = 1;
        for (String cfg : toSort) {
            chat.send(sender, String.format(" <gray>%s.</gray> <hover:show_text:'<green>Click to edit'><click:run_command:/wanderingtrades edit %s>%s", index++, cfg, cfg));
        }
    }
}
