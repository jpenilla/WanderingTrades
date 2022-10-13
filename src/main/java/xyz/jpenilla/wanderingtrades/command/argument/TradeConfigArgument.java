package xyz.jpenilla.wanderingtrades.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeConfigArgument extends CommandArgument<CommandSender, TradeConfig> {
    private TradeConfigArgument(
        final boolean required,
        final String name,
        final String defaultValue,
        final @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
        final ArgumentDescription defaultDescription
    ) {
        super(required, name, new Parser(), defaultValue, TradeConfig.class, suggestionsProvider, defaultDescription);
    }

    public static TradeConfigArgument of(final String name) {
        return builder(name).build();
    }

    public static TradeConfigArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static Builder builder(final String name) {
        return new Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, TradeConfig> {
        @Override
        public ArgumentParseResult<TradeConfig> parse(
            final CommandContext<CommandSender> commandContext,
            final Queue<String> inputQueue
        ) {
            final WanderingTrades plugin = commandContext.get(Commands.PLUGIN);
            final @Nullable TradeConfig tradeConfig = plugin.configManager().tradeConfigs().get(inputQueue.peek());
            if (tradeConfig != null) {
                inputQueue.remove();
                return ArgumentParseResult.success(tradeConfig);
            }
            return ArgumentParseResult.failure(new IllegalArgumentException(plugin.langConfig().get(Lang.COMMAND_SUMMON_NO_CONFIG)));
        }

        @Override
        public List<String> suggestions(
            final CommandContext<CommandSender> commandContext,
            final String input
        ) {
            return List.copyOf(commandContext.get(Commands.PLUGIN).configManager().tradeConfigs().keySet());
        }
    }

    public static final class Builder extends TypedBuilder<CommandSender, TradeConfig, TradeConfigArgument.Builder> {
        private Builder(final String name) {
            super(TradeConfig.class, name);
        }

        @Override
        public TradeConfigArgument build() {
            return new TradeConfigArgument(
                this.isRequired(),
                this.getName(),
                this.getDefaultValue(),
                this.getSuggestionsProvider(),
                this.getDefaultDescription()
            );
        }
    }
}
