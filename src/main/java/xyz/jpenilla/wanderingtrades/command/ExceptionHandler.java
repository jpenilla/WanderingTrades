package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
final class ExceptionHandler {
    private static final Pattern SYNTAX_HIGHLIGHT_PATTERN = Pattern.compile("[^\\s\\w\\-]");

    private final WanderingTrades plugin;
    private final CommandManager<CommandSender> commandManager;

    ExceptionHandler(final WanderingTrades plugin, final CommandManager<CommandSender> commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    void register() {
        new MinecraftExceptionHandler<CommandSender>()
            .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, ExceptionHandler::noPermission)
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, this::invalidSyntax)
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, this::invalidSender)
            .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, this::argumentParsing)
            .withCommandExecutionHandler()
            .withDecorator(ExceptionHandler::decorate)
            .apply(this.commandManager, this.plugin.audiences()::sender);
    }

    private Component invalidSyntax(final Exception ex) {
        final InvalidSyntaxException exception = (InvalidSyntaxException) ex;
        final Component invalidSyntaxMessage = Component.text(this.plugin.langConfig().get(Lang.COMMAND_INVALID_SYNTAX), NamedTextColor.RED);
        final Component correctSyntaxMessage = Component.text(
            String.format("/%s", exception.getCorrectSyntax()),
            NamedTextColor.GRAY
        ).replaceText(config -> {
            config.match(SYNTAX_HIGHLIGHT_PATTERN);
            config.replacement(builder -> builder.color(NamedTextColor.WHITE));
        });

        return Component.textOfChildren(invalidSyntaxMessage, correctSyntaxMessage);
    }

    private Component invalidSender(final Exception ex) {
        final InvalidCommandSenderException exception = (InvalidCommandSenderException) ex;
        final Component invalidSenderMessage = Component.text(
            this.plugin.langConfig().get(Lang.COMMAND_INVALID_SENDER),
            NamedTextColor.RED
        );
        final Component correctSenderType = Component.text(
            exception.getRequiredSender().getSimpleName(),
            NamedTextColor.GRAY
        );
        return invalidSenderMessage.replaceText(config -> {
            config.matchLiteral("{type}");
            config.replacement(match -> correctSenderType);
        });
    }

    private Component argumentParsing(final Exception ex) {
        final Component invalidArgumentMessage = Component.text(this.plugin.langConfig().get(Lang.COMMAND_INVALID_ARGUMENT), NamedTextColor.RED);
        final Component causeMessage = Component.text(ex.getCause().getMessage(), NamedTextColor.GRAY);
        return Component.textOfChildren(invalidArgumentMessage, causeMessage);
    }

    private static Component noPermission(final Exception e) {
        return Component.translatable("commands.help.failed", NamedTextColor.RED);
    }

    private static Component decorate(final Component component) {
        return Component.textOfChildren(Constants.PREFIX_COMPONENT, component);
    }
}
