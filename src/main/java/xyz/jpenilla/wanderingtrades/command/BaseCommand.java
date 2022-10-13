package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public abstract class BaseCommand {
    protected final WanderingTrades plugin;
    protected final Commands commands;
    protected final PaperCommandManager<CommandSender> commandManager;
    protected final Chat chat;

    protected BaseCommand(final WanderingTrades plugin, final Commands commands) {
        this.plugin = plugin;
        this.commands = commands;
        this.commandManager = commands.commandManager();
        this.chat = plugin.chat();
    }

    public abstract void register();
}
