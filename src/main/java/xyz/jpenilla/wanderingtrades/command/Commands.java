package xyz.jpenilla.wanderingtrades.command;

import io.leangen.geantyref.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.translations.LocaleExtractor;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigParser;
import xyz.jpenilla.wanderingtrades.command.commands.AboutCommand;
import xyz.jpenilla.wanderingtrades.command.commands.ConfigCommands;
import xyz.jpenilla.wanderingtrades.command.commands.HelpCommand;
import xyz.jpenilla.wanderingtrades.command.commands.ReloadCommand;
import xyz.jpenilla.wanderingtrades.command.commands.SummonCommands;
import xyz.jpenilla.wanderingtrades.command.commands.TradeCommands;

import static org.incendo.cloud.translations.TranslationBundle.core;
import static org.incendo.cloud.translations.bukkit.BukkitTranslationBundle.bukkit;
import static org.incendo.cloud.translations.minecraft.extras.AudienceLocaleExtractor.audienceLocaleExtractor;
import static org.incendo.cloud.translations.minecraft.extras.MinecraftExtrasTranslationBundle.minecraftExtras;

@NullMarked
public final class Commands {
    public static final CloudKey<WanderingTrades> PLUGIN = CloudKey.of("wt:plugin", TypeToken.get(WanderingTrades.class));

    private final WanderingTrades plugin;
    private final PaperCommandManager<Source> commandManager;
    private final Map<String, CommandFlag.Builder<Source, ?>> flagRegistry = new HashMap<>();

    private Commands(final WanderingTrades plugin, final PaperCommandManager<Source> commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;

        new ExceptionHandler(plugin, commandManager).register();
        this.registerCaptions();
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

    public CommandManager<Source> commandManager() {
        return this.commandManager;
    }

    private void registerCaptions() {
        final LocaleExtractor<Source> extractor = audienceLocaleExtractor(Source::source);
        this.commandManager.captionRegistry()
            .registerProvider(minecraftExtras(extractor))
            .registerProvider(bukkit(extractor))
            .registerProvider(core(extractor));
    }

    private void registerParsers() {
        this.commandManager.parserRegistry().registerParser(TradeConfigParser.tradeConfigParser());
    }

    private void preProcessContext(final CommandPreprocessingContext<Source> context) {
        context.commandContext().store(PLUGIN, this.plugin);
    }

    public CommandFlag.Builder<Source, ?> getFlag(final String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(final String name, final CommandFlag.Builder<Source, ?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public void register(final List<Command<? extends Source>> commands) {
        commands.forEach(this.commandManager::command);
    }

    public static void setup(final WanderingTrades plugin) {
        final PaperCommandManager<Source> manager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(plugin);
        new Commands(plugin, manager);
    }
}
