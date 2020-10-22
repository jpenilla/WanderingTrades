package xyz.jpenilla.wanderingtrades.command;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.flags.CommandFlag;
import cloud.commandframework.bukkit.BukkitCommandMetaBuilder;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandManager extends PaperCommandManager<CommandSender> {

    @Getter private final MinecraftHelp<CommandSender> help;
    private final Map<String, CommandArgument.Builder<CommandSender, ?>> argumentRegistry = new HashMap<>();
    private final Map<String, CommandFlag.Builder<?>> flagRegistry = new HashMap<>();

    public CommandManager(WanderingTrades wanderingTrades) throws Exception {
        super(
                wanderingTrades,
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(),
                Function.identity(),
                Function.identity()
        );

        help = new MinecraftHelp<>("/wanderingtrades help", wanderingTrades.getAudience()::sender, this);
        help.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(0x00a3ff),
                NamedTextColor.WHITE,
                TextColor.color(0x284fff),
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY
        ));
        help.setMessageProvider((sender, key) -> wanderingTrades.getLang().get(Lang.valueOf(String.format("HELP_%s", key).toUpperCase())));

        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .apply(this, wanderingTrades.getAudience()::sender);

        /* Register Brigadier */
        try {
            this.registerBrigadier();
            wanderingTrades.getLogger().info("Successfully registered Mojang Brigadier support for commands.");
        } catch (Exception ignored) {
        }

        /* Register Asynchronous Completion Listener */
        try {
            this.registerAsynchronousCompletions();
            wanderingTrades.getLogger().info("Successfully registered asynchronous command completion listener.");
        } catch (Exception ignored) {
        }

        /* Register Commands */
        ImmutableList.of(
                new CommandWanderingTrades(wanderingTrades, this),
                new CommandSummon(wanderingTrades, this),
                new CommandConfig(wanderingTrades, this)
        ).forEach(WTCommand::registerCommands);
    }

    public CommandArgument.Builder<CommandSender, ?> getArgument(String name) {
        return this.argumentRegistry.get(name);
    }

    public void registerArgument(String name, CommandArgument.Builder<CommandSender, ?> argumentBuilder) {
        this.argumentRegistry.put(name, argumentBuilder);
    }

    public CommandFlag.Builder<?> getFlag(String name) {
        return this.flagRegistry.get(name);
    }

    public void registerFlag(String name, CommandFlag.Builder<?> flagBuilder) {
        this.flagRegistry.put(name, flagBuilder);
    }

    public static SimpleCommandMeta metaWithDescription(final String description) {
        return BukkitCommandMetaBuilder.builder().withDescription(description).build();
    }
}
