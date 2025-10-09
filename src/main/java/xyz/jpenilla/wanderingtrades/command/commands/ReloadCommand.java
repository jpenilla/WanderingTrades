package xyz.jpenilla.wanderingtrades.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.Source;
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
        final Command<Source> reload = this.commandManager.commandBuilder("wt")
            .commandDescription(Messages.COMMAND_RELOAD_DESCRIPTION.asDescription())
            .literal("reload")
            .permission("wanderingtrades.reload")
            .handler(this::execute)
            .build();

        this.commandManager.command(reload);
    }

    private void execute(final CommandContext<Source> context) {
        context.sender().source().sendMessage(Messages.COMMAND_RELOAD);
        this.plugin.reload();
        context.sender().source().sendMessage(Messages.COMMAND_RELOAD_DONE);
    }
}
