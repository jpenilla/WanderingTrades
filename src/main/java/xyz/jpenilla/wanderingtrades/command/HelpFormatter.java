package xyz.jpenilla.wanderingtrades.command;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandHelpFormatter;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        String helpHeader = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <white>Detailed Help ]</white>=====";
        helpHeader = TextUtil.replacePlaceholders(helpHeader, arrayToMap(getHeaderFooterFormatReplacements(help)), false);
        send(issuer, helpHeader);
    }

    public void printSearchHeader(CommandHelp help, CommandIssuer issuer) {
        String searchHeader = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <italic>{search}</italic> <white>Search Results ]</white>=====";
        searchHeader = TextUtil.replacePlaceholders(searchHeader, arrayToMap(getHeaderFooterFormatReplacements(help)), false);
        send(issuer, searchHeader);
    }

    public void printHelpHeader(CommandHelp help, CommandIssuer issuer) {
        String helpHeader = "<color:" + getColor() + ">=====<white>[</white> {commandprefix}{command} <white>Help ]</white>=====";
        helpHeader = TextUtil.replacePlaceholders(helpHeader, arrayToMap(getHeaderFooterFormatReplacements(help)), false);
        send(issuer, helpHeader);
    }

    private String getFooter(CommandHelp help) {
        final StringBuilder builder = new StringBuilder();
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
        List<String> footer = new ArrayList<>();
        footer.add(TextUtil.replacePlaceholders(getFooter(help), arrayToMap(getHeaderFooterFormatReplacements(help)), false));
        footer.add("");
        send(issuer, footer);
    }

    public void printHelpFooter(CommandHelp help, CommandIssuer issuer) {
        printSearchFooter(help, issuer);
    }

    public void printDetailedHelpFooter(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
    }

    public void printDetailedHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        String detailedHelp;
        detailedHelp = " <white>- <click:suggest_command:/{command} ><hover:show_text:'<italic>Click to suggest'>/</white><color:" + getColor() + ">{command}</color:" + getColor() + "> <gray>{parameters}</gray></hover></click> <color:" + getColor() + ">{separator}</color:" + getColor() + "> {description}";
        detailedHelp = TextUtil.replacePlaceholders(detailedHelp, arrayToMap(getEntryFormatReplacements(help, entry)), false);
        send(issuer, detailedHelp);
    }

    public void printHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        printDetailedHelpCommand(help, issuer, entry);
    }

    public void printSearchEntry(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        printDetailedHelpCommand(help, issuer, entry);
    }

    private void send(CommandIssuer issuer, String message) {
        if (loaded && (issuer.isPlayer() || issuer.getIssuer() instanceof ConsoleCommandSender)) {
            wanderingTrades.getChat().sendParsed((issuer).getIssuer(), message);
        }
    }

    private void send(CommandIssuer issuer, List<String> messages) {
        for (String m : messages) {
            send(issuer, m);
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
        Map<String, String> map = new HashMap<>();
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
