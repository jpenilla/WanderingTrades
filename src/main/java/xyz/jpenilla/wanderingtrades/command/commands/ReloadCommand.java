package xyz.jpenilla.wanderingtrades.command.commands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;

@NullMarked
public final class ReloadCommand extends BaseCommand {
    public ReloadCommand(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
    }

    @Override
    public void register() {
        final Command<CommandSender> reload = this.commandManager.commandBuilder("wt")
            .commandDescription(Messages.COMMAND_RELOAD_DESCRIPTION.asDescription())
            .literal("reload")
            .permission("wanderingtrades.reload")
            .handler(this::execute)
            .build();

        this.commandManager.command(reload);
    }

    private void execute(final CommandContext<CommandSender> context) {
        context.sender().sendMessage(Messages.COMMAND_RELOAD);
        this.plugin.reload();
        context.sender().sendMessage(Messages.COMMAND_RELOAD_DONE);
    }
}
