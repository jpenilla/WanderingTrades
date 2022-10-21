package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;

@DefaultQualifier(NonNull.class)
public final class ReloadCommand extends BaseCommand {
    public ReloadCommand(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
    }

    @Override
    public void register() {
        final Command<CommandSender> reload = this.commandManager.commandBuilder("wt")
            .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Messages.COMMAND_RELOAD_DESCRIPTION.asComponent())
            .literal("reload")
            .permission("wanderingtrades.reload")
            .handler(this::execute)
            .build();

        this.commandManager.command(reload);
    }

    private void execute(final CommandContext<CommandSender> context) {
        this.chat.send(context.getSender(), Chat.getCenteredMessage(Messages.COMMAND_RELOAD.message()));
        this.plugin.reload();
        this.chat.send(context.getSender(), Chat.getCenteredMessage(Messages.COMMAND_RELOAD_DONE.message()));
    }
}
