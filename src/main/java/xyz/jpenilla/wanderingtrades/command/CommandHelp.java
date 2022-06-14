package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

public class CommandHelp implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager commandManager;
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final CommandHelpHandler<CommandSender> commandHelpHandler;

    public CommandHelp(WanderingTrades wanderingTrades, CommandManager commandManager) {
        this.wanderingTrades = wanderingTrades;
        this.commandManager = commandManager;
        this.minecraftHelp = commandManager.minecraftHelp();
        this.commandHelpHandler = commandManager.createCommandHelpHandler();
    }

    @Override
    public void register() {
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
        final Command<CommandSender> help = commandManager.commandBuilder("wt", "wanderingtrades")
            .meta(CommandMeta.DESCRIPTION, wanderingTrades.langConfig().get(Lang.COMMAND_WT_HELP))
            .literal("help")
            .argument(helpQueryArgument, ArgumentDescription.of(wanderingTrades.langConfig().get(Lang.COMMAND_ARGUMENT_HELP_QUERY)))
            .handler(context -> minecraftHelp.queryCommands(
                context.getOptional(helpQueryArgument).orElse(""),
                context.getSender()
            ))
            .build();

        commandManager.command(help);
    }
}
