package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.config.Lang;

@DefaultQualifier(NonNull.class)
public final class CommandWanderingTrades extends BaseCommand {
    public CommandWanderingTrades(final WanderingTrades plugin, final CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.commandManager.commandBuilder("wt");

        /* About Command */
        final Command<CommandSender> about = wt
            .meta(CommandMeta.DESCRIPTION, this.plugin.langConfig().get(Lang.COMMAND_WT_ABOUT))
            .literal("about")
            .handler(this::executeAbout)
            .build();

        /* Reload Command */
        final Command<CommandSender> reload = wt
            .meta(CommandMeta.DESCRIPTION, this.plugin.langConfig().get(Lang.COMMAND_WT_RELOAD))
            .literal("reload")
            .permission("wanderingtrades.reload")
            .handler(this::executeReload)
            .build();

        this.commandManager.register(List.of(about, reload));
    }

    private void executeAbout(final CommandContext<CommandSender> context) {
        Stream.of(
            "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
            "<hover:show_text:'<rainbow>click me!'><click:open_url:" + this.plugin.getDescription().getWebsite() + ">" + this.plugin.getName() + " <gradient:blue:green>" + this.plugin.getDescription().getVersion(),
            "<gray>By <gradient:gold:yellow>jmp",
            "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
        ).map(Chat::getCenteredMessage).forEach(string -> this.chat.send(context.getSender(), string));
    }

    private void executeReload(final CommandContext<CommandSender> context) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            this.chat.sendParsed(context.getSender(), Chat.getCenteredMessage(this.plugin.langConfig().get(Lang.COMMAND_RELOAD)));
            this.plugin.reload();
            this.chat.sendParsed(context.getSender(), Chat.getCenteredMessage(this.plugin.langConfig().get(Lang.COMMAND_RELOAD_DONE)));
        });
    }
}
