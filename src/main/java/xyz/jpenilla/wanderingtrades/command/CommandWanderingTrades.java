package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.Command;
import cloud.commandframework.meta.CommandMeta;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

public class CommandWanderingTrades implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandWanderingTrades(WanderingTrades wanderingTrades, CommandManager mgr) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.chat();
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> wt = mgr.commandBuilder("wt");

        /* About Command */
        final Command<CommandSender> about = wt
                .meta(CommandMeta.DESCRIPTION, wanderingTrades.langConfig().get(Lang.COMMAND_WT_ABOUT))
                .literal("about")
                .handler(context -> Stream.of(
                        "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
                        "<hover:show_text:'<rainbow>click me!'><click:open_url:" + wanderingTrades.getDescription().getWebsite() + ">" + wanderingTrades.getName() + " <gradient:blue:green>" + wanderingTrades.getDescription().getVersion(),
                        "<gray>By <gradient:gold:yellow>jmp",
                        "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
                ).map(Chat::getCenteredMessage).forEach(string -> chat.send(context.getSender(), string))).build();

        /* Reload Command */
        final Command<CommandSender> reload = wt
                .meta(CommandMeta.DESCRIPTION, wanderingTrades.langConfig().get(Lang.COMMAND_WT_RELOAD))
                .literal("reload")
                .permission("wanderingtrades.reload")
                .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                    chat.sendParsed(context.getSender(), Chat.getCenteredMessage(wanderingTrades.langConfig().get(Lang.COMMAND_RELOAD)));
                    wanderingTrades.config().load();
                    wanderingTrades.langConfig().load();
                    wanderingTrades.listeners().reload();
                    this.wanderingTrades.storedPlayers().updateCacheTimerState();
                    chat.sendParsed(context.getSender(), Chat.getCenteredMessage(wanderingTrades.langConfig().get(Lang.COMMAND_RELOAD_DONE)));
                }).execute()).build();

        mgr.register(ImmutableList.of(about, reload));
    }
}
