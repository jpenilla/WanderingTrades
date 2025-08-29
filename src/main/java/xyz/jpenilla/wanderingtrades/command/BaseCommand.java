package xyz.jpenilla.wanderingtrades.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
public abstract class BaseCommand {
    protected final WanderingTrades plugin;
    protected final Commands commands;
    protected final CommandManager<CommandSender> commandManager;

    protected BaseCommand(final WanderingTrades plugin, final Commands commands) {
        this.plugin = plugin;
        this.commands = commands;
        this.commandManager = commands.commandManager();
    }

    public abstract void register();
}
