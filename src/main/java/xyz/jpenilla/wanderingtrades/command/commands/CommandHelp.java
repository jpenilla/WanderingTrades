package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.config.Lang;

@DefaultQualifier(NonNull.class)
public final class CommandHelp extends BaseCommand {
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final CommandHelpHandler<CommandSender> commandHelpHandler;

    public CommandHelp(final WanderingTrades plugin, final CommandManager commandManager) {
        super(plugin, commandManager);
        this.minecraftHelp = commandManager.minecraftHelp();
        this.commandHelpHandler = commandManager.createCommandHelpHandler();
    }

    @Override
    public void register() {
        /* Help Query Argument */
        final CommandArgument<CommandSender, String> helpQueryArgument = StringArgument.<CommandSender>newBuilder("query")
            .greedy()
            .asOptional()
            .withSuggestionsProvider((context, input) -> {
                final CommandHelpHandler.IndexHelpTopic<CommandSender> indexHelpTopic =
                    (CommandHelpHandler.IndexHelpTopic<CommandSender>) this.commandHelpHandler.queryHelp(context.getSender(), "");
                return indexHelpTopic.getEntries()
                    .stream()
                    .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                    .toList();
            })
            .build();

        /* Help Command */
        final Command<CommandSender> help = this.commandManager.commandBuilder("wt", "wanderingtrades")
            .meta(CommandMeta.DESCRIPTION, this.plugin.langConfig().get(Lang.COMMAND_WT_HELP))
            .literal("help")
            .argument(helpQueryArgument, ArgumentDescription.of(this.plugin.langConfig().get(Lang.COMMAND_ARGUMENT_HELP_QUERY)))
            .handler(context -> this.minecraftHelp.queryCommands(
                context.getOptional(helpQueryArgument).orElse(""),
                context.getSender()
            ))
            .build();

        this.commandManager.command(help);
    }
}
