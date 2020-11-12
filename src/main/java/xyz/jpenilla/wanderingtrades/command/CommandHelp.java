package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.Description;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

import java.util.stream.Collectors;

import static xyz.jpenilla.wanderingtrades.command.CommandManager.metaWithDescription;

public class CommandHelp implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager commandManager;
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final CommandHelpHandler<CommandSender> commandHelpHandler;

    public CommandHelp(WanderingTrades wanderingTrades, CommandManager commandManager) {
        this.wanderingTrades = wanderingTrades;
        this.commandManager = commandManager;
        this.minecraftHelp = commandManager.getHelp();
        this.commandHelpHandler = commandManager.getCommandHelpHandler();
    }

    @Override
    public void registerCommands() {
        /* Help Query Argument */
        CommandArgument<CommandSender, String> helpQueryArgument = StringArgument.<CommandSender>newBuilder("query")
                .greedy()
                .asOptional()
                .withSuggestionsProvider((context, input) -> {
                    final CommandHelpHandler.IndexHelpTopic<CommandSender> indexHelpTopic =
                            (CommandHelpHandler.IndexHelpTopic<CommandSender>) commandHelpHandler.queryHelp(context.getSender(), "");
                    return indexHelpTopic.getEntries()
                            .stream()
                            .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                            .collect(Collectors.toList());
                })
                .build();

        /* Help Command */
        commandManager.command(
                commandManager.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_HELP)), "wanderingtrades")
                        .literal("help")
                        .argument(helpQueryArgument, Description.of(wanderingTrades.getLang().get(Lang.COMMAND_ARGUMENT_HELP_QUERY)))
                        .handler(context -> minecraftHelp.queryCommands(
                                context.getOptional(helpQueryArgument).orElse(""),
                                context.getSender()
                        ))
        );
    }
}
