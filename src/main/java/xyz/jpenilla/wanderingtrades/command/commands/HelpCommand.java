package xyz.jpenilla.wanderingtrades.command.commands;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.help.HelpHandler;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.help.result.IndexCommandResult;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.suggestion.SuggestionProvider;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;

import static org.incendo.cloud.minecraft.extras.MinecraftHelp.helpColors;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

@DefaultQualifier(NonNull.class)
public final class HelpCommand extends BaseCommand {
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final HelpHandler<CommandSender> commandHelpHandler;

    public HelpCommand(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
        this.minecraftHelp = this.createMinecraftHelp();
        this.commandHelpHandler = this.commandManager.createHelpHandler();
    }

    @Override
    public void register() {
        /* Help Query Argument */
        final TypedCommandComponent<CommandSender, String> helpQueryArgument = CommandComponent.<CommandSender, String>ofType(String.class, "query")
            .parser(greedyStringParser())
            .optional()
            .suggestionProvider(SuggestionProvider.blockingStrings((context, input) -> {
                final IndexCommandResult<CommandSender> indexHelpTopic = this.commandHelpHandler.queryRootIndex(context.sender());
                return indexHelpTopic.entries()
                    .stream()
                    .map(CommandEntry::syntax)
                    .toList();
            }))
            .description(Messages.COMMAND_ARGUMENT_HELP_QUERY.asDescription())
            .defaultValue(DefaultValue.constant(""))
            .build();

        /* Help Command */
        final Command<CommandSender> help = this.commandManager.commandBuilder("wt", "wanderingtrades")
            .commandDescription(Messages.COMMAND_HELP_DESCRIPTION.asDescription())
            .literal("help")
            .argument(helpQueryArgument)
            .handler(context -> this.minecraftHelp.queryCommands(context.get(helpQueryArgument), context.sender()))
            .build();

        this.commandManager.command(help);
    }

    private MinecraftHelp<CommandSender> createMinecraftHelp() {
        return MinecraftHelp.<CommandSender>builder()
            .commandManager(this.commandManager)
            .audienceProvider(AudienceProvider.nativeAudience())
            .commandPrefix("/wanderingtrades help")
            .messageProvider(this::helpMessage)
            .colors(helpColors(
                TextColor.color(0x00a3ff),
                NamedTextColor.WHITE,
                TextColor.color(0x284fff),
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY
            ))
            .build();
    }

    private Component helpMessage(final CommandSender sender, final String key, final Map<String, String> args) {
        if (key.equals("help")) {
            return Messages.COMMAND_HELP_DESCRIPTION.asComponent();
        }

        return MinecraftHelp.captionMessageProvider(this.commandManager.captionRegistry(), ComponentCaptionFormatter.miniMessage())
            .provide(sender, key, args);
    }
}
