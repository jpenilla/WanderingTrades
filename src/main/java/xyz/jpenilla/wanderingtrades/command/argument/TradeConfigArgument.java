package xyz.jpenilla.wanderingtrades.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

public final class TradeConfigArgument extends CommandArgument<CommandSender, TradeConfig> {
    private TradeConfigArgument(
            final @NonNull WanderingTrades plugin,
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new Parser(plugin), defaultValue, TradeConfig.class, suggestionsProvider, defaultDescription);
    }

    public static @NonNull TradeConfigArgument of(final @NonNull WanderingTrades plugin, final @NonNull String name) {
        return builder(plugin, name).build();
    }

    public static @NonNull TradeConfigArgument optional(final @NonNull WanderingTrades plugin, final @NonNull String name) {
        return builder(plugin, name).asOptional().build();
    }

    public static TradeConfigArgument.@NonNull Builder builder(final @NonNull WanderingTrades plugin, final @NonNull String name) {
        return new Builder(plugin, name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, TradeConfig> {
        private final WanderingTrades plugin;

        public Parser(final @NonNull WanderingTrades plugin) {
            this.plugin = plugin;
        }

        @Override
        public @NonNull ArgumentParseResult<@NonNull TradeConfig> parse(
                final @NonNull CommandContext<@NonNull CommandSender> commandContext,
                final @NonNull Queue<@NonNull String> inputQueue
        ) {
            final TradeConfig tradeConfig = this.plugin.config().tradeConfigs().getOrDefault(inputQueue.peek(), null);
            if (tradeConfig != null) {
                inputQueue.remove();
                return ArgumentParseResult.success(tradeConfig);
            }
            return ArgumentParseResult.failure(new IllegalArgumentException(this.plugin.langConfig().get(Lang.COMMAND_SUMMON_NO_CONFIG)));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
                final @NonNull CommandContext<@NonNull CommandSender> commandContext,
                final @NonNull String input
        ) {
            return ImmutableList.copyOf(this.plugin.config().tradeConfigs().keySet());
        }
    }

    public static final class Builder extends TypedBuilder<CommandSender, TradeConfig, TradeConfigArgument.Builder> {
        private final WanderingTrades plugin;

        private Builder(final @NonNull WanderingTrades plugin, final @NonNull String name) {
            super(TradeConfig.class, name);
            this.plugin = plugin;
        }

        @Override
        public @NonNull TradeConfigArgument build() {
            return new TradeConfigArgument(
                    this.plugin,
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }
}
