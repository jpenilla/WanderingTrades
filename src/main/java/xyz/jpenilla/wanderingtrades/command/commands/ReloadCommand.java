package xyz.jpenilla.wanderingtrades.command.commands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.ConfigManager;
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

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final ConfigManager.Snapshot snapshot = this.plugin.configManager().buildSnapshot();
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    this.plugin.configManager().applySnapshot(snapshot);
                    context.sender().sendMessage(Messages.COMMAND_RELOAD_DONE);
                });
            } catch (final Exception ex) {
                this.plugin.getServer().getScheduler().runTask(this.plugin, () ->
                    context.sender().sendMessage("Reload failed: " + ex.getMessage())
                );
            }
        });
    }
}
