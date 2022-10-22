package xyz.jpenilla.wanderingtrades.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import java.util.ArrayList;
import java.util.List;
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
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.command.BaseCommand;
import xyz.jpenilla.wanderingtrades.command.Commands;
import xyz.jpenilla.wanderingtrades.command.argument.TradeConfigArgument;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.InventoryFactory;

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
            .argument(TradeConfigArgument.of("config"))
            .permission("wanderingtrades.tradecommand")
            .handler(this::tradeConfig);
        this.commandManager.command(config);
        this.commandManager.command(config
            .permission("wanderingtrades.tradecommand.others")
            .argument(MultiplePlayerSelectorArgument.of("players")));

        final Command.Builder<CommandSender> natural = trade
            .literal("natural")
            .permission("wanderingtrades.tradenaturalcommand")
            .handler(this::tradeNatural);
        this.commandManager.command(natural);
        this.commandManager.command(natural
            .permission("wanderingtrades.tradenaturalcommand.others")
            .argument(MultiplePlayerSelectorArgument.of("players")));
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
            final Merchant merchant = InventoryFactory.createMerchant(player, Component.translatable("entity.minecraft.wandering_trader"));
            final List<MerchantRecipe> result = new ArrayList<>(trades);
            result.addAll(recipes);
            merchant.setRecipes(result);
            player.openMerchant(merchant, false);
        });
    }

    private static List<Player> players(final CommandContext<CommandSender> ctx) {
        @Nullable List<Player> players = ctx.<MultiplePlayerSelector>getOptional("players")
            .map(MultiplePlayerSelector::getPlayers)
            .orElse(null);
        if (players == null) {
            if (ctx.getSender() instanceof Player player) {
                players = List.of(player);
            } else {
                throw new InvalidCommandSenderException(ctx.getSender(), Player.class, null);
            }
        }
        return players;
    }
}
