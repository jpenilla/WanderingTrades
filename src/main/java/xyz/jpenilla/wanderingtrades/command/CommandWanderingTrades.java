package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.Description;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

import java.util.stream.Collectors;

import static xyz.jpenilla.wanderingtrades.command.CommandHelper.metaWithDescription;

public class CommandWanderingTrades implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final PaperCommandManager<CommandSender> mgr;
    private final CommandHelper commandHelper;
    private final Chat chat;

    public CommandWanderingTrades(WanderingTrades wanderingTrades, PaperCommandManager<CommandSender> mgr, CommandHelper commandHelper) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.commandHelper = commandHelper;
        this.chat = wanderingTrades.getChat();
    }

    @Override
    public void registerCommands() {
        /* Help Query Argument */
        CommandArgument<CommandSender, String> helpQueryArgument = StringArgument.<CommandSender>newBuilder("query")
                .greedy()
                .asOptional()
                .withSuggestionsProvider((context, input) ->
                        ((CommandHelpHandler.IndexHelpTopic<CommandSender>) mgr.getCommandHelpHandler().queryHelp(context.getSender(), ""))
                                .getEntries().stream().map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString).collect(Collectors.toList())
                ).build();

        /* Help Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_HELP)), "wanderingtrades")
                        .literal("help")
                        .argument(helpQueryArgument, Description.of(wanderingTrades.getLang().get(Lang.COMMAND_ARGUMENT_HELP_QUERY)))
                        .handler(context -> wanderingTrades.getCommandHelper().getHelp()
                                .queryCommands(context.getOrDefault(helpQueryArgument, ""), context.getSender()))
        );

        /* About Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_ABOUT)))
                        .literal("about")
                        .handler(context -> ImmutableList.of(
                                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
                                "<hover:show_text:'<rainbow>click me!'><click:open_url:" + wanderingTrades.getDescription().getWebsite() + ">" + wanderingTrades.getName() + " <gradient:blue:green>" + wanderingTrades.getDescription().getVersion(),
                                "<gray>By <gradient:gold:yellow>jmp",
                                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
                        ).forEach(string -> chat.send(context.getSender(), chat.getCenteredMessage(string))))
        );

        /* Reload Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_RELOAD)))
                        .literal("reload")
                        .permission("wanderingtrades.reload")
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            chat.sendParsed(context.getSender(), chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD)));
                            wanderingTrades.getCfg().load();
                            wanderingTrades.getLang().load();
                            wanderingTrades.getListeners().reload();
                            wanderingTrades.getStoredPlayers().load();
                            chat.sendParsed(context.getSender(), chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD_DONE)));
                        }).execute())
        );
    }
}
