package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.execution.preprocessor.CommandPreprocessingContext;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.command.commands.AboutCommand;
import xyz.jpenilla.wanderingtrades.command.commands.ConfigCommands;
import xyz.jpenilla.wanderingtrades.command.commands.HelpCommand;
import xyz.jpenilla.wanderingtrades.command.commands.ReloadCommand;
import xyz.jpenilla.wanderingtrades.command.commands.SummonCommands;
import xyz.jpenilla.wanderingtrades.command.commands.TradeCommands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class Commands {
    public static final CloudKey<WanderingTrades> PLUGIN = SimpleCloudKey.of("wt:plugin", TypeToken.get(WanderingTrades.class));

    private final WanderingTrades plugin;
    private final PaperCommandManager<CommandSender> commandManager;
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    private Commands(final WanderingTrades plugin, final PaperCommandManager<CommandSender> commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;

        this.commandManager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
            FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
        ));
        new ExceptionHandler(plugin, commandManager).register();
        this.registerMessageFactories();
        if (this.commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.commandManager.registerBrigadier();
            final @Nullable CloudBrigadierManager<CommandSender, ?> brigManager = this.commandManager.brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }
        this.registerParsers();
        this.commandManager.registerCommandPreProcessor(this::preProcessContext);
        this.registerCommands();
    }

    private void registerCommands() {
        final List<BaseCommand> commands = List.of(
            new HelpCommand(this.plugin, this),
            new ReloadCommand(this.plugin, this),
            new AboutCommand(this.plugin, this),
            new SummonCommands(this.plugin, this),
            new TradeCommands(this.plugin, this),
            new ConfigCommands(this.plugin, this)
        );
        commands.forEach(BaseCommand::register);
    }

    public PaperCommandManager<CommandSender> commandManager() {
        return this.commandManager;
    }

    private void registerMessageFactories() {
        if (!(this.commandManager.captionRegistry() instanceof final SimpleCaptionRegistry<CommandSender> registry)) {
            return;
        }
        registry.registerMessageFactory(
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
            (caption, sender) -> Messages.COMMAND_ARGUMENT_PARSE_FAILURE_ENUM.message()
        );
        registry.registerMessageFactory(
            BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE,
            (caption, sender) -> Messages.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE.message()
        );
        registry.registerMessageFactory(
            BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT,
            (caption, sender) -> Messages.COMMAND_ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT.message()
        );
    }

    private void registerParsers() {
        this.commandManager.parserRegistry().registerParserSupplier(
            TypeToken.get(TradeConfig.class),
            parameters -> new TradeConfigArgument.Parser()
        );
    }

    private void preProcessContext(final CommandPreprocessingContext<CommandSender> context) {
        context.getCommandContext().store(PLUGIN, this.plugin);
    }

    public CommandFlag.Builder<?> getFlag(final String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public void register(final List<Command<CommandSender>> commands) {
        commands.forEach(this.commandManager::command);
    }

    public static void setup(final WanderingTrades plugin) {
        final PaperCommandManager<CommandSender> manager;
        try {
            manager = PaperCommandManager.createNative(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator()
            );
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to initialize command manager", ex);
        }
        new Commands(plugin, manager);
    }
}
