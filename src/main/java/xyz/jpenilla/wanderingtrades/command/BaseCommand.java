package xyz.jpenilla.wanderingtrades.command;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public abstract class BaseCommand {
    protected final WanderingTrades plugin;
    protected final CommandManager commandManager;
    protected final Chat chat;

    protected BaseCommand(final WanderingTrades plugin, final CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
        this.chat = plugin.chat();
    }

    public abstract void register();
}
