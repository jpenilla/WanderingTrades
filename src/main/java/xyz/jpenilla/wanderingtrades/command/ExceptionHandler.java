package xyz.jpenilla.wanderingtrades.command;

import java.util.Objects;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.exception.handling.ExceptionContext;
import org.incendo.cloud.exception.handling.ExceptionContextFactory;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.util.TypeUtils;
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
        MinecraftExceptionHandler.create(this.plugin.audiences()::sender)
            .handler(NoPermissionException.class, ExceptionHandler::noPermission)
            .handler(InvalidSyntaxException.class, this::invalidSyntax)
            .handler(InvalidCommandSenderException.class, this::invalidSender)
            .handler(ArgumentParseException.class, this::argumentParsing)
            .handler(CommandExecutionException.class, this::commandExecution)
            .decorator(ExceptionHandler::decorate)
            .registerTo(this.commandManager);
    }

    private @Nullable ComponentLike commandExecution(final ComponentCaptionFormatter<CommandSender> formatter, final ExceptionContext<CommandSender, CommandExecutionException> e) {
        final Throwable cause = e.exception().getCause();

        final ExceptionContextFactory<CommandSender> factory = new ExceptionContextFactory<>(this.commandManager.exceptionController());
        if (cause instanceof NoPermissionException noPermissionException) {
            return noPermission(formatter, factory.createContext(e.context(), noPermissionException));
        } else if (cause instanceof InvalidSyntaxException invalidSyntaxException) {
            return this.invalidSyntax(formatter, factory.createContext(e.context(), invalidSyntaxException));
        } else if (cause instanceof InvalidCommandSenderException invalidCommandSenderException) {
            return this.invalidSender(formatter, factory.createContext(e.context(), invalidCommandSenderException));
        } else if (cause instanceof ArgumentParseException argumentParseException) {
            return this.argumentParsing(formatter, factory.createContext(e.context(), argumentParseException));
        }

        return MinecraftExceptionHandler.<CommandSender>createDefaultCommandExecutionHandler().message(formatter, e);
    }

    private Component invalidSyntax(final ComponentCaptionFormatter<CommandSender> formatter, final ExceptionContext<CommandSender, InvalidSyntaxException> e) {
        final InvalidSyntaxException exception = e.exception();
        final Component correctSyntaxMessage = Component.text(
            String.format("/%s", exception.correctSyntax()),
            NamedTextColor.GRAY
        ).replaceText(config -> {
            config.match(SYNTAX_HIGHLIGHT_PATTERN);
            config.replacement(builder -> builder.color(NamedTextColor.WHITE));
        });
        return Messages.COMMAND_INVALID_SYNTAX.withPlaceholders(
            Components.placeholder("syntax", correctSyntaxMessage)
        );
    }

    private Component invalidSender(final ComponentCaptionFormatter<CommandSender> formatter, final ExceptionContext<CommandSender, InvalidCommandSenderException> e) {
        final InvalidCommandSenderException exception = e.exception();
        return Messages.COMMAND_INVALID_SENDER.withPlaceholders(
            Components.placeholder("type", TypeUtils.simpleName(exception.requiredSender()))
        );
    }

    private Component argumentParsing(final ComponentCaptionFormatter<CommandSender> formatter, final ExceptionContext<CommandSender, ArgumentParseException> ex) {
        final Component causeMessage = Objects.requireNonNull(ComponentMessageThrowable.getOrConvertMessage(ex.exception().getCause()))
            .colorIfAbsent(NamedTextColor.GRAY);
        return Messages.COMMAND_INVALID_ARGUMENT.withPlaceholders(
            Components.placeholder("error", causeMessage)
        );
    }

    private static Component noPermission(final ComponentCaptionFormatter<CommandSender> formatter, final ExceptionContext<CommandSender, NoPermissionException> e) {
        return Component.translatable("commands.help.failed", NamedTextColor.RED);
    }

    private static Component decorate(final ComponentLike component) {
        return Component.textOfChildren(Constants.PREFIX_COMPONENT, component);
    }
}
