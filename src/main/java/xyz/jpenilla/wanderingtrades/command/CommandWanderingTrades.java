package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.arguments.standard.StringArgument;
import com.google.common.collect.ImmutableList;
import xyz.jpenilla.jmplib.Chat;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

import static xyz.jpenilla.wanderingtrades.command.CommandManager.metaWithDescription;

public class CommandWanderingTrades implements WTCommand {

    private final WanderingTrades wanderingTrades;
    private final CommandManager mgr;
    private final Chat chat;

    public CommandWanderingTrades(WanderingTrades wanderingTrades, CommandManager mgr) {
        this.wanderingTrades = wanderingTrades;
        this.mgr = mgr;
        this.chat = wanderingTrades.getChat();
    }

    @Override
    public void registerCommands() {
        /* About Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_ABOUT)))
                        .literal("about")
                        .handler(context -> ImmutableList.of(
                                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------",
                                "<hover:show_text:'<rainbow>click me!'><click:open_url:" + wanderingTrades.getDescription().getWebsite() + ">" + wanderingTrades.getName() + " <gradient:blue:green>" + wanderingTrades.getDescription().getVersion(),
                                "<gray>By <gradient:gold:yellow>jmp",
                                "<strikethrough><gradient:white:blue>-------------</gradient><gradient:blue:white>-------------"
                        ).forEach(string -> chat.send(context.getSender(), chat.getCenteredMessage(string))))
        );

        /* Reload Command */
        mgr.command(
                mgr.commandBuilder("wt", metaWithDescription(wanderingTrades.getLang().get(Lang.COMMAND_WT_RELOAD)))
                        .literal("reload")
                        .permission("wanderingtrades.reload")
                        .handler(c -> mgr.taskRecipe().begin(c).synchronous(context -> {
                            chat.sendParsed(context.getSender(), chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD)));
                            wanderingTrades.getCfg().load();
                            wanderingTrades.getLang().load();
                            wanderingTrades.getListeners().reload();
                            wanderingTrades.getStoredPlayers().load();
                            chat.sendParsed(context.getSender(), chat.getCenteredMessage(wanderingTrades.getLang().get(Lang.COMMAND_RELOAD_DONE)));
                        }).execute())
        );
    }
}
