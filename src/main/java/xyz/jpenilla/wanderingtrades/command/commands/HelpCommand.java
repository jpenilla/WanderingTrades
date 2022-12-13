package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.util.Components;

@DefaultQualifier(NonNull.class)
public final class HelpCommand extends BaseCommand {
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final CommandHelpHandler<CommandSender> commandHelpHandler;

    public HelpCommand(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
        this.minecraftHelp = this.createMinecraftHelp();
        this.commandHelpHandler = this.commandManager.createCommandHelpHandler();
    }

    @Override
    public void register() {
        /* Help Query Argument */
        final CommandArgument<CommandSender, String> helpQueryArgument = StringArgument.<CommandSender>builder("query")
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
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Messages.COMMAND_HELP_DESCRIPTION.asComponent())
            .literal("help")
            .argument(helpQueryArgument, Messages.COMMAND_ARGUMENT_HELP_QUERY.asDescription())
            .handler(context -> this.minecraftHelp.queryCommands(
                context.getOptional(helpQueryArgument).orElse(""),
                context.getSender()
            ))
            .build();

        this.commandManager.command(help);
    }

    private MinecraftHelp<CommandSender> createMinecraftHelp() {
        final MinecraftHelp<CommandSender> minecraftHelp = new MinecraftHelp<>(
            "/wanderingtrades help",
            this.plugin.audiences()::sender,
            this.commandManager
        );
        minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
            TextColor.color(0x00a3ff),
            NamedTextColor.WHITE,
            TextColor.color(0x284fff),
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY
        ));
        minecraftHelp.messageProvider(this::helpMessage);
        return minecraftHelp;
    }

    private Component helpMessage(final CommandSender sender, final String key, final String... args) {
        // Hack but works
        final TagResolver[] placeholders;
        if (args.length == 0) {
            placeholders = new TagResolver[]{};
        } else {
            placeholders = new TagResolver[]{
                Components.placeholder("page", args[0]),
                Components.placeholder("max_pages", args[1])
            };
        }

        return ((Messages.SingleMessage) Messages.get("command.help.message." + key.replace("_", "-")))
            .withPlaceholders(placeholders);
    }
}
