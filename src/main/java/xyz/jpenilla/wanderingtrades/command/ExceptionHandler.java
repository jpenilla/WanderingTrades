package xyz.jpenilla.wanderingtrades.command;

import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Constants;

import static org.incendo.cloud.exception.handling.ExceptionHandler.unwrappingHandler;

@NullMarked
final class ExceptionHandler {

    private final WanderingTrades plugin;
    private final CommandManager<CommandSender> commandManager;

    ExceptionHandler(
        final WanderingTrades plugin,
        final CommandManager<CommandSender> commandManager
    ) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    void register() {
        MinecraftExceptionHandler.<CommandSender>create(AudienceProvider.nativeAudience())
            .handler(NoPermissionException.class, (formatter, ctx) -> Component.translatable("commands.help.failed", NamedTextColor.RED))
            .defaultInvalidSyntaxHandler()
            .defaultInvalidSenderHandler()
            .defaultArgumentParsingHandler()
            .defaultCommandExecutionHandler(ctx -> this.plugin.getLogger().log(Level.WARNING, "Unexpected exception during command execution", ctx.exception().getCause()))
            .decorator(msg -> Component.textOfChildren(Constants.PREFIX_COMPONENT, msg))
            .registerTo(this.commandManager);

        this.commandManager.exceptionController()
            .registerHandler(CommandExecutionException.class, unwrappingHandler(NoPermissionException.class))
            .registerHandler(CommandExecutionException.class, unwrappingHandler(InvalidSyntaxException.class))
            .registerHandler(CommandExecutionException.class, unwrappingHandler(InvalidCommandSenderException.class))
            .registerHandler(CommandExecutionException.class, unwrappingHandler(ArgumentParseException.class));
    }
}
