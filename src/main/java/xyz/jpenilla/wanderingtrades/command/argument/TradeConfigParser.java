package xyz.jpenilla.wanderingtrades.command.argument;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeConfigParser implements ArgumentParser<CommandSender, TradeConfig>, BlockingSuggestionProvider.Strings<CommandSender> {

    public static ParserDescriptor<CommandSender, TradeConfig> tradeConfigParser() {
        return ParserDescriptor.of(new TradeConfigParser(), TradeConfig.class);
    }

    @Override
    public ArgumentParseResult<TradeConfig> parse(
        final CommandContext<CommandSender> commandContext,
        final CommandInput input
    ) {
        final WanderingTrades plugin = commandContext.get(Commands.PLUGIN);
        final @Nullable TradeConfig tradeConfig = plugin.configManager().tradeConfigs().get(input.readString());
        if (tradeConfig != null) {
            return ArgumentParseResult.success(tradeConfig);
        }
        return ArgumentParseResult.failure(new IllegalArgumentException(Messages.COMMAND_PARSE_EXCEPTION_NO_TRADE_CONFIG.message()));
    }

    @Override
    public Iterable<String> stringSuggestions(
        final CommandContext<CommandSender> commandContext,
        final CommandInput input
    ) {
        return List.copyOf(commandContext.get(Commands.PLUGIN).configManager().tradeConfigs().keySet());
    }
}
