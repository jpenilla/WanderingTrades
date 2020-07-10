package xyz.jpenilla.wanderingtrades.command;

import co.aikar.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.util.*;

public class HelpFormatter extends CommandHelpFormatter {
    private final WanderingTrades wanderingTrades;
    private boolean loaded = false;

    public HelpFormatter(WanderingTrades plugin, PaperCommandManager manager) {
        super(manager);
        this.wanderingTrades = plugin;
        Bukkit.getScheduler().runTaskLaterAsynchronously(wanderingTrades, () -> loaded = true, 10L);
    }

    private String getColor() {
        return "#00ADFF";
    }

    public void printDetailedHelpHeader(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        String var10002 = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <white>Detailed Help ]</white>=====";
        String[] var10004 = this.getHeaderFooterFormatReplacements(help);
        var10002 = TextUtil.replacePlaceholders(var10002, this.arrayToMap(var10004), false);
        this.send(issuer, var10002);
    }

    public void printSearchHeader(CommandHelp help, CommandIssuer issuer) {
        String var10002 = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <italic>{search}</italic> <white>Search Results ]</white>=====";
        String[] var10004 = this.getHeaderFooterFormatReplacements(help);
        var10002 = TextUtil.replacePlaceholders(var10002, this.arrayToMap(var10004), false);
        this.send(issuer, var10002);
    }

    public void printHelpHeader(CommandHelp help, CommandIssuer issuer) {
        String var10002 = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <white>Help ]</white>=====";
        String[] var10004 = this.getHeaderFooterFormatReplacements(help);
        var10002 = TextUtil.replacePlaceholders(var10002, this.arrayToMap(var10004), false);
        this.send(issuer, var10002);
    }

    private String getFooter(CommandHelp help) {
        StringBuilder builder = new StringBuilder();
        if (help.getPage() > 1) {
            builder.append("<color:").append(getColor()).append("><bold><click:run_command:/wanderingtrades help ").append(this.listToSpaceSeparatedString(help.getSearch())).append(' ').append(help.getPage() - 1).append("><hover:show_text:'<italic>Click for previous page'><<</bold></click></hover> </color:").append(getColor()).append('>');
        }

        builder.append("Page <color:").append(getColor()).append(">{page}</color:").append(getColor()).append("> of <color:").append(getColor()).append(">{totalpages}</color:").append(getColor()).append("> (<color:").append(getColor()).append(">{results} results<white>)</white> ============");
        if (help.getPage() < help.getTotalPages() && !help.isOnlyPage()) {
            builder.append("<white><bold><click:run_command:/wanderingtrades help ").append(this.listToSpaceSeparatedString(help.getSearch())).append(' ').append(help.getPage() + 1).append("><hover:show_text:'<italic>Click for next page'> >></bold></click></hover></white>");
        }

        return builder.toString();
    }

    public void printSearchFooter(CommandHelp help, CommandIssuer issuer) {
        String[] var10000 = new String[2];
        String var10003 = this.getFooter(help);
        String[] var10005 = this.getHeaderFooterFormatReplacements(help);
        var10000[0] = TextUtil.replacePlaceholders(var10003, this.arrayToMap(var10005), false);
        var10000[1] = "";
        this.send(issuer, Arrays.asList(var10000));
    }

    public void printHelpFooter(CommandHelp help, CommandIssuer issuer) {
        this.printSearchFooter(help, issuer);
    }

    public void printDetailedHelpFooter(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
    }

    public void printDetailedHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        String var10002 = " <white>- <click:suggest_command:/{command} ><hover:show_text:'<italic>Click to suggest'>/</white><color:" + getColor() + ">{command}</color:" + getColor() + "> <gray>{parameters}</gray></hover></click> <color:" + getColor() + ">{separator}</color:" + getColor() + "> {description}";
        String[] var10004 = this.getEntryFormatReplacements(help, entry);
        var10002 = TextUtil.replacePlaceholders(var10002, this.arrayToMap(var10004), false);
        this.send(issuer, var10002);
    }

    public void printHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        this.printDetailedHelpCommand(help, issuer, entry);
    }

    public void printSearchEntry(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        this.printDetailedHelpCommand(help, issuer, entry);
    }

    private void send(CommandIssuer $this$send, String message) {
        if ($this$send instanceof BukkitCommandIssuer && this.loaded && ($this$send.isPlayer() || ((BukkitCommandIssuer) $this$send).getIssuer() instanceof ConsoleCommandSender)) {
            wanderingTrades.getChat().sendPlaceholders(((BukkitCommandIssuer) $this$send).getIssuer(), message);
        }
    }

    private void send(CommandIssuer $this$send, List<String> messages) {
        for (String m : messages) {
            this.send($this$send, m);
        }
    }

    private String listToSpaceSeparatedString(List<String> strings) {
        StringBuilder b = new StringBuilder();
        if (strings != null) {
            int index = 0;
            for (Iterator<String> var5 = ((Iterable<String>) strings).iterator(); var5.hasNext(); ++index) {
                String s = var5.next();
                b.append(s);
                if (index != strings.size() - 1) {
                    b.append(" ");
                }
            }
        }
        return b.toString();
    }

    private Map<String, String> arrayToMap(String[] list) {
        HashMap<String, String> map = new HashMap<>();
        String entry = "";
        boolean first = true;

        for (String r : list) {
            if (first) {
                entry = r;
                first = false;
            } else {
                map.put(entry, r);
                first = true;
            }
        }

        return map;
    }
}
