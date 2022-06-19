package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Command;
import cloud.commandframework.meta.CommandMeta;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

public final class CommandWanderingTrades implements WTCommand {
    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandWanderingTrades(final WanderingTrades wanderingTrades, final CommandManager mgr) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.chat();
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = this.mgr.commandBuilder("wt");

        /* About Command */
        final Command<CommandSender> about = wt
            .meta(CommandMeta.DESCRIPTION, this.wanderingTrades.langConfig().get(Lang.COMMAND_WT_ABOUT))
            .literal("about")
            .handler(context -> Stream.of(
                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
                "<hover:show_text:'<rainbow>click me!'><click:open_url:" + this.wanderingTrades.getDescription().getWebsite() + ">" + this.wanderingTrades.getName() + " <gradient:blue:green>" + this.wanderingTrades.getDescription().getVersion(),
                "<gray>By <gradient:gold:yellow>jmp",
                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
            ).map(Chat::getCenteredMessage).forEach(string -> this.chat.send(context.getSender(), string))).build();

        /* Reload Command */
        final Command<CommandSender> reload = wt
            .meta(CommandMeta.DESCRIPTION, this.wanderingTrades.langConfig().get(Lang.COMMAND_WT_RELOAD))
            .literal("reload")
            .permission("wanderingtrades.reload")
            .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                this.chat.sendParsed(context.getSender(), Chat.getCenteredMessage(this.wanderingTrades.langConfig().get(Lang.COMMAND_RELOAD)));
                this.wanderingTrades.config().load();
                this.wanderingTrades.langConfig().load();
                this.wanderingTrades.listeners().reload();
                this.wanderingTrades.storedPlayers().updateCacheTimerState();
                this.chat.sendParsed(context.getSender(), Chat.getCenteredMessage(this.wanderingTrades.langConfig().get(Lang.COMMAND_RELOAD_DONE)));
            }).execute()).build();

        this.mgr.register(ImmutableList.of(about, reload));
    }
}
