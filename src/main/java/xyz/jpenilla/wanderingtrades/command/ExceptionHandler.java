package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import java.util.Objects;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.util.Components;
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
            .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION, this::commandExecution)
            .withDecorator(ExceptionHandler::decorate)
            .apply(this.commandManager, this.plugin.audiences()::sender);
    }

    private Component commandExecution(final CommandSender commandSender, final Exception ex) {
        final Throwable cause = ex.getCause();

        if (cause instanceof NoPermissionException noPermissionException) {
            return noPermission(noPermissionException);
        } else if (cause instanceof InvalidSyntaxException invalidSyntaxException) {
            return this.invalidSyntax(invalidSyntaxException);
        } else if (cause instanceof InvalidCommandSenderException invalidCommandSenderException) {
            return this.invalidSender(invalidCommandSenderException);
        } else if (cause instanceof ArgumentParseException argumentParseException) {
            return this.argumentParsing(argumentParseException);
        }

        return MinecraftExceptionHandler.DEFAULT_COMMAND_EXECUTION_FUNCTION.apply(ex);
    }

    private Component invalidSyntax(final Exception ex) {
        final InvalidSyntaxException exception = (InvalidSyntaxException) ex;
        final Component correctSyntaxMessage = Component.text(
            String.format("/%s", exception.getCorrectSyntax()),
            NamedTextColor.GRAY
        ).replaceText(config -> {
            config.match(SYNTAX_HIGHLIGHT_PATTERN);
            config.replacement(builder -> builder.color(NamedTextColor.WHITE));
        });
        return Messages.COMMAND_INVALID_SYNTAX.withPlaceholders(
            Components.placeholder("syntax", correctSyntaxMessage)
        );
    }

    private Component invalidSender(final Exception ex) {
        final InvalidCommandSenderException exception = (InvalidCommandSenderException) ex;
        return Messages.COMMAND_INVALID_SENDER.withPlaceholders(
            Components.placeholder("type", exception.getRequiredSender().getSimpleName())
        );
    }

    private Component argumentParsing(final Exception ex) {
        final Component causeMessage = Objects.requireNonNull(ComponentMessageThrowable.getOrConvertMessage(ex.getCause()))
            .colorIfAbsent(NamedTextColor.GRAY);
        return Messages.COMMAND_INVALID_ARGUMENT.withPlaceholders(
            Components.placeholder("error", causeMessage)
        );
    }

    private static Component noPermission(final Exception e) {
        return Component.translatable("commands.help.failed", NamedTextColor.RED);
    }

    private static Component decorate(final ComponentLike component) {
        return Component.textOfChildren(Constants.PREFIX_COMPONENT, component);
    }
}
