package xyz.jpenilla.wanderingtrades.command.argument;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@NullMarked
public final class TradeConfigParser implements ArgumentParser<Source, TradeConfig>, BlockingSuggestionProvider.Strings<CommandSender> {

    public static ParserDescriptor<Source, TradeConfig> tradeConfigParser() {
        return ParserDescriptor.of(new TradeConfigParser(), TradeConfig.class);
    }

    @Override
    public ArgumentParseResult<TradeConfig> parse(
        final CommandContext<Source> commandContext,
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
