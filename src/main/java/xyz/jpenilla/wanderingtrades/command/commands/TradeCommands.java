package xyz.jpenilla.wanderingtrades.command.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.parser.flag.CommandFlag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser.multiplePlayerSelectorParser;
import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static xyz.jpenilla.wanderingtrades.command.argument.TradeConfigParser.tradeConfigParser;

@NullMarked
public final class TradeCommands extends BaseCommand {
    public TradeCommands(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
    }

    private CommandFlag<String> tradeSessionFlag() {
        return this.commandManager.flagBuilder("session").withAliases("s").withComponent(stringParser()).build();
    }

    private CommandFlag<Long> sessionLifetimeFlag() {
        return this.commandManager.flagBuilder("lifetime").withAliases("l").withComponent(longParser()).build();
    }

    @Override
    public void register() {
        final Command.Builder<CommandSender> trade = this.commandManager
            .commandBuilder("wt")
            .literal("trade");

        final Command.Builder<CommandSender> config = trade
            .literal("config")
            .required("config", tradeConfigParser())
            .permission("wanderingtrades.tradecommand")
            .handler(this::tradeConfig);
        this.commandManager.command(config);
        this.commandManager.command(config
            .permission("wanderingtrades.tradecommand.others")
            .required("players", multiplePlayerSelectorParser())
            .flag(this.tradeSessionFlag())
            .flag(this.sessionLifetimeFlag()));

        final Command.Builder<CommandSender> natural = trade
            .literal("natural")
            .permission("wanderingtrades.tradenaturalcommand")
            .handler(this::tradeNatural);
        this.commandManager.command(natural);
        this.commandManager.command(natural
            .permission("wanderingtrades.tradenaturalcommand.others")
            .required("players", multiplePlayerSelectorParser())
            .flag(this.tradeSessionFlag())
            .flag(this.sessionLifetimeFlag()));
    }

    private static final long DEFAULT_SESSION_LIFETIME = 20L * 60L * 60L; // 1 hour

    private void tradeConfig(final CommandContext<CommandSender> ctx) {
        final @Nullable String sessionId = ctx.flags().<String>getValue("session").orElse(null);
        final long sessionLifetime = ctx.flags().<Long>getValue("lifetime").orElse(DEFAULT_SESSION_LIFETIME);
        final TradeConfig config = ctx.get("config");
        for (final Player player : players(ctx)) {
            this.tradeWithSession(player, sessionId, sessionLifetime, this.tradeConfig(player, config));
        }
    }

    private void tradeNatural(final CommandContext<CommandSender> ctx) {
        final @Nullable String sessionId = ctx.flags().<String>getValue("session").orElse(null);
        final long sessionLifetime = ctx.flags().<Long>getValue("lifetime").orElse(DEFAULT_SESSION_LIFETIME);
        for (final Player player : players(ctx)) {
            this.tradeWithSession(player, sessionId, sessionLifetime, this.tradeNatural(player));
        }
    }

    private void tradeWithSession(
        final Player player,
        final @Nullable String sessionId,
        final long sessionLifetime,
        final Supplier<CompletableFuture<Merchant>> merchantSupplier
    ) {
        final CompletableFuture<Merchant> future;
        if (sessionId != null) {
            future = this.plugin.sessionManager().getOrCreateSession(sessionId, sessionLifetime, merchantSupplier);
        } else {
            future = merchantSupplier.get();
        }
        future.thenAccept(merchant -> player.openMerchant(merchant, false));
    }

    private Supplier<CompletableFuture<Merchant>> tradeConfig(final Player player, final TradeConfig config) {
        return () -> {
            final CompletableFuture<Merchant> future = new CompletableFuture<>();

            this.plugin.getFoliaLib().getScheduler().runAsync(asyncTask -> {
                final @Nullable List<MerchantRecipe> merchantRecipes = config.tryGetTrades(player);
                if (merchantRecipes == null) {
                    return;
                }

                final Merchant merchant = this.plugin.getServer().createMerchant(miniMessage().deserialize(config.customName()));
                merchant.setRecipes(merchantRecipes);
                future.complete(merchant);
            });

            return future;
        };
    }

    private Supplier<CompletableFuture<Merchant>> tradeNatural(final Player player) {
        return () -> {
            final CompletableFuture<Merchant> future = new CompletableFuture<>();

            final List<MerchantRecipe> recipes = new ArrayList<>();
            final WanderingTrader wanderingTrader = player.getWorld().spawn(player.getLocation(), WanderingTrader.class, trader -> {
                trader.getPersistentDataContainer().set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);
                trader.setInvisible(true);
                trader.setInvulnerable(true);
                trader.setCollidable(false);
                trader.setAI(false);
                trader.setGravity(false);
                recipes.addAll(trader.getRecipes());
            });
            wanderingTrader.remove();
            this.plugin.tradeApplicator().selectTrades(trades -> {
                final Merchant merchant = this.plugin.getServer().createMerchant(Component.translatable("entity.minecraft.wandering_trader"));
                final List<MerchantRecipe> result = new ArrayList<>(trades);
                result.addAll(recipes);
                merchant.setRecipes(result);
                future.complete(merchant);
            });

            return future;
        };
    }

    private static Collection<Player> players(final CommandContext<CommandSender> ctx) {
        @Nullable Collection<Player> players = ctx.<MultiplePlayerSelector>optional("players")
            .map(MultiplePlayerSelector::values)
            .orElse(null);
        if (players == null) {
            if (ctx.sender() instanceof Player player) {
                players = List.of(player);
            } else {
                throw new InvalidCommandSenderException(ctx.sender(), Set.of(Player.class), List.of(), null);
            }
        }
        return players;
    }
}
