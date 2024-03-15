package xyz.jpenilla.wanderingtrades.command.commands;

import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.Messages;

@DefaultQualifier(NonNull.class)
public final class AboutCommand extends BaseCommand {
    public AboutCommand(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
    }

    @Override
    public void register() {
        final Command<CommandSender> about = this.commandManager.commandBuilder("wt")
            .commandDescription(Messages.COMMAND_ABOUT_DESCRIPTION.asDescription())
            .literal("about")
            .handler(this::execute)
            .build();

        this.commandManager.command(about);
    }

    private void execute(final CommandContext<CommandSender> context) {
        Stream.of(
            "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
            "<hover:show_text:'<rainbow>click me!'><click:open_url:" + this.plugin.getDescription().getWebsite() + ">" + this.plugin.getName() + " <blue>" + this.plugin.getDescription().getVersion(),
            "<gray>By <blue>jmp",
            "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
        ).map(Chat::getCenteredMessage).forEach(string -> this.chat.send(context.sender(), string));
    }
}
