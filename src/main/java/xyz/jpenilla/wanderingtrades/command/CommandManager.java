package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.bukkit.BukkitCommandMetaBuilder;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CommandManager extends PaperCommandManager<CommandSender> {

    private static final Pattern SYNTAX_HIGHLIGHT_PATTERN = Pattern.compile("[^\\s\\w\\-]");

    private final WanderingTrades wanderingTrades;
    @Getter
    private final MinecraftHelp<CommandSender> help;
    private final Map<String, CommandArgument.Builder<CommandSender, ?>> argumentRegistry = new HashMap<>();
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public CommandManager(WanderingTrades wanderingTrades) throws Exception {
        super(
                wanderingTrades,
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(),
                Function.identity(),
                Function.identity()
        );
        this.wanderingTrades = wanderingTrades;

        help = new MinecraftHelp<>("/wanderingtrades help", wanderingTrades.getAudience()::sender, this);
        help.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(0x00a3ff),
                NamedTextColor.WHITE,
                TextColor.color(0x284fff),
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY
        ));
        help.setMessageProvider((sender, key) -> wanderingTrades.getLang().get(Lang.valueOf(String.format("HELP_%s", key).toUpperCase())));

        this.registerExceptionHandlers();

        if (this.getCaptionRegistry() instanceof SimpleCaptionRegistry) {
            final SimpleCaptionRegistry<CommandSender> registry = (SimpleCaptionRegistry<CommandSender>) this.getCaptionRegistry();
            registry.registerMessageFactory(
                    StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                    (caption, sender) -> wanderingTrades.getLang().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_ENUM)
            );
            registry.registerMessageFactory(
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE,
                    (caption, sender) -> wanderingTrades.getLang().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE)
            );
            registry.registerMessageFactory(
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT,
                    (caption, sender) -> wanderingTrades.getLang().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT)
            );
        }

        /* Register Brigadier */
        try {
            this.registerBrigadier();
            wanderingTrades.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        } catch (Exception ignored) {
        }

        /* Register Asynchronous Completion Listener */
        try {
            this.registerAsynchronousCompletions();
            wanderingTrades.getLogger().info("Successfully registered asynchronous command completion listener.");
        } catch (Exception ignored) {
        }

        /* Register Commands */
        ImmutableList.of(
                new CommandHelp(wanderingTrades, this),
                new CommandWanderingTrades(wanderingTrades, this),
                new CommandSummon(wanderingTrades, this),
                new CommandConfig(wanderingTrades, this)
        ).forEach(WTCommand::registerCommands);
    }

    private void registerExceptionHandlers() {
        new MinecraftExceptionHandler<CommandSender>()
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, e ->
                        Component.translatable("commands.help.failed", NamedTextColor.RED))
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, e -> {
                    final InvalidSyntaxException exception = (InvalidSyntaxException) e;
                    final Component invalidSyntaxMessage = Component.text(wanderingTrades.getLang().get(Lang.COMMAND_INVALID_SYNTAX), NamedTextColor.RED);
                    final Component correctSyntaxMessage = Component.text(
                            String.format("/%s", exception.getCorrectSyntax()),
                            NamedTextColor.GRAY
                    ).replaceText(config -> {
                        config.match(SYNTAX_HIGHLIGHT_PATTERN);
                        config.replacement(builder -> builder.color(NamedTextColor.WHITE));
                    });

                    return Component.text()
                            .append(invalidSyntaxMessage)
                            .append(correctSyntaxMessage)
                            .build();
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, e -> {
                    final InvalidCommandSenderException exception = (InvalidCommandSenderException) e;
                    final Component invalidSenderMessage = Component.text(
                            wanderingTrades.getLang().get(Lang.COMMAND_INVALID_SENDER),
                            NamedTextColor.RED
                    );
                    final Component correctSenderType = Component.text(
                            exception.getRequiredSender().getSimpleName(),
                            NamedTextColor.GRAY
                    );
                    return invalidSenderMessage.replaceText(config -> {
                        config.match(Pattern.compile("\\{type}"));
                        config.replacement(match -> correctSenderType);
                    });
                })
                .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, e -> {
                    final Component invalidArgumentMessage = Component.text(wanderingTrades.getLang().get(Lang.COMMAND_INVALID_ARGUMENT), NamedTextColor.RED);
                    final Component causeMessage = Component.text(e.getCause().getMessage(), NamedTextColor.GRAY);
                    return Component.text()
                            .append(invalidArgumentMessage)
                            .append(causeMessage)
                            .build();
                })
                .withCommandExecutionHandler()
                .withDecorator(component -> Component.text()
                        .append(Constants.PREFIX_COMPONENT)
                        .append(component)
                        .build())
                .apply(this, wanderingTrades.getAudience()::sender);
    }

    public CommandArgument.Builder<CommandSender, ?> getArgument(String name) {
        return this.argumentRegistry.get(name);
    }

    public void registerArgument(String name, CommandArgument.Builder<CommandSender, ?> argumentBuilder) {
        this.argumentRegistry.put(name, argumentBuilder);
    }

    public CommandFlag.Builder<?> getFlag(String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(String name, CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public static SimpleCommandMeta metaWithDescription(final String description) {
        return BukkitCommandMetaBuilder.builder().withDescription(description).build();
    }
}
