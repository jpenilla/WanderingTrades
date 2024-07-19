package xyz.jpenilla.wanderingtrades.command.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.InventoryFactory;
import xyz.jpenilla.wanderingtrades.util.Reflection;

import static org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser.multiplePlayerSelectorParser;
import static xyz.jpenilla.wanderingtrades.command.argument.TradeConfigParser.tradeConfigParser;

@DefaultQualifier(NonNull.class)
public final class TradeCommands extends BaseCommand {
    public TradeCommands(final WanderingTrades plugin, final Commands commands) {
        super(plugin, commands);
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
            .required("players", multiplePlayerSelectorParser()));

        final Command.Builder<CommandSender> natural = trade
            .literal("natural")
            .permission("wanderingtrades.tradenaturalcommand")
            .handler(this::tradeNatural);
        this.commandManager.command(natural);
        this.commandManager.command(natural
            .permission("wanderingtrades.tradenaturalcommand.others")
            .required("players", multiplePlayerSelectorParser()));
    }

    private void tradeConfig(final CommandContext<CommandSender> ctx) {
        final TradeConfig config = ctx.get("config");
        for (final Player player : players(ctx)) {
            this.tradeConfig(player, config);
        }
    }

    private void tradeNatural(final CommandContext<CommandSender> ctx) {
        players(ctx).forEach(this::tradeNatural);
    }

    private void tradeConfig(final Player player, final TradeConfig config) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final @Nullable List<MerchantRecipe> merchantRecipes = config.tryGetTrades(player);
            if (merchantRecipes == null) {
                return;
            }

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                final Merchant merchant = InventoryFactory.createMerchant(player, this.plugin.miniMessage().deserialize(config.customName()));
                merchant.setRecipes(merchantRecipes);
                player.openMerchant(merchant, false);
            });
        });
    }

    private void tradeNatural(final Player player) {
        final List<MerchantRecipe> recipes = new ArrayList<>();
        final WanderingTrader wanderingTrader = Reflection.spawn(player.getLocation(), WanderingTrader.class, trader -> {
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
            final Merchant merchant = InventoryFactory.createMerchant(player, Component.translatable("entity.minecraft.wandering_trader"));
            final List<MerchantRecipe> result = new ArrayList<>(trades);
            result.addAll(recipes);
            merchant.setRecipes(result);
            player.openMerchant(merchant, false);
        });
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
