package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Components;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
public final class CommandManager extends PaperCommandManager<CommandSender> {
    public static final CloudKey<WanderingTrades> PLUGIN = SimpleCloudKey.of("wt:plugin", TypeToken.get(WanderingTrades.class));
    private static final Pattern SYNTAX_HIGHLIGHT_PATTERN = Pattern.compile("[^\\s\\w\\-]");

    private final WanderingTrades wanderingTrades;
    private final MinecraftHelp<CommandSender> help;
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public CommandManager(final WanderingTrades wanderingTrades) throws Exception {
        super(
            wanderingTrades,
            AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(),
            Function.identity(),
            Function.identity()
        );
        this.wanderingTrades = wanderingTrades;

        this.help = new MinecraftHelp<>("/wanderingtrades help", wanderingTrades.audiences()::sender, this);
        this.help.setHelpColors(MinecraftHelp.HelpColors.of(
            TextColor.color(0x00a3ff),
            NamedTextColor.WHITE,
            TextColor.color(0x284fff),
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY
        ));
        this.help.setMessageProvider((sender, key) -> wanderingTrades.langConfig().get(Lang.valueOf(String.format("HELP_%s", key).toUpperCase())));

        this.registerExceptionHandlers();

        if (this.captionRegistry() instanceof final SimpleCaptionRegistry<CommandSender> registry) {
            registry.registerMessageFactory(
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                (caption, sender) -> wanderingTrades.langConfig().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_ENUM)
            );
            registry.registerMessageFactory(
                BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE,
                (caption, sender) -> wanderingTrades.langConfig().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE)
            );
            registry.registerMessageFactory(
                BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT,
                (caption, sender) -> wanderingTrades.langConfig().get(Lang.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT)
            );
        }

        /* Register Brigadier */
        if (this.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = this.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
            wanderingTrades.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        }

        /* Register Asynchronous Completion Listener */
        if (this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.registerAsynchronousCompletions();
            wanderingTrades.getLogger().info("Successfully registered asynchronous command completion listener.");
        }

        this.parserRegistry().registerParserSupplier(
            TypeToken.get(TradeConfig.class),
            parameters -> new TradeConfigArgument.Parser()
        );

        this.registerCommandPreProcessor(ctx -> ctx.getCommandContext().store(PLUGIN, wanderingTrades));

        /* Register Commands */
        Stream.of(
            new CommandHelp(wanderingTrades, this),
            new CommandWanderingTrades(wanderingTrades, this),
            new CommandSummon(wanderingTrades, this),
            new CommandConfig(wanderingTrades, this)
        ).forEach(WTCommand::register);
    }

    private void registerExceptionHandlers() {
        new MinecraftExceptionHandler<CommandSender>()
            .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, e ->
                Component.translatable("commands.help.failed", NamedTextColor.RED))
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, e -> {
                final InvalidSyntaxException exception = (InvalidSyntaxException) e;
                final Component invalidSyntaxMessage = Component.text(this.wanderingTrades.langConfig().get(Lang.COMMAND_INVALID_SYNTAX), NamedTextColor.RED);
                final Component correctSyntaxMessage = Component.text(
                    String.format("/%s", exception.getCorrectSyntax()),
                    NamedTextColor.GRAY
                ).replaceText(config -> {
                    config.match(SYNTAX_HIGHLIGHT_PATTERN);
                    config.replacement(builder -> builder.color(NamedTextColor.WHITE));
                });

                return Components.ofChildren(invalidSyntaxMessage, correctSyntaxMessage);
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, e -> {
                final InvalidCommandSenderException exception = (InvalidCommandSenderException) e;
                final Component invalidSenderMessage = Component.text(
                    this.wanderingTrades.langConfig().get(Lang.COMMAND_INVALID_SENDER),
                    NamedTextColor.RED
                );
                final Component correctSenderType = Component.text(
                    exception.getRequiredSender().getSimpleName(),
                    NamedTextColor.GRAY
                );
                return invalidSenderMessage.replaceText(config -> {
                    config.matchLiteral("{type}");
                    config.replacement(match -> correctSenderType);
                });
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, e -> {
                final Component invalidArgumentMessage = Component.text(this.wanderingTrades.langConfig().get(Lang.COMMAND_INVALID_ARGUMENT), NamedTextColor.RED);
                final Component causeMessage = Component.text(e.getCause().getMessage(), NamedTextColor.GRAY);
                return Components.ofChildren(invalidArgumentMessage, causeMessage);
            })
            .withCommandExecutionHandler()
            .withDecorator(component -> Components.ofChildren(Constants.PREFIX_COMPONENT, component))
            .apply(this, this.wanderingTrades.audiences()::sender);
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this::command);
    }

    public MinecraftHelp<CommandSender> minecraftHelp() {
        return this.help;
    }
}
