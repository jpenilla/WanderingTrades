package fun.ccmc.wanderingtrades.command;

import fun.ccmc.wanderingtrades.WanderingTrades;

import java.util.ArrayList;
import java.util.Arrays;

public class TabCompletions {
    private WanderingTrades plugin;

    public TabCompletions(WanderingTrades instance) {
        plugin = instance;
    }

    public void register() {
        plugin.getCommandManager().getCommandCompletions().registerCompletion("wtConfigs", c -> {
            ArrayList<String> completions = new ArrayList<>();
            Arrays.stream(plugin.getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> {
                completions.add((String) completion);
            });
            return completions;
        });

        plugin.getCommandManager().getCommandCompletions().registerCompletion("boolean", c -> {
            ArrayList<String> completions = new ArrayList<>();
            completions.add("true");
            completions.add("false");
            return completions;
        });
    }
}
